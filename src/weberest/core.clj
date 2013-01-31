(ns weberest.core
  (:require [clojure.math.numeric-tower :as nt]
            [clojure.java.io :as cjio]
            clojure.set
            [incanter.core :as ic]
            [clojure.string :as cstr]
            [clojure.pprint :as pp]
            [clj-time.core :as ctc]
            [weberest 
             [datomic :as bd]
             [util :as bu]]
            [clojure-csv.core :as csv]
            [clojure.tools.macro :as ctm]
            [datomic.api :as d]
            #_[clojure.algo.generic.arithmetic :as caga]
            )
  (:use [weberest.helper :only [|-> |<- --< juxt*] :as bh]
        [let-else :only [let?]]
        [clojure.core.incubator :only [-?>]]
        [clojure.algo.generic.functor :only [fmap]]
        clojure.test))

;(frinj-init!)

(def ^{:berest/unit :cm} max-soil-depth 150)
#_(def layer-sizes (flatten [5 5 (repeat 19 10)]))
(def ^{:dynamic true :berest/unit :cm} *layer-sizes* [10 20 30 40 50])
(defn ^{:berest/unit :cm} layer-depths [] (reductions + *layer-sizes*))
(defn no-of-soil-layers [] (count *layer-sizes*))

(defn pFK->mm7x 
  "fc [mm/x] -> [mm/x]" 
  [fc percent-value] 
  (* 1/100 percent-value fc))

(defn pNFK->mm7x 
  "fc, pwp [mm/x] -> [mm/x]"
  [fc pwp percent-value]
  (+ pwp (* 1/100 percent-value (- fc pwp))))

(defn mm7dm->pNFK 
  "fc, pwp, mm-value [mm/x] -> [% NFK]" 
  [fc pwp sm-value] 
  (/ (- sm-value pwp) (* (- fc pwp) 100)))

(defn volp->mm7dm "value [volp] -> [mm/dm] -> " [value] value)

(defn mm7dm->volp "value [mm/dm] -> [volp]" [value] value)

(defn mm7dm->mm7cm "value [mm/dm] -> [mm/cm]" [value] (/ value 10))

(defn mm7cm->mm7dm "value [mm/cm] -> [mm/dm]" [value] (* value 10))

(def fout (ref []))
(def out (ref ""))

(defn append-out [out append-func value]
  (dosync 
    (alter out append-func value)))

(defn nFC [fc pwp] (- fc pwp)) 

(defn potential-evaporation-turc-wendling 
  [globrad-Jpcm2 tavg & {:keys [fk] :or {fk 1}}]
  (/ (* (+ globrad-Jpcm2 (* 93 fk)) (+ tavg 22)) (* 150 (+ tavg 123))))

(defn climatic-water-balance-turc-wendling 
  [precip-mm globrad-Jpcm2 tavg & {:keys [fk] :or {fk 1}}]
  (- precip-mm (potential-evaporation-turc-wendling globrad-Jpcm2 tavg :fk fk)))

(defn sorted-unzip-map 
  "split a map into it's keys and values by sorted keys, return [[keys][values]]"
  [map]
  ((--< keys vals) (into (sorted-map) map)))

(defn expand-layers 
  "(expand-fn value expanded-depth)" 
  [max-depth [depths values] & {:keys [expand-fn] :or {expand-fn (fn [val _] (identity val))}}]
  {:pre [(= (count depths) (count values))]}
  (let [;create pairs of upper and lower bounds
        bounds (partition 2 1 (into (sorted-set) (flatten [0 (take-while #(<= % max-depth) depths) max-depth])))
        ;create sizes out of the list of bounds
        depths* (map (fn [[l r]] (- r l)) bounds)]
    ;associate the according values to the depths just created
    (mapcat #(repeat %1 (expand-fn %2 %1)) depths* (flatten [values (last values)]))))

(defn aggregate-layers
  "aggregate layers according to given layer-sizes and reduce function reduce-fn"
  [reduce-fn layer-sizes layer-data]
  (->> (loop [res [] 
              sizes layer-sizes 
              data layer-data]
         (if (not (seq sizes)) 
           (if (seq data) 
             (lazy-cat res [data])
             res) 
           (let [size (first sizes)]
             (recur (lazy-cat res [(take size data)]) (rest sizes) (drop size data)))))
    (map (partial reduce reduce-fn) ,,,)))

(defn adjacent-kv-pairs [map key]
  {:lower (->> map 
               (filter #(< (first %) key) ,,,) 
               (into (sorted-map) ,,,) 
               last)
   :upper (->> map 
               (filter #(> (first %) key) ,,,) 
               (into (sorted-map) ,,,)
               first)})

(defn interpolated-value 
  "interpolate between the keys in map m for the given value key
  or if an exact match, just return the matched value in m
  m can also contain a map as values, in which case the values of that map
  are assumed to be numbers and treated as multiple results"
  [m key]
  (if (empty? m)
    nil
    (if-let [v (find m key)]
      (second v)
      ;split in lower and upper parts, lower parts
      ;will again include = to circumvent that clojure will
      ;compare for equality 20.0 = 20 => false (equivalence 20.0 == 20 => true), 
      ;but the find above will use equality and thus won't match integer and 
      ;floating point values
      (let [m-lower (->> m 
                         (filter #(<= (first %) key) ,,,)
                         (into (sorted-map) ,,,))
            m-upper (->> m 
                         (filter #(> (first %) key) ,,,)
                         (into (sorted-map) ,,,))]
        (cond
          (empty? m-lower) (-> m-upper first second)
          (empty? m-upper) (-> m-lower last second)
          :else (let [[lowerKey, lowerValue] (last m-lower)
                      [upperKey, upperValue] (first m-upper)
                      normalized-key (/ (- key lowerKey) 
                                        (- upperKey lowerKey))]
                     (if (every? map? [lowerValue upperValue])
                         ;the values are actually maps
                         (into (empty lowerValue) 
                               (map (fn [[lk lv] [uk uv]]
                                      [lk (+ lv (* normalized-key
                                                   (- uv lv)))])
                                    lowerValue upperValue))
                         ;the values are normal scalars
                         (+ lowerValue (* normalized-key 
                                          (- upperValue lowerValue))))))))))

(defn remove-namespace-from-keyword 
  "remove the namespace from the given keyword"
  [kw]
  (-> kw name keyword))

(defmulti remove-namespace-1 
  "remove namespace keywords collection just 1 level deep"
  class)

(defmethod remove-namespace-1
  clojure.lang.IPersistentMap [m]
  (reduce (fn [m [key value]]
            (if (keyword? key)
              (-> m
                (assoc ,,, (remove-namespace-from-keyword key) value)
                (dissoc ,,, key))
              m))
          m m))

(defn remove-namespace-from-keywords 
  "remove namespace from keywords in collection deeply"
  [collection]
  (clojure.walk/prewalk #(if (keyword? %) 
                           (remove-namespace-from-keyword %) 
                           %) 
                        collection))
                 
(defn abs-pre-calculation-day [plot] (dec (:plot/abs-calculation-day plot)))

(defn crop-id [crop] (str (:plot/number crop) "/" (:plot/cultivation-type crop) "/" (:plot/usage crop)))

(defn resulting-damage-compaction-depth-cm 
  [plot] 
  (if (> (:plot/damage-compaction-area plot) (* 0.5 (:plot/crop-area plot))) 
    (:plot/damage-compaction-depth plot) 300))

#_(defn awc-percent 
  "available water capacity in percent (nFK Prozent)"
  [from-layer to-layer soil-moisture fc pwp]
  (let [[sum-wc sum-awc] 
        (reduce (fn [[sum-wc sum-awc] i]
                  (let [[smi fci pwpi] ((--< soil-moisture fc pwp) i)]
                    [(+ sum-wc (- smi pwpi))
                     (+ sum-awc (nFC fci pwpi))]))
                [0. 0.] (range 0 (count soil-moisture)))]
    (max 0. (round (* 100.0 (/ sum-wc sum-awc))))))

(defn create-soil-moistures
  "create some interesting soil-moistures, function had been used in berest"
  [max-soil-depth layer-depths soil-moisture fc pwp]
  (let [;create expanded layers for all 3 inputs (1 cm layers)
        [sm fc pwp] (map #(expand-layers max-soil-depth [layer-depths %] :expand-fn /) 
                         [soil-moisture fc pwp])
        
        ;the two functions to calculate and sum a vector of pairs of values
        ;for water content and available water content
        sum-wc-fn (partial reduce #(+ %1 (apply - %2)) 0.)
        sum-awc-fn (partial reduce #(+ %1 (apply nFC %2)) 0.)
        
        ;the actual function calulating the percentage for a requested layer range
        f (fn [from to]
            (let [create-pairs (partial map vector)
                  sum-wc (-> (create-pairs sm pwp)
                           vec
                           (subvec ,,, from to)
                           sum-wc-fn)
                  sum-awc (-> (create-pairs fc pwp)
                            vec
                            (subvec ,,, from to)
                            sum-awc-fn)]
              (max 0. (Math/round (* 100.0 (/ sum-wc sum-awc))))))]
    {:pNFK_0-30cm (f 0 30)
     :pNFK_30-60cm (f 30 60) 
     :pNFK_0-60cm (f 0 60)}))

(defn lambda-correction 
  "return the lambda correction factor for the given day of year,
assuming right now the given day of year is without leap years"
  {:doc/origin "The expression part in the let is taken from: 
W. Mirschel et al./Ecological Modelling 81 (1995) 53-69, equation (13)
while the doy* binding is taken from the BOWET source code (the last part can be taken from
as well: 
Pascal code: 
IF(jahr[l] MOD 4 = 0) THEN BEGIN
  IF(tag > 305) THEN t:= tag - 305 ELSE t:= tag + 61;
END;
IF(jahr[l] MOD 4 > 0) THEN BEGIN
  IF(tag > 304) THEN t:= tag - 304 ELSE t:= tag + 61;
END;
{Die letzten 6 Anweisungen nur zur Anpassung an Programm verdtur2}
fakt:=1 + 0.77 * SIN(0.01571 * (t - 166));"}
  [doy]
  (let [doy* (if (> doy 304) 
               (- doy 304) 
               (+ doy 61))] 
    (+ 1 (* 0.77 (Math/sin (* 0.01571 (- doy* 166)))))))

(defn lambda-without-correction
  "create lambda without correction layer structure"
  [resulting-damage-compaction-depth-cm stt fcs-cm]
  (let [[first-30-cm below-30-cm] (map (partial apply array-map)
                                       #_[[fk-in-volP*10 lambda*100]]
                                       [[100 115, 140 68, 200 50, 240 33, 260 33, 
                                         300 25, 312 25, 327 17, 356 15, 1000 15]
                                        [90 115, 120 68, 180 50, 220 33, 250 33, 
                                         280 25, 344 25, 364 17, 373 15, 1000 15]])
        cm->dm #(/ % 10)
        mm7cm->volp (|-> mm7cm->mm7dm mm7dm->volp)]
    (->> fcs-cm
      ; create cm uncorrected lambda layers
      (map-indexed 
        (fn [layer-no fc]
          (/ (cond 
               (< resulting-damage-compaction-depth-cm (inc layer-no)) 1 
               (<= 1121 stt 1323) 100
               (<= 713 stt 723) 5
               :else (->> (if (< layer-no 30) 
                            first-30-cm
                            below-30-cm) 
                       (drop-while (fn [[fc*10 _]] (>= (* (mm7cm->volp fc) 10) fc*10)) ,,,)
                       first ;first element in rest of list 
                       second)) ;get lambda from pair
             100))
        ,,,)
      ; aggregate layers via choosing smallest lambda value in resulting layer
      (aggregate-layers min *layer-sizes* ,,,)
      ; divide by resulting layer-size squared in dm (to use the above dm values)
      (map #(/ %2 (* (cm->dm %) (cm->dm %))) *layer-sizes*) ,,,)))

(defn lambda
  "get layers with lambda values at a given day of year"
  [lambda-without-correction doy] 
  (map (partial * (lambda-correction doy)) lambda-without-correction)) 
          
#_(defrecord Donation [day amount])

(defn donations-at [donations day]
  (reduce
    (fn [acc d] (if (= day (:irrigation/abs-day d)) (+ acc (:irrigation/amount d)) acc))
    0 donations))

(comment
  (clojure.walk/prewalk-replace {:irrigation/abs-start-day :start
                                 :irrigation/abs-end-day :end
                                 :irrigation/area :area
                                 :irrigation/amount :amount}))

(defn db-read-irrigation-donations [db plot-no]
  (->> (d/q '[:find ?donation-e 
            :in $ ?plot-no 
            :where
            [?plot-e :plot/number ?plot-no]
            [?plot-e :plot/irrigation-water-donations ?donation-e]]
          db plot-no)
    (map #(-> %
            first
            (bd/get-entity ,,,))
         ,,,)))

(defn db-create-irrigation-donation 
  [datomic-connection plot-no abs-start-day abs-end-day area amount]
  (let [plot-id (ffirst (d/q '[:find ?plot :in $ ?plot-no 
                             :where [?plot :plot/number ?plot-no]] 
                           (d/db datomic-connection) plot-no))
        donation {:db/id (bd/new-entity-id),
                  :irrigation/abs-start-day abs-start-day
                  :irrigation/abs-end-day abs-end-day
                  :irrigation/area area
                  :irrigation/amount amount}
        plot {:db/id plot-id
              :plot/irrigation-water-donations (bd/get-entity-id donation)}]
    (d/transact datomic-connection (flatten [donation plot]))))

(defn read-irrigation-donations [db plot-no irrigation-area]
  (->> (db-read-irrigation-donations db plot-no)
    (filter #(and (>= (:irrigation/abs-end-day %) (:irrigation/abs-start-day %)) 
                  (> (:irrigation/area %) (* 0.5 irrigation-area))) 
            ,,,)
    (map #(assoc % :irrigation/abs-day (/ (+ (:irrigation/abs-start-day %) 
                                             (:irrigation/abs-end-day %)) 
                                          2))
         ,,,)))
    
(comment
  (def ds (list (:abs-day 1 :amount 20) (:abs-day 2 :amount 10) (:abs-day 1 :amount 30)))
  (println (donations-at ds 2))
  )

(def climate-data 
     (-> "private/db/klima-potsdam-2006.csv"
         cjio/resource 
         slurp
         csv/parse-csv))

(def weather-map
  (into (sorted-map) (map (fn [[day month year tavg precip globrad]]
                            (let [doy (bu/date-to-doy (Integer/parseInt day) 
                                                      (Integer/parseInt month))]
                              [doy {:doy doy 
                                    :evaporation (potential-evaporation-turc-wendling 
                                                   (Float/parseFloat globrad) (Float/parseFloat tavg))
                                    :precipitation (Float/parseFloat precip)
                                    :prognosis? false}]))
                          (rest climate-data))))

(defn longterm-evap-precip [doy]
  (let [longterm-average-evaporation-values 
        [#_"01.04." 1.1, 1.2, 1.2, 1.2, 1.3, 1.3, 1.3, 1.4, 1.4, 1.4,
         #_"11.04." 1.4, 1.5, 1.5, 1.5, 1.6, 1.6, 1.6, 1.7, 1.7, 1.7,
         #_"21.04." 1.8, 1.8, 1.8, 1.9, 1.9, 1.9, 2.0, 2.0, 2.0, 2.1,
         #_"01.05." 2.1, 2.1, 2.2, 2.2, 2.2, 2.3, 2.3, 2.3, 2.4, 2.4,
         #_"11.05." 2.4, 2.5, 2.5, 2.5, 2.6, 2.6, 2.6, 2.7, 2.7, 2.7,
         #_"21.05." 2.7, 2.8, 2.8, 2.8, 2.9, 2.9, 2.9, 2.9, 3.0, 3.0, 3.0,
         #_"01.06." 3.0, 3.1, 3.1, 3.1, 3.2, 3.2, 3.2, 3.2, 3.3, 3.3,
         #_"11.06." 3.3, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4,
         #_"21.06." 3.4, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4, 3.4,
         #_"01.07." 3.4, 3.4, 3.3, 3.3, 3.3, 3.3, 3.3, 3.3, 3.3, 3.3,
         #_"11.07." 3.3, 3.3, 3.3, 3.3, 3.3, 3.3, 3.3, 3.2, 3.2, 3.2,
         #_"21.07." 3.2, 3.2, 3.2, 3.1, 3.1, 3.1, 3.1, 3.1, 3.1, 3.1, 3.0,
         #_"01.08." 3.0, 3.0, 3.0, 3.0, 3.0, 2.9, 2.9, 2.9, 2.9, 2.9,
         #_"11.08." 2.9, 2.9, 2.8, 2.8, 2.8, 2.8, 2.7, 2.7, 2.7, 2.7,
         #_"21.08." 2.6, 2.6, 2.6, 2.6, 2.5, 2.5, 2.4, 2.4, 2.4, 2.4, 2.4,
         #_"01.09." 2.3, 2.3, 2.3, 2.2, 2.2, 2.2, 2.2, 2.1, 2.1, 2.1,
         #_"11.09." 2.0, 2.0, 2.0, 2.0, 1.9, 1.9, 1.9, 1.8, 1.8, 1.8,
         #_"21.09." 1.7, 1.7, 1.6, 1.6, 1.6, 1.5, 1.5, 1.5, 1.4, 1.4,
         #_"01.10." 1.4, 1.3, 1.3, 1.3, 1.2, 1.2, 1.2, 1.1, 1.1, 1.0,
         #_"11.10." 1.0, 1.0, 0.9, 0.9, 0.9, 0.7, 0.6, 0.7, 0.5, 0.8,
         #_"21.10." 1.0, 1.0, 0.9, 0.9, 0.9, 0.7, 0.6, 0.7, 0.5, 0.8]
        longterm-average-precipitation-values
        [#_"01.04." 1.0, 1.7, 0.6, 0.5, 1.1, 0.9, 0.9, 0.9, 1.9, 1.5,
         #_"11.04." 1.1, 0.8, 1.2, 1.5, 2.2, 0.9, 1.4, 1.1, 2.0, 0.9,
         #_"21.04." 0.7, 1.3, 0.9, 0.9, 0.4, 0.6, 0.9, 1.0, 2.0, 1.6,
         #_"01.05." 1.3, 1.1, 2.0, 1.5, 1.6, 1.8, 1.9, 1.3, 1.0, 1.3,
         #_"11.05." 4.2, 0.6, 1.6, 1.5, 1.3, 0.6, 0.9, 1.9, 1.4, 4.6,
         #_"21.05." 1.0, 0.9, 0.4, 0.9, 2.7, 1.0, 3.6, 2.8, 0.7, 2.2, 2.3,
         #_"01.06." 1.1, 2.2, 0.6, 1.3, 1.0, 0.8, 0.7, 2.7, 4.4, 3.5,
         #_"11.06." 2.0, 6.0, 1.3, 1.0, 1.8, 1.9, 1.5, 1.0, 3.3, 1.5,
         #_"21.06." 1.9, 2.8, 0.7, 0.6, 3.6, 2.4, 4.1, 3.3, 3.5, 1.9,
         #_"01.07." 1.6, 1.5, 1.9, 3.0, 3.4, 1.9, 1.1, 0.9, 2.5, 1.2,
         #_"11.07." 1.3, 2.2, 1.5, 1.0, 2.5, 2.0, 1.9, 3.4, 1.1, 4.3,
         #_"21.07." 3.6, 3.7, 3.6, 1.5, 0.9, 1.4, 2.1, 1.0, 1.4, 1.2, 0.9,
         #_"01.08." 0.8, 0.5, 2.4, 1.7, 1.0, 1.3, 0.8, 1.7, 1.9, 1.3,
         #_"11.08." 2.0, 1.7, 1.4, 0.3, 2.3, 1.7, 2.8, 1.1, 1.1, 3.1,
         #_"21.08." 1.6, 2.9, 1.2, 1.4, 2.6, 1.4, 2.4, 3.2, 4.0, 1.6, 0.6,
         #_"01.09." 2.1, 0.5, 0.3, 1.0, 1.4, 1.6, 3.1, 1.8, 2.6, 2.3,
         #_"11.09." 2.9, 1.0, 1.2, 1.9, 0.6, 2.0, 1.8, 1.1, 0.7, 1.2,
         #_"21.09." 0.6, 1.5, 0.6, 2.3, 1.2, 0.9, 0.6, 2.2, 2.3, 1.0,
         #_"01.10." 0.6, 0.8, 2.5, 0.4, 0.7, 0.5, 2.1, 0.5, 1.1, 2.4,
         #_"11.10." 0.8, 0.2, 0.9, 1.6, 1.0, 2.5, 1.7, 1.6, 1.5, 1.0,
         #_"21.10." 2.2, 2.9, 1.8, 1.4, 1.2, 0.6, 1.3, 2.0, 0.4, 1.9]]
    (if (and (< 90 doy) (<= doy (+ 90 213)))
      (let [index (- doy 90 1)]
        {:evaporation (nth longterm-average-evaporation-values index),
         :precipitation (nth longterm-average-precipitation-values index)})
      {:evaporation 0
       :precipitation 0})))

(defn weather-at [map doy]
  (if-let [v (find map doy)]
    (second v)
    (let [{:keys [evaporation precipitation]} (longterm-evap-precip doy)]
      {:doy doy 
       :evaporation evaporation 
       :precipitation precipitation 
       :prognosis? true})))

;type CodeEinheit = | PFK | PNFK | Volp | MM
;:PFK :PNFK :Volp :MM

;type IrrigationMode = | SprinkleLosses | NoSprinkleLosses
;:Irrigation-mode :Sprinkle-losses :No-sprinkle-losses

(defn user-input-fc-or-pwp-to-cm-layers 
  "convert the user input field capacity or permanent wilting point to cm layers
e.g. {30 17.7, 60 13.7, 150 15.7}
user-input-soil-data [volP | mm/dm] -> [mm/cm]"
  [user-input-soil-data]
  (->> user-input-soil-data
    sorted-unzip-map 
    (expand-layers max-soil-depth ,,,)
    (map (|-> volp->mm7dm mm7dm->mm7cm) ,,,)))

(defn user-input-soil-moisture-to-cm-layers
  "convert the user input soil-moisture into the internally defined layer structure
e.g. {30 3.4, 60 2.9, 150 3.8} :pFK
fc, pwp [mm/cm] -> [mm/cm]"
  [fcs-cm pwps-cm soil-moisture-unit user-input-soil-moistures]
  (->> user-input-soil-moistures
    sorted-unzip-map 
    (expand-layers max-soil-depth ,,,)
    (map (soil-moisture-unit {:pFK #(pFK->mm7x %1 %3) 
                              :pNFK pNFK->mm7x  
                              :volP #(-> %3 
                                       volp->mm7dm 
                                       mm7dm->mm7cm)})
         fcs-cm pwps-cm ,,,)))

(defn db-crop-data-map->crop
  "transform the crop data from datomic to crop map"
  [db-crop-data-map]
  (reduce (fn [m [relation-kw key-kw value-kw]]
            (assoc m relation-kw 
                   (bd/create-map-from-entities key-kw value-kw (relation-kw m))
                   #_(bd/create-map-from-entity-ids key-kw value-kw (relation-kw m))))
          db-crop-data-map
          [[:crop/dc-to-rel-dc-days :kv/dc :kv/rel-dc-day]
           [:crop/dc-to-developmental-state-names :kv/dc :kv/name]
           [:crop/rel-dc-day-to-cover-degrees :kv/rel-dc-day :kv/cover-degree]
           [:crop/rel-dc-day-to-extraction-depths :kv/rel-dc-day :kv/extraction-depth]
           [:crop/rel-dc-day-to-transpiration-factors :kv/rel-dc-day :kv/transpiration-factor]
           [:crop/rel-dc-day-to-quotient-aet-pets :kv/rel-dc-day :kv/quotient-aet-pet]]))

(defn db-create-crop
  "create crop from give db crop-id"
  [db crop-id]
  (db-crop-data-map->crop (into {} (bd/get-entity db crop-id))))

(defn db-read-crop 
  "read a crop from the database by the given number and optional cultivation type and usage"
  [db number & {:keys [cultivation-type usage] :or {cultivation-type 1 usage 0}}]
  (let [crop-id (ffirst (d/q '[:find ?crop-id 
                               :in $ ?no ?ct ?u 
                               :where
                               [?crop-id :crop/number ?no]
                               [?crop-id :crop/cultivation-type ?ct]
                               [?crop-id :crop/usage ?u]]
                             db number cultivation-type usage))]
    (db-create-crop db crop-id)))


#_(defn replace-entities 
  "replaces in map m entity-ids referenced by the given entity keywords 
with their respective entities recursively
- entity-kws should be of form 
- [:keyword1 :keyword2 ...] or
- [:keyword1 :keyword2 [:sub-keyword2-1 :sub-keyword2-2] ...]
- [:keyword1 {:keyword2 replace-function-for-keyword2} ...], thus
a keyword can be replaced by a map of one pair mapping keyword to a function able to 
replace the keyword by its entity, else the default function db/get-entity will be used"
  [m entity-kws]
  (->> entity-kws
    ;create pairs of keywords and lists of possible sub-keywords to replace
    (partition 2 1 [:nothing] ,,,)
    ;remove sub-keywords at first place (but keep maps with custom replace/load function)
    (remove (fn [[first _]] (and (coll? first) (not (map? first)))) ,,,)
    ;transform single keyword at second place to empty sub-keyword list
    (map (fn [[first second]] [first (if (keyword? second) [] second)]))
    ;transform recursively entity-ids to sub-maps (should only be very shallow, so no stack-overflow)
    (reduce (fn [m [e-kw?f sub-e-kws]]
              (let [;get actual keyword and possibly associated replace function (or default)
                    [e-kw func] (if (map? e-kw?f) 
                                  (first e-kw?f) 
                                  [e-kw?f db/get-entity]) 
                    ;always create collection of ids (even if just one is returned)
                    e-id?s (e-kw m)
                    e-ids (if (coll? e-id?s) e-id?s [e-id?s]) 
                    ;get the maps according to the ids from previous step
                    e-maps (map func e-ids) 
                    ;possibly sub-replace sub-keywords in all returned maps
                    e-maps* (map #(replace-entities % sub-e-kws) e-maps)]
                ;and replace entity-id by entity
                (assoc m e-kw (if (= (count e-maps*) 1) (first e-maps*) e-maps*)))) 
            m
            ,,,)))

(defn entity->map 
  [db entity]
  (letfn [(copy [value] (if (= datomic.query.EntityMap (type value))
                          (entity->map db (bd/get-entity db (:db/id value)))
                          value))]
    (into {} (for [[key value?s] entity]
               [key (if (set? value?s)
                      (map copy value?s)
                      (copy value?s))]))))

(defn db-read-plot [db plot-no]
  (let? []
    (let? [plot-id (ffirst (d/q '[:find ?plot 
                                  :in $ ?plot-no 
                                  :where
                                  [?plot :plot/number ?plot-no]]
                                db plot-no))
           :else nil
           
           plot (bd/get-entity db plot-id)
           
           ; abbreviation
           cmfe 
           #_#(bd/create-map-from-entity-ids %1 %2 %3)
           #(bd/create-map-from-entities %1 %2 %3)
           
           fcs-cm (->> (:plot/field-capacities plot)
                    (cmfe :soil/upper-boundary-depth :soil/field-capacity ,,,)
                    user-input-fc-or-pwp-to-cm-layers)
           fcs (->> fcs-cm
                 (aggregate-layers + *layer-sizes* ,,,))
           
           pwps-cm (->> (:plot/permanent-wilting-points plot)
                     (cmfe :soil/upper-boundary-depth :soil/permanent-wilting-point ,,,)
                     user-input-fc-or-pwp-to-cm-layers)
           pwps (->> pwps-cm
                  (aggregate-layers + *layer-sizes* ,,,))
           
           sms (->> (:plot/initial-soil-moistures plot) 
                 (cmfe :soil/upper-boundary-depth :soil/soil-moisture ,,,)
                 (user-input-soil-moisture-to-cm-layers 
                   fcs-cm pwps-cm (->> (:plot/initial-sm-unit plot)
                                    remove-namespace-from-keyword)
                   )
                 (aggregate-layers + *layer-sizes* ,,,))
           
           lwc (lambda-without-correction 
                 (resulting-damage-compaction-depth-cm plot)
                 (:plot/stt plot)
                 fcs-cm)
           
           ;read a fallow "crop" to be used with this plot
           fallow (db-read-crop db 0 :cultivation-type 1 :usage 0) ]
      
      (-> (entity->map db plot)
        #_(replace-entities ,,, [:plot/technology 
                                 :plot/dc-assertions [:assertion/crop]])
        #_(replace-entities ,,, [:plot/dc-assertions [:assertion/crop [{:crop/template db-create-crop}]]])
        ((fn [p] (clojure.walk/postwalk (fn [item] 
                                          (if (vector? item)
                                            (let [[kw db-crop-data-map] item]
                                              (if (= kw :crop-instance/template)
                                                [kw (db-crop-data-map->crop db-crop-data-map)]
                                                item))
                                            item)) 
                                        p)) ,,,)
        (assoc ,,, :plot/initial-soil-moistures sms
                   :plot/field-capacities fcs
                   :plot/permanent-wilting-points pwps
                   :lambda-without-correction lwc
                   :fallow fallow)))))

(defn db-store-initial-soil-moisture 
  [datomic-connection plot-no depths soil-moistures units]
  (let [plot-id (ffirst (d/q '[:find ?plot :in $ ?plot-no 
                             :where [?plot :plot/number ?plot-no]] 
                           (d/db datomic-connection) plot-no))
        entities (bd/create-entities {:soil/upper-boundary-depth depths
                                      :soil/soil-moisture soil-moistures 
                                      :soil/soil-moisture-unit units})
        plot {:db/id plot-id
              :plot/user-soil-data (bd/get-entity-ids entities)}]
    (d/transact datomic-connection (flatten [entities plot]))))


(defn glugla
  "wie = WiE = Wassergehalt am Ende des Zeitabschnittes i (mm) = finalExcessWater"
  [wia nist lai]
  ;more water will fit into the current layer (below infiltration barrier)
  (cond
    (< wia 0.) (let [wie (+ nist wia)]
                (if (and (>= nist 0.) (> wie 0.))
                  (let [b2 (- (Math/exp (* -2 (Math/sqrt (* lai nist)) (+ 1 (/ wia nist)))))]
                    (/ (* (Math/sqrt (/ nist lai)) (+ 1 b2)) 
                       (- 1 b2)))
                  wie))
    ;current layer already above infiltration barrier
    :else (cond
            (= nist 0.) (/ wia (+ 1 (* lai wia)))
            ;Entzug
            (< nist 0.)(let [n7l (Math/sqrt (/ (- nist) lai))
                            nl (Math/sqrt (* (- nist) lai))
                            at (/ (Math/atan (/ wia n7l)) 
                                  nl)]
                        (if (<= at 1.)
                          (* nist (- 1 at))
                          (let [beta (/ (Math/sin nl) (Math/cos nl))]
                            (/ (* n7l (- wia (* n7l beta))) 
                               (+ n7l (* wia beta))))))
            ;if nist > 0 // kein Entzug
            :else (let [n7l (Math/sqrt (/ nist lai))
                        nl (Math/sqrt (* lai nist))
                        alpha (* (/ (- wia n7l) 
                                    (+ wia n7l)) 
                                 (Math/exp (* -2 nl)))]
                    (/ (* n7l (+ 1 alpha)) 
                       (- 1 alpha))))))

(defn interception [precipitation evaporation irrigation transpiration-factor irrigation-mode]
  (let [null5 0.5
        null2 0.2
        tf (max 1 transpiration-factor)
        
        ;Berechnung fuer natuerlichen Regen
        tin (+ null5 (* (- evaporation 1) tf null2))
        [interception-precipitation, pet] (if (> precipitation 0) 
                                            [tin, (- evaporation (* tin null5))]
                                            [0, evaporation])
        
        ;Berechnung fuer Zusatzregen/Spruehverluste
        [interception-irrigation, 
         sprinkle-loss, 
         pet*] (if (> irrigation 1)
                 (let [[ii, sl]
                       (condp = irrigation-mode
                         :sprinkle-losses [(* 0.6 tin (+ 1 (* irrigation 0.05))),
                                           (* (+ 1 (* (- evaporation 2.5) null2)) null2 irrigation)]
                         :no-sprinkle-losses [0, 0])]
                   (if (> precipitation 0)
                     [ii, sl, (- evaporation (* (+ ii interception-precipitation) null5))]
                     [ii, sl, (- evaporation (* ii 0.75))]))
                 [0, 0, pet])]
    {:pet (max 0 pet*),
     :effective-precipitation (- precipitation interception-precipitation),
     :effective-irrigation (- irrigation interception-irrigation sprinkle-loss),
     :effective-irrigation-uncovered (- irrigation sprinkle-loss)}))

(defn uncovered-water-abstraction-fraction
  "Get fraction of water-abstraction on uncovered soil for given layer i when using maximal m equal sized layers
the function will use by default the curvature parameter z with value 0.05 which fits best the value for the
first two layers in the original Berest table [:0-10cm 0.625, :10-20cm 0.3, :30-60cm 0.075].
The third layer (30-60cm) isn't matched exactly, but it's the layer with the smallest water-abstraction
and very close (0.08145 vs 0.075), but the integral over the all the layers is nevertheless 1 and maybe
the original values have been choosen to get rounded but close to the functions results values."
  [m i & {:keys [z] :or {z 0.05}}]
  {:doc/origin "R. Koitzsch, Zeitschrift für Meteorologie Band 27 Heft 5,
Schätzung der Bodenfeuchte mit einem Mehrschichtenmodell, equation (9)"}
  (/ (- (* (+ z 1) (Math/log (/ (+ (* m z) i) 
                                (+ (* m z) i -1)))) 
        (/ 1 m)) 
     (- (* (+ z 1) (Math/log (/ (+ z 1) 
                                z))) 
        1)))

(defn covered-water-abstraction-fraction
  "Get fraction of water-abstraction on plant covered soil for given layer i when using maximal n equal sized layers."
  [n i]
  {:doc/origin "R. Koitzsch, Zeitschrift für Meteorologie Band 27 Heft 5,
Schätzung der Bodenfeuchte mit einem Mehrschichtenmodell, equation (6)"}
  (/ (- 2 (/ (- (* 2 i) 1) 
             n)) 
     n))

(defn complement-layers [no-of-layers with-value incomplete-layers]
  (take no-of-layers (concat incomplete-layers (repeat with-value))))

(defn f1-koitzsch 
  "returns a sequence of soil-depth-cm layers with water-abstraction values for covered or uncovered case
for the given maximum depth in cm"
  [max-depth-cm covered?]
  (let [f (if covered? 
            covered-water-abstraction-fraction 
            uncovered-water-abstraction-fraction)
        upper-values (for [i (range 1 (inc max-depth-cm))] 
                       (f max-depth-cm i))]
    (complement-layers max-soil-depth 0 upper-values)))

(defn gi-koitzsch 
  "calculate water abstraction for a given maximum extraction depth and the give
reduction factors
note: code still contains the uncovered case, but that code has actually moved
one level up to the caller of gi-koitzsch, because the extraction depth doesn't make
much sense for uncovered soil and has been originally defined to be 60cm, which is 
constant throughout the program, whereas in the uncovered case the there is a maximum
rooting (extraction) depth which can be fully used, but will only be so, if 
given the reduction factors applied to the layers gives the the largest abstraction, else
shallower extraction depth will be used"  
  [extraction-depth-cm reduction-factors & {:keys [covered?] :or {covered true}}]
  (let [;we search within the layers above and equal to extraction-depth-cm
        search-layer-depths (take-while (partial >= extraction-depth-cm) (layer-depths))
        ;create a list of [f1-koitzsch-result] for every possible layer depth in use
        ffs (for [max-depth search-layer-depths] 
              (->> (f1-koitzsch max-depth true)
                 (aggregate-layers + *layer-sizes* ,,,)))
        ;calculate the gj and store the sum of all layers to find out the maximum later 
        gis (map (fn [depth-cm, ff]
                   (let [gj (ic/mult reduction-factors ff)
                         rij (ic/sum gj)]
                     {:depth-cm depth-cm, :rij rij, :gj gj})) search-layer-depths ffs)
        ;sort gis according to ascending rij and then descending depth-cm
        gis-sorted (sort-by identity 
                            (fn [{l-rij :rij, l-depth :depth-cm} {r-rij :rij, r-depth :depth-cm}]
                              (if (= l-rij r-rij) (> l-depth r-depth) (> l-rij r-rij)))
                            gis)]
    ;just use values only below 50cm, take the first one and extract the gj and complement with zeros below extraction depth
    (->> gis-sorted
      first
      :gj
      (complement-layers (no-of-soil-layers) 0 ,,,))))

(defn capillary-rise-barrier 
  "calculate capillary rise barrier for a layer with size x-dm based on the field capacity of a x-dm layer"
  [fc-xdm layer-size-dm] 
  (let [fc-1dm (/ fc-xdm layer-size-dm)] 
    (* (+ fc-1dm 40.667 (- (* fc-1dm 0.408)))
       layer-size-dm)))

(defn infiltration-barrier [fk pwp abs-current-day layer-depth-cm]
  (let [barriers (if (<= layer-depth-cm 30) 
                   (sorted-map 80 11, 140 7) 
                   (sorted-map 100 10, 200 8))
        vs (* (interpolated-value barriers abs-current-day) 0.1) 
        vs* (let [pwfk (+ (/ pwp fk) -1 vs)]  
              (if (and (< vs 1.) (> pwfk 0.))
                (+ vs (/ (* (- 1 vs) pwfk 0.95) 
                         (- 0.66 0.3)))
                vs))]
    (+ (* (nFC fk pwp) vs*) pwp)))

(defn pwp4p [fc pwp] (- pwp (* 0.04 (nFC fc pwp)))) 

(defn uncovered-reduction-factors [fcs pwps soil-moistures] 
  (map (fn [fc pwp sm]  
         (let [pwp4p* (pwp4p fc pwp)
               r (if (> sm pwp4p*) 
                   (min (/ (- sm pwp4p*) (- fc pwp4p*)) 1) 
                   0)]
           (* r r))) 
       fcs pwps soil-moistures))
  
(defn covered-reduction-factors [extraction-depth-cm fcs pwps soil-moisture pet] 
  (map (fn [layer-size-cm depth-cm fc pwp sm]
         (let [pwp4p* (pwp4p fc pwp)]
           (if (and (<= depth-cm extraction-depth-cm) (< sm fc))
             (let [nfc (nFC fc pwp)
                   fc-dm (/ fc layer-size-cm 10)
                   xsm (if (or (< fc-dm 10) (> fc-dm 46))
                         (+ (* nfc 0.81) pwp)
                         (+ (* (+ 67.77
                                  (* 3.14 fc-dm)
                                  (* -0.2806 fc-dm fc-dm)
                                  (* 0.008131 fc-dm fc-dm fc-dm)
                                  (* -0.0000735 fc-dm fc-dm fc-dm fc-dm)) 
                               nfc 0.01) 
                            pwp))
                   rk (if (> pet 3) 
                        (* (- fc xsm) 0.0666) 
                        (* (- xsm pwp) 0.3333))
                   sm-crit (+ xsm (* (- pet 3) rk))]
               (if (< sm sm-crit) 
                 (let [pwp12p (- pwp (* 0.12 nfc))]
                   (cond 
                     (< pwp sm) (/ (- sm pwp12p) 
                                   (- sm-crit pwp12p))
                     (< pwp4p* sm) (let [sreb 3] ; { sreb=(pwp-pw7)/(pwp-pw9)=3.0 }
                                     (* (/ (- sm pwp4p*) 
                                           (- sm-crit pwp12p)) 
                                        sreb))
                     :else 0))
                 1))
             1)))
       *layer-sizes* (layer-depths) fcs pwps soil-moisture))

(defn sm-1-day [extraction-depth-cm cover-degree pet abs-current-day 
                 fcs pwps lambdas soil-moistures soil-moisture-prognosis? 
                 evaporation ivd groundwater-level daily-precipitation-and-irrigation]
   (let [sms+surface-water (concat [(+ daily-precipitation-and-irrigation (first soil-moistures))] 
                                   (rest soil-moistures))
         
         ;for at least partly uncovered ground
         water-abstractions (if (>= cover-degree 99/100)
                              (repeat (no-of-soil-layers) 0)
                              ;calculate same as gi-koitzsch for a depth of 60cm but uncovered soil
                              (->> (f1-koitzsch 60 false)
                                (aggregate-layers + *layer-sizes* ,,,)
                                (ic/mult evaporation 
                                         (uncovered-reduction-factors fcs pwps sms+surface-water)
                                         ,,,)))
         
         ;for at least partly covered ground
         [aet, water-abstractions*] 
         (if (<= cover-degree 1/100) 
           [0, water-abstractions]
           (let [extraction-depth-cm* (if soil-moisture-prognosis?
                                        (min extraction-depth-cm 60)
                                        extraction-depth-cm) 
                 rfs (if (> cover-degree 1/100)
                       (covered-reduction-factors extraction-depth-cm fcs pwps sms+surface-water pet)
                       (repeat (no-of-soil-layers) 1))
                 gi (gi-koitzsch extraction-depth-cm* rfs)] 
             [(ic/sum (ic/mult pet gi)), 
              (ic/plus (ic/mult (- 1 cover-degree) water-abstractions) 
                       (ic/mult pet cover-degree gi))]))
                  
         {groundwater-infiltration :infiltration-into-next-layer
          sms* :soil-moistures}   
         (->> soil-moistures
           ;combine a few more needed inputs to infiltration calculation
           (map vector lambdas water-abstractions *layer-sizes* (layer-depths) fcs pwps ,,,)
           ;calculate the infiltration top down layer by layer, transporting excess water down
           ;and building up the soil layers as we go down 
           (reduce (fn [{infiltration-from-prev-layer :infiltration-into-next-layer
                         sms :soil-moistures} [lambda water-abstraction layer-size-cm depth-cm fc pwp sm]] 
                     (let [cr-barrier (capillary-rise-barrier fc (/ layer-size-cm 10))]
                       (if (<= depth-cm groundwater-level)
                         (let [;above that barrier the water will start to infiltrate to the next layer
                               inf-barrier (infiltration-barrier fc pwp abs-current-day depth-cm)
                               
                               ;in the next steps we basically care just about the excess water
                               ;not about the layers full water content, so we calculate
                               ;just the difference (positive or negative) to the infiltration barrier
                               ;(in case of the first layer, possibly including todays precipitation)
                               initial-excess-water (- sm inf-barrier)
                               
                               net-infiltration-from-above-layer (- infiltration-from-prev-layer water-abstraction)
                               
                               ;the excess water after calculation of the infiltration to the next layer
                               final-excess-water (glugla initial-excess-water net-infiltration-from-above-layer lambda)
                               
                               ;calculate the resulting infiltration for the next layer from this layer
                               infiltration-into-next-layer (+ initial-excess-water net-infiltration-from-above-layer 
                                                               (- final-excess-water))
                               
                               ;add the (positive/negative) excess water back to the infiltration barrier
                               ;to obtain the actual water content now in this layer
                               ;(after water could infiltrate to the next layer)
                               pwp4p (- pwp (* 0.04 (nFC fc pwp)))
                               sm* (max (+ inf-barrier final-excess-water) pwp4p)]
                           {:infiltration-into-next-layer infiltration-into-next-layer,
                            :soil-moistures (conj sms sm*)})
                         {:infiltration-into-next-layer 0, 
                          :soil-moistures cr-barrier}))) 
                   {:infiltration-into-next-layer daily-precipitation-and-irrigation
                    :soil-moistures []}
                   ,,,))
         
         ;calculate the capillary rise if applicable
         [_ soil-moistures*] (->> sms*
                               ;combine soil-moistures with fc and *layer-sizes* for reduce function below
                               (map vector fcs *layer-sizes* ,,,)
                               ;reverse, to go from bottom to top layer
                               reverse 
                               ;transport excess water from lower layers to top layers
                               (reduce (fn [[excess-water sms] [fc layer-size-cm sm]] 
                                         (let [cr-barrier (capillary-rise-barrier fc (/ layer-size-cm 10))
                                               sm* (+ sm excess-water)
                                               excess-water* (max 0 (- sm* cr-barrier))]
                                           [excess-water* (cons (- sm* excess-water*) sms)]))
                                       [0 '()]
                                       ,,,))]
     {:aet aet 
      :soil-moistures soil-moistures* 
      :groundwater-infiltration groundwater-infiltration}))     

(defn calc-soil-moisture [{:keys [qu-sum-deficits qu-sum-targets soil-moistures]}
                          {:keys [abs-day rel-dc-day crop irrigation-amount
                                  evaporation precipitation irrigation-mode
                                  cover-degree qu-target rounded-extraction-depth-cm
                                  transpiration-factor
                                  fcs pwps lambdas groundwaterlevel-cm damage-compaction-depth-cm
                                  sm-prognosis?]}]
  (let [{:keys [pet 
                effective-precipitation 
                effective-irrigation 
                effective-irrigation-uncovered]} 
        (interception precipitation evaporation irrigation-amount 
                      transpiration-factor irrigation-mode)
        
        pet* (if (< cover-degree 1/1000) 
               0 
               (* pet transpiration-factor))
        
        daily-precipitation-and-irrigation 
        (+ (* (+ effective-precipitation effective-irrigation) cover-degree) 
           (* (+ precipitation effective-irrigation-uncovered) (- 1 cover-degree)))

        {aet :aet
         soil-moistures* :soil-moistures
         groundwater-infiltration :groundwater-infiltration} 
        (sm-1-day rounded-extraction-depth-cm cover-degree pet* 
                  abs-day fcs pwps lambdas soil-moistures
                  sm-prognosis? evaporation damage-compaction-depth-cm 
                  groundwaterlevel-cm daily-precipitation-and-irrigation)
                        
        aet7pet (cond 
                  (< cover-degree 1/1000) 0
                  (> pet* 1/100) (/ aet pet*)
                  :else 1)]
        {:abs-day abs-day
         :rel-dc-day rel-dc-day
         :aet aet
         :pet pet*
         :aet7pet aet7pet
         :qu-target qu-target
         :qu-deficit (if (< aet7pet qu-target) 
                       (- qu-target aet7pet) 
                       0)
         :qu-sum-deficits (if (< aet7pet qu-target) 
                            (+ qu-sum-deficits (- qu-target aet7pet)) 
                            qu-sum-deficits)
         :qu-sum-targets (+ qu-sum-targets qu-target)
         :soil-moistures soil-moistures* 
         :groundwater-infiltration groundwater-infiltration}))
 
(defn abs-search-dc-day 
  "calculate the interpolated! absolute search-dc-day given the dc-assertion and 
given search-dc in the dc curve"
  [dc-to-rel-dc-day {:keys [assert-dc abs-assert-dc-day] :as dc-assertion} search-dc]
  (let [rel-assert-dc-day (interpolated-value dc-to-rel-dc-day assert-dc)
        rel-search-dc-day (interpolated-value dc-to-rel-dc-day search-dc)]
    (+ abs-assert-dc-day (- rel-search-dc-day rel-assert-dc-day))))

(defn search-dc-data 
  "calulate both of the interpolated! search-dc data given the dc-assertion and 
given absolute search-dc-day in the dc curve, 
return a map of the search-dc and the relative search-dc-day belonging to it"
  [dc-to-rel-dc-days {:keys [assert-dc abs-assert-dc-day] :as dc-assertion} abs-search-dc-day]
  (let [rel-assert-dc-day (interpolated-value dc-to-rel-dc-days assert-dc)
        rel-search-dc-day (+ rel-assert-dc-day (- abs-search-dc-day abs-assert-dc-day))]
    {:rel-search-dc-day rel-search-dc-day
     :search-dc (interpolated-value (clojure.set/map-invert dc-to-rel-dc-days) rel-search-dc-day)}))

(defn rel-search-dc-day 
  "calculate the interpolated! rel-search-dc-day given the dc-assertion and
given the absolute search-dc-day in the dc curve"
  [dc-to-rel-dc-days {:keys [assert-dc abs-assert-dc-day] :as dc-assertion} abs-search-dc-day]
  (:rel-search-dc-day (search-dc-data dc-to-rel-dc-days dc-assertion abs-search-dc-day)))

(defn rel-search-dc 
  "calculate the interpolated! search-dc given the dc-assertion and
given the absolute search-dc-day in the dc curve"
  [dc-to-rel-dc-days {:keys [assert-dc abs-assert-dc-day] :as dc-assertion} abs-search-dc-day]
  (:search-dc (search-dc-data dc-to-rel-dc-days dc-assertion abs-search-dc-day)))

(defn reached-dc-data 
  "returns a map with the reached DC state at the abs-search-day and
to which actual abs-day the reached DC state belongs given the dc-assertions"
  [dc-to-rel-dc-days {:keys [assert-dc abs-assert-dc-day] :as dc-assertion} abs-search-day]
  (if (seq dc-to-rel-dc-days)
    (let [;get the relative dc day for the asserted DC state from the curve
          rel-assert-dc-day (Math/round (interpolated-value dc-to-rel-dc-days assert-dc))
          
          ;knowing the distance between the absolute assert-dc-day and the search-dc-day
          ;we can calculate the relative search-day, which can be used to check 
          ;the curve for the last reached DC state
          rel-search-day (+ rel-assert-dc-day (- abs-search-day abs-assert-dc-day))
                    
          ;reverse curve map to get the dc milestone just before (or exactly at) 
          ;the relative search-day
          ;first inverse the map
          rel-dc-day-to-dcs (into (sorted-map) (clojure.set/map-invert dc-to-rel-dc-days))
          
          ;find the reached DC state
          [rel-reached-dc-day 
           reached-dc] (if-let [reached (->> rel-dc-day-to-dcs
                                          (take-while #(-> %
                                                         first
                                                         (<= ,,, rel-search-day))
                                                      ,,,)
                                          last)]
                         reached
                         (first rel-dc-day-to-dcs))]
      {:reached-dc reached-dc 
       ;calculate the abs-day belonging to the reached-dc, by
       ;getting the delta between the known relative curve-dc-day and the newly calculated
       ;relative reached-dc-day and adding this delta to the (only) known absolute 
       ;asserted dc-day
       :abs-reached-dc-day (+ abs-assert-dc-day (- rel-reached-dc-day rel-assert-dc-day))})))

#_(defn- sorted-crop-start-data 
  "create sorted map of crop start data from dc-assertions and possible absolute minimal start day
-> map will be sorted according to abs-start-day and then abs-assert-dc-day"
  [dc-assertions abs-min-start-day]
  (into (sorted-map-by <-start-day=-<-assert-day-comparator)
        (for [dca dc-assertions 
              :let [{:keys [crop assert-dc abs-assert-dc-day] :as dc-assertion} 
                    (remove-namespace-1 dca)]]
          (let [dc-to-rel-dc-days (-> crop :crop/dc-to-rel-dc-days)
                
                ;absolute start day
                abs-start-day (-> dc-to-rel-dc-days
                                (abs-search-dc-day ,,, dc-assertion
                                                       1)
                                (max ,,, abs-min-start-day))
                
                ;relative start day in crop curve according to absolute day for current crop
                rel-start-dc-day (rel-search-dc-day dc-to-rel-dc-days 
                                                    dc-assertion
                                                    abs-start-day)] 
            [[abs-start-day abs-assert-dc-day] {:abs-start-day abs-start-day
                                                :rel-start-dc-day rel-start-dc-day
                                                :crop crop}]))))

#_(defn- sorted-crop-start-data 
  "create sorted seq of crop start data from dc-assertions and possible absolute minimal start day
the seq will be sorted according to descending abs-assert-dc-day"
  [dc-assertions abs-min-start-day]
  (->> (for [dca dc-assertions 
             :let [{:keys [crop assert-dc abs-assert-dc-day] :as dc-assertion} 
                   (remove-namespace-1 dca)]]
         (let [dc-to-rel-dc-days (-> crop :crop/dc-to-rel-dc-days)
               
               ;absolute start day
               abs-start-day (-> dc-to-rel-dc-days
                               (abs-search-dc-day ,,, dc-assertion
                                                      1)
                               (max ,,, abs-min-start-day))
               
               ;relative start day in crop curve according to absolute day for current crop
               rel-start-dc-day (rel-search-dc-day dc-to-rel-dc-days 
                                                   dc-assertion
                                                   abs-start-day)] 
           {:abs-start-day abs-start-day
            :abs-assert-dc-day abs-assert-dc-day
            :rel-start-dc-day rel-start-dc-day
            :crop crop}))
    (sort-by :abs-assert-dc-day > ,,,)))

#_(def ^:private sorted-crop-start-data* 
  "memoized version of sorted-crop-start-data"
  (memoize sorted-crop-start-data))

#_(def ^:private sorted-crop-start-data* 
  "memoized version of sorted-crop-start-data"
  (memoize sorted-crop-start-data))

#_(defn crop-start-data-at-abs-day 
  "get crop start data from dc-assertions for given abs-day
(might involve corrections for start data for multiple dc assertions for the same crop at later
abs-days) using as lower border for fallow the given abs-min-start-day"
  [dc-assertions abs-min-start-day abs-day]
  (let? [;default if no crop there
         fallow {:abs-start-day 1
                 :rel-start-dc-day 1
                 :crop (db-read-crop 0 :cultivation-type 1 :usage 0)}
         
         {:keys [crop 
                 abs-start-day]
          :as crop-data} (->> (sorted-crop-start-data* dc-assertions abs-min-start-day)
                           ;get only ones asserted before abs-day
                           (drop-while #(> (:abs-assert-dc-day %) abs-day) ,,,)
                           ;get closest assertion and that abs-start-day
                           first)
         :else fallow 
        
        ;get the end abs-day for current crop and check if it hasn't been harvested yet
        abs-crop-end (->> crop
                       :crop/dc-to-rel-dc-days
                       last
                       second
                       (+ abs-start-day ,,,))
        :is (partial <= abs-day)
        :else fallow]
        
        crop-data))

(defn dc-to-abs+rel-dc-day-from-crop-instance-dc-assertions
  "create a dc to abs+rel-dc-day map from the data given with crop-instance
(dc-assertions and the crop template, thus the dc-to-rel-dc-day curve)"
  [crop-instance]
  (let? [dc-assertions* (->> crop-instance
                          :crop-instance/dc-assertions
                          (sort-by :assertion/at-abs-day ,,,)) 
         :is-not empty?
          
         ;_ (println dc-assertions*)
         
         sorted-dc-to-rel-dc-days (->> crop-instance 
                                    :crop-instance/template 
                                    :crop/dc-to-rel-dc-days
                                    (into (sorted-map) ,,,))
         
         ;_ (println sorted-dc-to-rel-dc-days)
         
         {dc* :assertion/assert-dc
          abs-dc-day* :assertion/abs-assert-dc-day} (first dc-assertions*)
         :else nil
                  
         ;_ (println "dc-to-rel-dc-days: " dc-to-rel-dc-days \newline)
         
         rel-dc-day* (interpolated-value sorted-dc-to-rel-dc-days dc*)
         sorted-dc-to-rel-dc-days* (assoc sorted-dc-to-rel-dc-days dc* rel-dc-day*)
         
         sorted-initial-dc-to-abs+rel-dc-day 
         (fmap (fn [rel-dc-day]
                 {:abs-dc-day (+ abs-dc-day* (- rel-dc-day rel-dc-day*))
                  :rel-dc-day rel-dc-day})
               sorted-dc-to-rel-dc-days*)]
    
    ;_ (println "initial: " sorted-initial-dc-to-abs+rel-dc-day \newline)
    
    (reduce (fn [m {dc* :assertion/assert-dc
                    abs-dc-day* :assertion/abs-assert-dc-day}]
              (assoc m dc* {:abs-dc-day abs-dc-day*
                            :rel-dc-day (if-let [{:keys [abs-dc-day rel-dc-day]} 
                                                 (or (get m dc*)
                                                     (->> (adjacent-kv-pairs m dc*)
                                                       (map second ,,,)
                                                       ;now order
                                                       (into #{} ,,,)
                                                       ;get lowest day
                                                       first))] 
                                          (+ rel-dc-day 
                                             (- abs-dc-day* abs-dc-day)))}))
            sorted-initial-dc-to-abs+rel-dc-day (rest dc-assertions*))))

(defn dc-to-abs+rel-dc-day-from-plot-dc-assertions 
  "create a dc to abs-dc-day map for all crops in sequence of dc-assertions"
  [crop-instances]
  (->> crop-instances 
    (map (fn [crop-instance]
           [crop-instance 
            (dc-to-abs+rel-dc-day-from-crop-instance-dc-assertions crop-instance)])
         ,,,)
    (into {} ,,,)))

(defn- index-localized-crop-instance-curves-by-abs-dc-day
  "transform result to map like form indexed by abs-dc-day for sorting and processing"
  [localized-crop-instance-curves]
  (map (fn [[crop-instance dc-map]]
         (map (fn [[dc {:keys [abs-dc-day rel-dc-day]}]]
                [abs-dc-day {:dc dc
                             :rel-dc-day rel-dc-day
                             :crop-instance crop-instance}])
              dc-map))
       localized-crop-instance-curves))
  
(defn- merge-abs-dc-day-to-crop-data-maps
  "merge all crops/abs-dc-days as pre-stage to get final map with correct order in year"
  [abs-dc-day-to-crop-data] 
  (reduce (fn [m crop-map]
            (into m 
                  (for [[abs-dc-day data-map] crop-map]
                    (assoc m abs-dc-day 
                           (if-let [val (get m abs-dc-day)]
                             (if (vector? val)
                               (conj val data-map)
                               [val data-map])
                             data-map)))))
          (sorted-map) abs-dc-day-to-crop-data))

(defn- calculate-final-abs-dc-to-crop-data-map
  "calculate order given a list of merged abs-dc-day-to-crop-data"
  [merged-abs-dc-day-to-crop-data-maps]
  (->> merged-abs-dc-day-to-crop-data-maps
    (reduce (fn [[m last-crop-instance crop-canceled] 
                 [abs-dc-day data-map?s]]
              (let [v? (vector? data-map?s)
                    lci (if last-crop-instance
                          last-crop-instance
                          ;if is vector simply take first crop-instance, other decision could be implemented
                          (:crop-instance ((if v? first identity) data-map?s)))]
                (if v?
                  ;we got a cancelation of the current crop, but have to decide which
                  ;to choose now
                  (->> data-map?s
                    ;ignore the data belong to last crop instance
                    (filter #(not= lci (:crop-instance %)) ,,,)
                    ;for now simply take first from list (other decision could be implemented)
                    first
                    ;create mapping to data for current abs-dc-day
                    (assoc m abs-dc-day ,,,)
                    ;create accumulator for next reduce call
                    (#(vector % (:crop-instance %) true) ,,,))
                  ;ignore crop/crop-data if its from previous crop
                  ;and only if crop has been canceled before by other crop
                  [(if (or (= lci (:crop-instance data-map?s))
                           (not crop-canceled))
                     (assoc m abs-dc-day data-map?s)
                     m) lci crop-canceled])))
            [(sorted-map) nil false] ,,,)
    first))

(defn abs-dc-day->crop-instance
    "given an abs-dc-day return the crop-instance being used and 
the rel-dc-day for this crop-instance
assumes that
a) dc = 1 means seeding, if there's no dc = 1, then is a winter crop, except
if the crop has a previous crop, then a dc > 1 means the crop stop breaks the previous crop
b) there will always be a harvesting (= last dc) step, thus after this step there
is always fallow unless another crop follows with a dc > 1 (see a)"
    [fallow abs-dc-day-to-crop-instance-data abs-dc-day]
    (let [fallow* {:rel-dc-day 1
                   :crop fallow}
          
          first-abs-dc-day (ffirst abs-dc-day-to-crop-instance-data)] 
      (if-let [{dc :dc 
                rel-dc-day :rel-dc-day
                {crop :crop-instance/template} :crop-instance}
             (get abs-dc-day-to-crop-instance-data abs-dc-day)] 
        {:rel-dc-day rel-dc-day :crop crop}
        (let [{[l-abs-dc-day 
                {l-dc :dc 
                 l-rel-dc-day :rel-dc-day
                 {l-crop :crop-instance/template} :crop-instance
                 :as lower}] :lower
               [u-abs-dc-day 
                {u-dc :dc 
                 u-rel-dc-day :rel-dc-day
                 {u-crop :crop-instance/template} :crop-instance
                 :as upper}] :upper} 
              (adjacent-kv-pairs abs-dc-day-to-crop-instance-data abs-dc-day)]
          (cond 
            ;before summer crop, just fallow, but before winter crop = winter crop
            (and (not lower) upper) (if (> u-dc 1) 
                                      {:rel-dc-day u-rel-dc-day :crop u-crop}
                                      fallow*)
            ;after last crop just fallow
            (and lower (not upper)) fallow*
            (and lower upper) (if
                                ;if next=prev crop, just interpolate
                                (or (= l-crop u-crop)
                                    ;if next crop is a different crop and next crop has
                                    ;a dc > 1, thus already after seeding
                                    ;keep on doing the previous crop, until the next crop
                                    ;comes, because the next crop will stop break the previous
                                    ;crop
                                    (and (not= l-crop u-crop)
                                         (> u-dc 1)))
                                {:rel-dc-day (+ l-rel-dc-day (- abs-dc-day l-abs-dc-day))
                                 :crop l-crop}
                                ;if next crop is a different crop, fallow if
                                ;the next crop's dc is 1 => there new crop will start
                                fallow*)
            :else nil)))))

#_(deftest test-abs-dc-day->crop-instance 
  (is (= )))

(defn base-input-seq 
  "create a input sequence for soil-moisture calculations
- takes into account dc assertions which are available in plot map
- lazy sequence as long as weather is available"
  [plot sorted-weather-map irrigation-donations-map irrigation-mode]
  (let [abs-dc-day-to-crop-instance-data  
        (->> (:plot/crop-instances plot)
          dc-to-abs+rel-dc-day-from-plot-dc-assertions
          index-localized-crop-instance-curves-by-abs-dc-day
          merge-abs-dc-day-to-crop-data-maps
          calculate-final-abs-dc-to-crop-data-map)]
    (for [abs-day (range 1 (-> sorted-weather-map rseq ffirst inc))
          :let [weather (sorted-weather-map abs-day)]
          :while weather]
      (let [{:keys [rel-dc-day crop]} 
            (abs-dc-day->crop-instance (:fallow plot) abs-dc-day-to-crop-instance-data abs-day)
            
            prev-day-cover-degree (interpolated-value (:crop/rel-dc-day-to-cover-degrees crop) 
                                                      (dec rel-dc-day))
            
            cover-degree (interpolated-value (:crop/rel-dc-day-to-cover-degrees crop) rel-dc-day)] 
        
        {:abs-day abs-day
         :rel-dc-day rel-dc-day
         :crop crop
         :irrigation-amount (donations-at irrigation-donations-map abs-day)
         :evaporation (bu/round (:evaporation weather) :digits 1)  
         :precipitation (bu/round (:precipitation weather) :digits 1) 
         :irrigation-mode irrigation-mode
         :cover-degree cover-degree
         :qu-target (bu/round 
                      (if (< prev-day-cover-degree 1/100) 
                        0 
                        (interpolated-value (:crop/rel-dc-day-to-quotient-aet-pets crop) rel-dc-day)) 
                      :digits 3)
         :rounded-extraction-depth-cm (->> (if (<= cover-degree 1/1000) 
                                             0 
                                             (interpolated-value (:crop/rel-dc-day-to-extraction-depths crop) rel-dc-day))
                                        (+ 1 ,,,)
                                        (bh/swap / 10 ,,,)
                                        nt/round
                                        (* 10 ,,,))
         :transpiration-factor (interpolated-value (:crop/rel-dc-day-to-transpiration-factors crop) rel-dc-day)
         :fcs (:plot/field-capacities plot)
         :pwps (:plot/permanent-wilting-points plot)
         :lambdas (lambda (:lambda-without-correction plot) abs-day)
         :groundwaterlevel-cm (:plot/groundwaterlevel plot)
         :damage-compaction-depth-cm (resulting-damage-compaction-depth-cm plot)
         :sm-prognosis? false}))))

(defn create-input-seq 
  "create the input sequence for all the other functions"
  [plot sorted-weather-map irrigation-donations-map until-abs-day irrigation-mode]
  (->> (base-input-seq plot 
                       sorted-weather-map 
                       irrigation-donations-map
                       irrigation-mode)
    (drop (dec (:plot/abs-day-of-initial-soil-moisture-measurement plot)) ,,,)
    (take-while #(<= (:abs-day %) until-abs-day) ,,,)))

(defn calc-soil-moistures* 
  "calculate the soil-moistures for the given inputs and initial soil-moisture returning
all intermediate steps, unless red-fn is defined to be reduce"
  [inputs initial-soil-moistures & {:keys [red-fn] :or {red-fn reductions}}]
  (red-fn calc-soil-moisture 
          {:qu-sum-deficits 0
           :qu-sum-targets 0
           :soil-moistures initial-soil-moistures}
          inputs))

(defn calc-soil-moistures 
  "calculate the soil-moistures for the given inputs and initial soil-moisture"
  [inputs initial-soil-moistures]
  (calc-soil-moistures* inputs initial-soil-moistures :red-fn reduce))

(defn average-prognosis-result 
  "averages the results returned from calc-soil-moisture for prognosis calculation"
  [no-of-days {:keys [qu-sum-deficits qu-sum-targets soil-moistures] :as m}]
  {:input m
   :qu-avg-current (/ (- qu-sum-targets qu-sum-deficits) 
                      no-of-days) 
   :qu-avg-target (/ qu-sum-targets 
                     no-of-days) 
   :soil-moistures soil-moistures})

(defn calc-soil-moisture-prognosis* 
  "calculate the soil-moisture prognosis but returning a list of intermediate results"
  [prognosis-days inputs soil-moistures & {:keys [red-fn] :or {red-fn reductions}}]
  (->> inputs
    ;if we got more input, take just the prognosis days
    (take prognosis-days ,,,)
    ;turn on prognosis mode
    (map #(assoc % :sm-prognosis? true) ,,,)
    ;calc soil-moistures (either with intermediate values or just last result)
    (red-fn calc-soil-moisture {:qu-sum-deficits 0
                                :qu-sum-targets 0
                                :soil-moistures soil-moistures}
            ,,,)
    ;always create map, even if we just used reduce as red-fn
    (#(if (map? %) [%] %) ,,,) 
    ;calculate averages of result(s)
    (map-indexed (fn [i v] (average-prognosis-result (inc i) v)) ,,,)))

(defn calc-soil-moisture-prognosis
  "calculate the soil-moisture prognosis in prognosis-days using inputs and the given soil-moisture"
  [prognosis-days inputs soil-moistures]
  (first (calc-soil-moisture-prognosis* prognosis-days inputs soil-moistures :red-fn reduce)))

(defn calc-donation-amount
  "calculate the irrigation amount to be given and the according recommendation text" 
  [qu-target technology inputs soil-moistures]
  (let [input (first inputs)
        
        irrigation-days (max 4 (min (:technology/cycle-days technology) 14))
        
        qu-eff (- qu-target (-> input :crop :crop/effectivity-quotient))
                
        {donation :donation
         recommendation-text :message} 
        (if (< qu-target 1/10) 
          {:donation 0
           :message "Entw/Zeitr"}
          (let [calc-sms-with-donation 
                (fn [irrigation-amount]
                  (->> inputs
                    (take irrigation-days ,,,)
                    (map (fn [input] (assoc input 
                                            :irrigation-amount irrigation-amount
                                            :sm-prognosis? true)) ,,,)
                    (reduce calc-soil-moisture 
                            {:qu-sum-deficits 0
                             :qu-sum-targets 0
                             :soil-moistures soil-moistures}
                            ,,,)
                    (map average-prognosis-result ,,,)))
                                
                ;calculate soil-moisture in given future time without any irrigation as base value
                {qu-0-current :qu-avg-current
                 qu-0-target :qu-avg-target} (calc-sms-with-donation 0)
                                
                {donation :donation
                 recommendation-text* :message} 
                (if (< qu-0-current qu-0-target)
                  ;without irrigation we're below the target curve
                  (if (< qu-eff qu-0-current)
                    ;but we're above the effective curve, thus try again in about 4 days
                    {:donation 0
                     :message "in ca 4 Tg"}
                    ;nope, we've got to irrigate 
                    (let [min-donation (:technology/min-donation technology)
                          max-donation (:technology/max-donation technology)
                          opt-donation (:technology/opt-donation technology)
                          step-size (:technology/donation-step-size technology)
                          
                          make-donation #(hash-map :irrigation/abs-day (inc (:abs-day input)) 
                                                   :irrigation/amount %)
                          
                          donations-+ (for [i (range) :let [delta (* i step-size)]] 
                                        [(make-donation (max min-donation (- opt-donation delta))) 
                                         (make-donation (min (+ opt-donation delta) max-donation))])
                          
                          calc-qus (|-> calc-sms-with-donation (--< :qu-avg-current :qu-avg-target))
                          
                          ;condition (fn [[qu-current qu-target]] (> qu-current (/ (+ qu-eff qu-target) 2)))
                          condition (fn [[qu-current qu-target]] (> qu-current (/ (+ 1 qu-target) 2)))
                          
                          qus-opt (calc-qus (make-donation opt-donation))
                          
                          [select-branch exit-condition] (if (condition qus-opt) 
                                                           [first <] ;opt-- branch: current < target 
                                                           [second >]) ;opt++ branch: current > target
                                                    
                          [final-donation [final-qu-current final-qu-target]] 
                          (->> donations-+
                            (map (|-> select-branch #(vector % (calc-qus %))) ,,,)
                            (drop-while (|-> second 
                                             #(apply (complement exit-condition) %)) 
                                        ,,,)
                            first)]
                      {:donation final-donation
                       :message (if (and (= final-donation max-donation) (< final-qu-current qu-eff))
                                  "S.K. erh."
                                  "Gabe opt.")}))
                  ;without irrigation we're above target curve, thus everything is fine
                  {:donation 0
                   :message "BF opt."})]
            {:donation donation
             :message recommendation-text*}))]
    {:recommendation-text recommendation-text
     :donation donation}))

(defn calc-technological-donation-boundaries
  "calculate the irrigation-water donation data given a soil-moisture and 
the technological restrictions"
  [forecast-days slope technology inputs soil-moistures]
  (let [{:keys [abs-day fcs pwps]} (first inputs)        
        
        [max-donation-soil-30 
         max-donation-soil-60] (->> (map vector (layer-depths) fcs pwps soil-moistures)
                                 ;["depths equal and below 30cm" , "rest"]
                                 (split-with #(<= (first %) 30) ,,,) 
                                 ((juxt first 
                                        (|-> second 
                                             ;finally get 30cm < depth <= 60cm
                                             (partial take-while #(<= (first %) 60))))
                                   ,,,)
                                 ;calc difference of inf-barrier and soil-moisture
                                 ;and sum up layers
                                 ;for both parts
                                 (map #(->> %
                                         (reduce (fn [sum [depth-cm fc pwp sm]] 
                                                   (+ sum 
                                                      (- (infiltration-barrier fc pwp abs-day depth-cm) sm))) 
                                                 0 ,,,)
                                         Math/round) 
                                      ,,,))
        
        max-donation-soil+60 (+ max-donation-soil-30 
                                (if (pos? (* max-donation-soil-30 
                                             max-donation-soil-60))
                                  max-donation-soil-60
                                  0)) 
        
        max-donation-soil+60+weather (->> inputs
                                       (reduce (fn [sum {:keys [evaporation precipitation]}] 
                                                 (+ sum (- evaporation precipitation))) 
                                               max-donation-soil+60 
                                               ,,,)
                                       Math/round
                                       (max 0 ,,,))
        
        max-donation-slope (condp #(% %2) slope
                             #{1 2} 50
                             #{3} 40
                             #{4 5} 30
                             #{6} 20)
        
        opt-donation-technology (:technology/opt-donation technology)
        donation-step-size (:technology/donation-step-size technology) 
        
        max-donation (min (:technology/max-donation technology) max-donation-slope max-donation-soil+60+weather)
        opt-donation (if (< max-donation-soil+60+weather opt-donation-technology)
                       (let [gap (- opt-donation-technology max-donation-soil+60+weather)
                             q (quot gap donation-step-size)
                             q* (+ q (min (mod gap donation-step-size) 1))]
                         (- opt-donation-technology (* q* donation-step-size)))
                       opt-donation-technology)]
    (assoc technology 
           :is-soil-moisture-high? (< max-donation-soil+60+weather 15)
           :opt-donation opt-donation
           :max-donation max-donation)))

(defn calc-recommendation 
  "calculate the recommendation text and recommendation donation amount for the given input values"
  [slope technology inputs soil-moistures]
  (let [input (first inputs)
      
        {:keys [is-soil-moistures-high?
                max-donation 
                min-donation
                opt-donation]
         :as technology*} (calc-technological-donation-boundaries 5 slope technology 
                                                                  inputs soil-moistures) 
        
        {:keys [qu-avg-current
                qu-avg-target]} (calc-soil-moisture-prognosis 7 inputs soil-moistures)
        
        [recommendation-text 
         donation] (cond 
                     
                     (< qu-avg-current qu-avg-target) 
                     (cond 
                       
                       is-soil-moistures-high? 
                       ["Bf-hoch" opt-donation]
                     
                       (>= max-donation min-donation)
                       (calc-donation-amount qu-avg-target technology* inputs soil-moistures)
                       
                       :else 
                       ["Tech.min" opt-donation])
                           
                     (< qu-avg-target 1/10) 
                     ["Entw/Zeitr" opt-donation]
                     
                     :else 
                     ["Bf-opt" opt-donation])]
    {:recommended-donation-amount donation
     :recommendation-text recommendation-text}))

(defn create-csv-output [inputs full-reductions-results]
  (let [header-line ["CLJ day"
                     "CLJ precip"
                     "CLJ evap"
                     "CLJ irrWater"
                     "CLJ pet"
                     "CLJ aet"
                     "CLJ aet/pet"
                     "CLJ aet/pet soll"
                     "CLJ infil 15dm"
                     "CLJ mm 10cm"
                     "CLJ mm 10-30cm"
                     "CLJ mm 30-60cm"
                     "CLJ mm 60-100cm"
                     "CLJ mm 100-150cm"
                     "CLJ mm 0-30cm"
                     "CLJ mm 30-60cm"
                     "CLJ rel DC day"
                     "CLJ lambda"]
        
        body-lines (map (fn [input rres]
                          (map str [(:abs-day input) 
                                    (:precipitation input)
                                    (- (:evaporation input))
                                    (:irrigation-amount input)
                                    (:pet rres)
                                    (:aet rres)
                                    (:aet7pet rres)
                                    (:qu-target rres)
                                    (:groundwater-infiltration rres)
                                    (nth (:soil-moistures rres) 0)
                                    (nth (:soil-moistures rres) 1)
                                    (nth (:soil-moistures rres) 2)
                                    (nth (:soil-moistures rres) 3)
                                    (nth (:soil-moistures rres) 4)
                                    (ic/sum (subvec (vec (:soil-moistures rres)) 0 2))
                                    (nth (:soil-moistures rres) 2)
                                    (:rel-dc-day input)
                                    0]))
                        inputs full-reductions-results)]
    (cons header-line body-lines)))

(defn run [db plot sorted-weather-map irrigation-donations-map until-abs-day irrigation-mode]
  (append-out out str (str (-> plot :plot/number str (subs ,,, 0 3)) "-"
                           (-> plot :plot/number str (subs ,,, 3 4)) " "
                           (-> plot :assertion/dc-assertions first :assertion/crop crop-id) " " 
                           (-> plot :assertion/dc-assertions first :assertion/crop :symbol) "      "))
  
  (let [inputs (create-input-seq plot irrigation-donations-map 
                                 sorted-weather-map (+ until-abs-day 7) irrigation-mode)
        _ (println "inputs:" \newline "----------------------")
        _ (pp/pprint inputs)
        _ (println "----------------------")
           
        inputs-7 (drop-last 7 inputs)
        prognosis-inputs (take-last 7 inputs)
                
        sms-7* (calc-soil-moistures* inputs-7 (:plot/initial-soil-moistures plot))
        _ (println "soil-moistures-7:" \newline "----------------------")
        _ (pp/pprint sms-7*) 
        _ (println "----------------------")
        {soil-moistures-7 :soil-moistures 
         :as sms-7} (last sms-7*) 
        #_(calc-soil-moistures inputs-7 (:plot/initial-soil-moistures plot))
               
        prognosis* (calc-soil-moisture-prognosis* 7 prognosis-inputs soil-moistures-7)
        _ (println "prognosis:" \newline "----------------------")
        _ (pp/pprint prognosis*)
        _ (println "----------------------")
        prognosis (last prognosis*)
        #_(calc-soil-moisture-prognosis 7 prognosis-inputs soil-moistures-7)
        
        {:keys [recommendation-text recommended-donation-amount] 
         :as rec} (calc-recommendation (:plot/slope plot) (:plot/technology plot) 
                                       prognosis-inputs soil-moistures-7)
        _ (println "recommendation:" \newline "----------------------")
        _ (pp/pprint rec)
        _ (println "----------------------")
        ]
    (spit "out.csv" (csv/write-csv (create-csv-output inputs (concat sms-7* prognosis*))))))

(defn -main
  "main function for commandline use"
  [& args]
  
  (let [db (->> "berest"
             (str bd/datomic-base-uri ,,,)
             d/connect
             d/db)
        plot (db-read-plot db "0400")
        weather weather-map
        irrigation-donations-map (read-irrigation-donations db 
                                                            (:plot/number plot) 
                                                            (:plot/irrigation-area plot))
        weather+prognosis weather
        irrigation-mode :sprinkle-losses]
    (run plot weather irrigation-donations-map (bu/date-to-doy 29 5) irrigation-mode)))

#_(-main)























#_(defn height 
  [rel-dc-day soil-moistures plot qu-target technology irrigation-mode weathers out]
  (let [crop (:crop plot)
        direction 0
        
        irrigation-days (max 4 (min (:cycle-days technology) 14))
        
        qu-eff (- qu-target (:crop/effectivity-quotient crop))
                
        {donation :donation
         recommendation-text :message} 
        (if (< qu-target 1/10) 
          {:donation 0
           :message "Entw/Zeitr"}
          (let [;calculate soil-moisture in given future time without any irrigation as base value
                {qu-0-target :qu-avg-target
                 qu-0-current :qu-avg-current} (sm-x-days irrigation-days (abs-pre-calculation-day plot) rel-dc-day
                                                          soil-moistures plot true irrigation-mode [] weathers out) ;q-ist bei hhOpt=0
                {donation :donation
                 recommendation-text* :message} 
                (if (< qu-0-current qu-0-target)
                  ;without irrigation we're below the target curve
                  (if (< qu-eff qu-0-current)
                    ;but we're above the effective curve, thus try again in about 4 days
                    {:donation 0
                     :message "in ca 4 Tg"}
                    ;nope, we've got to irrigate 
                    (let [min-donation (:min-donation technology)
                          max-donation (:max-donation technology)
                          opt-donation (:opt-donation technology)
                          step-size (:donation-step-size technology)
                          
                          make-donation #(hash-map :abs-day (inc (:abs-calculation-day plot)) 
                                                   :amount %)
                          
                          donations-+ (for [i (range) :let [delta (* i step-size)]] 
                                        [(make-donation (max min-donation (- opt-donation delta))) 
                                         (make-donation (min (+ opt-donation delta) max-donation))])
                          
                          calc-qus (|-> (fn [single-donation]
                                          sm-x-days irrigation-days (abs-pre-calculation-day plot) rel-dc-day
                                          soil-moistures plot true irrigation-mode [single-donation] weathers out)
                                        (--< :qu-avg-current :qu-avg-target))
                          
                          ;condition (fn [[qu-current qu-target]] (> qu-current (/ (+ qu-eff qu-target) 2)))
                          condition (fn [[qu-current qu-target]] (> qu-current (/ (+ 1 qu-target) 2)))
                          
                          qus-opt (calc-qus (make-donation opt-donation))
                          
                          [select-branch exit-condition] (if (condition qus-opt) 
                                                           [first <] ;opt-- branch: current < target 
                                                           [second >]) ;opt++ branch: current > target
                                                    
                          [final-donation [final-qu-current final-qu-target]] 
                          (->> donations-+
                            (map (|-> select-branch #(vector % (calc-qus %))) ,,,)
                            (drop-while (|-> second 
                                             #(apply (complement exit-condition) %)) 
                                        ,,,)
                            first)]
                      {:donation final-donation
                       :message (if (and (= final-donation max-donation) (< final-qu-current qu-eff))
                                  "S.K. erh."
                                  "Gabe opt.")}))
                  ;without irrigation we're above target curve, thus everything is fine
                  {:donation 0
                   :message "BF opt."})]
            {:donation donation
             :message recommendation-text*}))]
    {:recommendation-text recommendation-text
     :donation donation}))




#_(defn sm-x-days [x-days abs-current-day rel-dc-day soil-moistures plot sm-prognosis?  
                   irrigation-mode donations weathers out]
  (loop [days x-days 
         qu-sum-deficits 0 
         qu-sum-targets 0 
         soil-moistures soil-moistures 
         groundwater-infiltration 0]
    (cond 
      (= days 0)
      {:qu-avg-current (/ (- qu-sum-targets qu-sum-deficits) 
                          x-days) 
       :qu-avg-target (/ qu-sum-targets 
                         x-days) 
       :soil-moistures soil-moistures 
       :groundwater-infiltration groundwater-infiltration}
      
      :else
      (let [crop (:crop plot)
            delta (- x-days days)
            rel-dc-day* (+ rel-dc-day delta 1)
            abs-current-day* (+ abs-current-day delta 1) 
            prev-day-cover-degree (interpolated-value (:rel-dc-day-to-cover-degree crop) (dec rel-dc-day*))
            qu-target (if (< prev-day-cover-degree 1/100) 
                        0 
                        (interpolated-value (:rel-dc-day-to-quotient-aet-pet crop) rel-dc-day*))
            cover-degree (interpolated-value (:rel-dc-day-to-cover-degree crop) rel-dc-day*)
            rounded-extraction-depth-cm (->> (if (<= cover-degree 1/1000) 
                                               0 
                                               (interpolated-value (:rel-dc-day-to-extraction-depth crop) rel-dc-day*))
                                          (+ 1 ,,,)
                                          (h/swap / 10 ,,,)
                                          nt/round
                                          (* 10 ,,,))
            weather (weather-at weathers abs-current-day*)
            donation (donations-at donations abs-current-day*)
            transpiration-factor (interpolated-value (:rel-dc-day-to-transpiration-factor crop) rel-dc-day*)
            
            {:keys [pet 
                    effective-precipitation 
                    effective-irrigation 
                    effective-irrigation-uncovered]} (interception (:precipitation weather) 
                                                                   (:evaporation weather) 
                                                                   donation transpiration-factor irrigation-mode)
            
            pet* (if (< cover-degree 1/1000) 
                   0 
                   (* pet transpiration-factor))
            daily-precipitation-and-irrigation (+ 
                                                 (* (+ effective-precipitation effective-irrigation) cover-degree) 
                                                 (* (+ (:precipitation weather) effective-irrigation-uncovered) (- 1 cover-degree)))

            {aet :aet
             soil-moistures* :soil-moistures
             groundwater-infiltration :groundwater-infiltration} (sm-1-day rounded-extraction-depth-cm cover-degree pet* 
                                                                           abs-current-day* (:fc plot) (:pwp plot)
                                                                           (lambda (:lambda-without-correction plot) abs-current-day*) soil-moistures
                                                                           sm-prognosis? (:evaporation weather) (resulting-damage-compaction-depth-cm plot) 
                                                                           (:groundwaterlevel plot) daily-precipitation-and-irrigation)
                        
            aet7pet (cond 
                      (< cover-degree 1/1000) 0
                      (> pet* 1/100) (/ aet pet*)
                      :else 1)

            _ (append-out out conj (map str [abs-current-day* 
                                             (:precipitation weather)
                                             (- (:evaporation weather))
                                             donation
                                             pet*
                                             aet
                                             aet7pet
                                             qu-target
                                             groundwater-infiltration
                                             (nth soil-moistures* 0)
                                             (nth soil-moistures* 1)
                                             (nth soil-moistures* 2)
                                             (nth soil-moistures* 3)
                                             (nth soil-moistures* 4)
                                             (ic/sum (subvec (vec soil-moistures*) 0 2))
                                             (nth soil-moistures* 2)
                                             rel-dc-day*
                                             0]))]
        (recur (dec days) 
               (if (< aet7pet qu-target) 
                 (+ qu-sum-deficits (- qu-target aet7pet)) 
                 qu-sum-deficits)
               (+ qu-sum-targets qu-target)
               soil-moistures* 
               groundwater-infiltration)))))

#_(defn dc-assertion-at-abs-day 
  "get the dc assertion map for the given crop at the given abs-day"  
  [dc-assertions for-crop abs-day]
  (let [;sort for dc and get only the onces for given crop
        ordered-asserts-for-crop (->> dc-assertions
                                   (filter #(= for-crop (:crop %)) ,,,)
                                   (map (fn [a] [(:abs-assert-dc-day a) a]) ,,,)
                                   (into (ordered-map) ,,,))
        
        ;try to get a list of all asserts after min-dc
        >=abs-dc-day-asserts (rsubseq ordered-asserts-for-crop <= abs-day)]
    (if (seq >=abs-dc-day-asserts)
      (first >=abs-dc-day-asserts) ;take first equal or before abs-day
      (last ordered-asserts-for-crop)))) ;if none, take closest one after abs-day (at least one should be there)



#_(defn calc-soil-moisture-prognosis [plot prognosis-days inputs current-soil-moistures]
  (let [dc-to-rel-dc-days (-> current-input :crop :dc-to-rel-dc-days)
        
        current-input (first inputs)
        
        abs-current-day (:abs-day current-input)
             
        ;get the current valid dc assertion for abs-current-day
        {:keys [crop assert-dc abs-assert-dc-day] :as dc-assertion} 
        (dc-assertion-at-abs-day (:dc-assertions plot) (:crop current-input) abs-current-day)
        
        ;get the dc data about the dc which has been reached at the prognosis time
        ;this can be in the past or only be slightly in the future
        ;depending on the dc milestones
        {reached-dc :reached-dc
         abs-reached-dc-day :abs-reached-dc-day} (reached-dc-data dc-to-rel-dc-days 
                                                                  dc-assertion
                                                                  (+ prognosis-days abs-current-day))  
        
        ;and get also the relative reached dc day, we need to calculate the correct current 
        ;rel-dc-day to use in other curves with possbile different milestones
        rel-reached-dc-day (-> dc-to-rel-dc-days
                             (interpolated-value ,,, reached-dc)
                             Math/round)
                
        rel-dc-day (+ rel-reached-dc-day (- abs-current-day abs-reached-dc-day))
                
        inputs* (map #(assoc % :sm-prognosis? true) inputs)
        
        rds (reductions calc-soil-moisture {:qu-sum-deficits 0
                                            :qu-sum-targets 0
                                            :soil-moistures current-soil-moistures}
                        inputs)]
    rds))



#_(defn run-1 [plot weathers irrigation-mode]
  (append-out fout conj ["CLJ day"
                        "CLJ precip"
                        "CLJ evap"
                        "CLJ irrWater"
                        "CLJ pet"
                        "CLJ aet"
                        "CLJ aet/pet"
                        "CLJ aet/pet soll"
                        "CLJ infil 15dm"
                        "CLJ mm 10cm"
                        "CLJ mm 10-30cm"
                        "CLJ mm 30-60cm"
                        "CLJ mm 60-100cm"
                        "CLJ mm 100-150cm"
                        "CLJ mm 0-30cm"
                        "CLJ mm 30-60cm"
                        "CLJ rel DC day"
                        "CLJ lambda"])
  
  (append-out out str (str (-> plot :number str (subs ,,, 0 3)) "-"
                           (-> plot :number str (subs ,,, 3 4)) " "
                           (-> plot :crop crop-id) " " 
                           (-> plot :crop :symbol) "      "))
  
  (let? [;get initial soilmoisture
        ;------------------------------
        
        initial-soil-moisture (:initial-soil-moisture plot)
                
        abs-initial-soil-moisture-day (:abs-day-of-initial-soil-moisture-measurement plot)
        
        ; reads at least all the irrigation water 
        donations (read-irrigation-donations (:number plot) 
                                             (:irrigation-area plot))
        
        fallow (db-read-crop 0 :cultivation-type 1 :usage 0)
        
        ;calculate when crop period starts, to determine length of fallow period
        ;if there is no DC=1, then the return DC day will be from a later
        ;(but the first available) stage, but in this case this doesn't matter
        ;because we care for actually the first stage and not necessarily DC=1
        abs-crop-start-day (-> plot 
                             :crop 
                             :dc-to-rel-dc-day
                             (abs-search-dc-day ,,, (:known-dc plot) 
                                                    (:known-abs-dc-day plot)
                                                    1)
                             (max ,,, abs-initial-soil-moisture-day))
        
        ;calculate soilmoisture after fallow period
        ;-----------------------------------

        ;but even more: first run everything with fallow
        ;we run as far as possible with fallow but only until the day before the
        ;calculation day
        fallow-days (- (min (abs-pre-calculation-day plot) abs-crop-start-day)
                       abs-initial-soil-moisture-day) 
        
        start-crop-soil-moistures (if (> fallow-days 0)
                                   (:soil-moistures (sm-x-days fallow-days abs-initial-soil-moisture-day 0
                                                              (:initial-soil-moisture plot) 
                                                              (assoc plot :crop fallow) false
                                                              irrigation-mode donations weathers fout))
                                   (:initial-soil-moisture plot)) 
                
        :when (if (< (abs-pre-calculation-day plot) abs-crop-start-day)
                (do 
                  (println "Berest could just run fallow. No need for irrigation so far.")
                  (println "Press return.")
                  (read-line)
                  false)
                true)
          
        ;calculate soilmoisture in past crop period
        ;---------------------------------------
        plant-days (- (abs-pre-calculation-day plot) abs-crop-start-day)
        
        :when (if (<= plant-days 0)
                (do 
                  (println "No plant days to calculate. Exiting.")
                  (println "Press return.")
                  (read-line)
                  false)
                true) 
        
        rel-crop-start-dc-day (-> plot 
                                :crop 
                                :dc-to-rel-dc-day
                                (rel-search-dc-day (:known-dc plot) 
                                                   (:known-abs-dc-day plot)
                                                   abs-crop-start-day)) 
        
        current-soil-moistures (-> (sm-x-days plant-days abs-crop-start-day rel-crop-start-dc-day
                                             start-crop-soil-moistures plot false irrigation-mode donations
                                             weathers fout)
                                :soil-moistures)
        
        abs-current-soil-moisture-day (+ abs-crop-start-day plant-days)
        
        ;prognosis of soilmoisture in next 7 days
        ;--------------------------------------------------

        ;get the dc we would have reached in 7 days
        ;this is taken as reference for the prognosis as the machinery on the plots
        ;and the farmers themselfs can't use any better resolution
        ;thus the resolution of berest (regarding dc states) is 7 days
        {known-dc :reached-dc
         known-dc-day :abs-reached-dc-day} (-> plot 
                                          :crop 
                                          :dc-to-rel-dc-day
                                          (reached-dc-data (:known-dc plot) 
                                                           (:known-abs-dc-day plot)
                                                           (+ 7 (:abs-calculation-day plot))))  
        
        ;even though we just have a 7 day resolution regarding the dc states
        ;we need to get the current exact relative dc day for some of the other
        ;needed curves, as these could use different milestones than the dc2day curve
        rel-milestone-dc-day (-> plot 
                               :crop 
                               :dc-to-rel-dc-day
                               (interpolated-value ,,, known-dc)
                               (+ ,,, 0.1)
                               Math/round)
        
        rel-dc-day (+ rel-milestone-dc-day (- abs-current-soil-moisture-day known-dc-day))
        
        sms (create-soil-moistures max-soil-depth (layer-depths) current-soil-moistures
                                   (:fc plot) (:pwp plot)) 
               
        current-dc-date (doy-to-date known-dc) 
        _ (append-out out str (str known-dc "/" 
                                   (>=2digits (ctc/day current-dc-date)) "." 
                                   (>=2digits (ctc/month current-dc-date)) ".   "
                                   (:pNFK_0-30cm sms) "  " (:pNFK_30-60cm sms) "    ")) 
        
        ;calculate the soil moisture we'd have in 7 days given a
        ;5 day weather forecast
        {qu-avg-current :qu-avg-current
         qu-avg-target :qu-avg-target
         predicted-7-day-soil-moistures :soil-moistures} 
        (sm-x-days 7 abs-current-soil-moisture-day rel-dc-day
                   current-soil-moistures plot true irrigation-mode donations
                   weathers fout)
        abs-7-day-soil-moisture-day (+ abs-current-soil-moisture-day 7)

        sms2 (create-soil-moistures max-soil-depth (layer-depths) 
                                    predicted-7-day-soil-moistures
                                    (:fc plot) (:pwp plot)) 
        
        _ (append-out out str (str (:pNFK_0-30cm sms2) "  " (:pNFK_30-60cm sms2) "  ")) 
        
        ;calculate the irrigation recommendation
        ;-----------------------------------------

        ;SekTyp1 schSrec;
        ;schSrec.empf_mm = 0;

        ;if(schlag.beFlaeche > 0 && schlag.beFlaeche <= schlag.faFlaeche)
        tech (technology abs-current-soil-moisture-day plot current-soil-moistures weathers) 
        
        [recommendation-text 
         donation] (cond 
                     
                     (< qu-avg-current qu-avg-target) 
                     (cond 
                       
                       (:is-soil-moistures-high? tech) 
                       ["Bf-hoch" (:opt-donation tech)]
                     
                       (>= (:max-donation tech) (:min-donation tech))
                       (height rel-dc-day current-soil-moistures plot qu-avg-target tech irrigation-mode weathers fout)
                       
                       :else 
                       ["Tech.min" (:opt-donation tech)])
                           
                     (< qu-avg-target 1/10) 
                     ["Entw/Zeitr" (:opt-donation tech)]
                     
                     :else 
                     ["Bf-opt" (:opt-donation tech)]) 
        
        _ (append-out out str (str "X " donation " " (:max-donation tech) " " recommendation-text)) 
        
        ]
        (print @out)
        (spit "out.csv" (csv/write-csv @fout))))
