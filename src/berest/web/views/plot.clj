(ns berest.web.views.plot
  (:require [berest.web.views.common :as common]
            [hiccup [element :as he]
                    [form :as hf]]
            #_[net.cgrand.enlive-html :as html]
            [berest.bcore :as bc]
            [noir.core :as nc]
            [c2.scale :as scale]
            [analemma.charts :as acharts]
            [analemma.svg :as asvg]
            [clojure.walk :as cw]
            [clojure.math.numeric-tower :as nt]
            [clj-time.format :as ctf])
  (:use [c2.core :only [unify]]
        [clojure.core.match :only [match]]
        [berest.helper :as bh :only [args-21->12 args-231->123 
                                    --< --<* |-> |* |*kw]]
        [clojure.core.incubator :only [-?> -?>>]]))

(defn style [m]
  (reduce (fn [s [k v]]
            (str s (name k) ": " 
              (match [v]
                [[val unit]] (str val (name unit))
                
                [n :guard number?]
                (if (#{:height :width :top :left :bottom :right} k)
                  (str n "px")
                  n)

                [val] (name val))
              ";"))
          "" m))

(defn seq->vec [analemma-forms]
  (cw/prewalk #(if (seq? %1) (into [] %1) %1) analemma-forms))

(defn graph-experiments []
  (let [x-margin 30
        y-margin 30
        width 450 
        height 200
        bar-height 20
        data {"A" 1, "B" 2, "C" 4, "D" 3}
        sx (scale/linear :domain [0 (apply max (vals data))]
                         :range [0 width])
        sy (scale/linear :domain [0 (apply max (vals data))]
                         :range [0 height])
        x (range -5 10 0.05)
        y1 (map #(Math/cos %) x)
        y2 (map #(Math/sin %) x)]
    [:div#bars {:style (style {:background-color :green
                               :height (+ height (* 2 y-margin))
                               :width (+ 200 width (* 2 x-margin))})}

      [:svg
        (:svg 
          (-> (acharts/xy-plot :width width :height height
                          :x x-margin :y y-margin
                          :xmin -5 :xmax 10
			                    :ymin -1.5 :ymax 1.5)
            (acharts/add-points [x y1] :transpose-data?? true :size 1)
		        (acharts/add-points [x y2] :transpose-data?? true :size 1
			                     :fill (asvg/rgb 255 0 0))
            (seq->vec)))
        
        #_["rect" {:stroke "#006600", :fill "#00cc00", 
                :ry 5, :rx 5, :x 10, :y 10, 
                :height 50, :width 50}]
        #_["line" {:x1 0 :y1 height :x2 width :y2 height
                :style (style {:stroke :black
                               :fill :none
                               :stroke-width 10})}]
        #_[:line {:x1 0 :y1 height :x2 0 :y2 0
                :style (style {:stroke :black
                               :fill :none
                               :stroke-width 10})}]]
      (unify data (fn [[label val]]
                    [:div.bar {:style (style {:height bar-height
                                              :width (int (sx val))
                                              :background-color :gray})}
                      [:span {:style "color: white;"} label]]))]))

(defn y-axis [{:keys [height width
              	      ymin ymax
            		      grid-lines
            		      axis-font-family axis-font-size
            		      axis-number-format
                      side] :or {side :left}}]
  (let [grid-y-space (/ height grid-lines)]
    (for [i (range 1 (inc grid-lines)) :when (even? i)]
      (-> (asvg/text  { :x (match side 
                            :left 0
                            :right width) 
                        :y (- height (* i grid-y-space))}
  	                  (format axis-number-format
          		                (asvg/translate-value (* i grid-y-space)
          			  	                                0 height ymin ymax)))
        (asvg/style :fill (asvg/rgb 150 150 150)
      	            :font-family axis-font-family
                    :font-size axis-font-size
                    :text-anchor (match side :left :end :right :start)
                    :alignment-baseline :middle)))))

(defn xy-plot [& options]
  (let [props (merge acharts/default-chart-props (apply hash-map options))
        [l-ymin l-ymax] (:y-range-left props)
        [r-ymin r-ymax] (:y-range-right props)]
    {:properties props
     :svg (-> (asvg/group (acharts/chart-background props))
            (asvg/translate (:x props) (:y props))
    	      (concat
    	        (acharts/x-grid props)
    	        (acharts/y-grid props)
    	        (acharts/x-axis props)
    	        (if (and l-ymin l-ymax) 
                (y-axis (merge props {:side :left
                                      :ymin l-ymin
                                      :ymax l-ymax}))
                [])
              (if (and r-ymin r-ymax)
                (y-axis (merge props {:side :right
                                      :ymin r-ymin
                                      :ymax r-ymax}))
                [])))}))

(defn point-label [id x y text options]
  [:text (merge {:id id :x x :y y
                :label-font-family "Verdana"
                :label-font-size "55px"
                :visibility :visible
                :fill (asvg/rgb 100 100 150)} options) 
          text])

(defn polyline [xs ys & options]
  (let [points (clojure.string/join " " (map #(str %1 "," %2) xs ys))]
    [:polyline (merge {:points points} (apply hash-map options))]))

(defn line [x1 y1 x2 y2 & options]
  [:line (merge {:x1 x1 :y1 y1 :x2 x2 :y2 y2} (apply hash-map options))])

(defn add-points [xs ys display-xs display-ys options]
  (map  (fn [x y dx dy]
          [:circle (merge { :cx x, :cy y, :r 2
                            :data-x dx :data-y dy} 
                          options)])
        xs ys display-xs display-ys))

(defn insert-into [chart hi-elements]
  (assoc chart :svg (concat (:svg chart) hi-elements)))

(defn add-margins [low high & {:keys [percentage] :or {percentage 10}}]
  (let [abs #(if (< 0 %1) %1 (- %1))
        range (- high low)
        fraction (* range (/ percentage 100))]
  [(- low (abs fraction)) (+ high (abs fraction))]))

(defn graph-javascript []
  (he/javascript-tag 
    " function enlargeCircle(evt, id, label, unit){
        var ct = evt.currentTarget;
        ct.prevSize = ct.r.baseVal.value;
        ct.r.baseVal.value = ct.prevSize * 3;
        
        var vd = document.getElementById('valueDisplay_'+id);
        vd.setAttribute('x', ct.getAttribute('cx'));
        vd.setAttribute('y', ct.getAttribute('cy'));
        vd.style.visibility = 'visible';
        
        vd.setAttribute('fill', ct.getAttribute('fill'));
        vd.textContent =  'Tag: '+ct.getAttribute('data-x')
                          +' | '+label+': '+ct.getAttribute('data-y')+unit;
      };
      function shrinkCircle(evt, id){
        var ct = evt.currentTarget;
        ct.r.baseVal.value = ct.prevSize;
        
        var vd = document.getElementById('valueDisplay_'+id);
        vd.setAttribute('style', 'visibility:hidden');  
      };"))

(def default-graph-properties 
  { :min-day 80
    :x-margin {:left 40 :right 40}
    :y-margin {:top 30 :bottom 30}
    :height 200
    :plot-area-margin-percentage 5
    :curve-properties { :color :black
                        :color-f nil
                        :label ""
                        :unit ""
                        :f identity
                        :type :line
                        :assoc-y :left
                        :assoc-x :bottom}})

(defn graph [id data & {:keys [ xmin xmax 
                                x-margin y-margin
                                height
                                margin-percentage
                                default-props
                                force-const-lines-display
                                align-zero] 
                        :or { xmin (:min-day default-graph-properties)
                              x-margin (:x-margin default-graph-properties)
                              y-margin (:y-margin default-graph-properties)
                              height (:height default-graph-properties)
                              margin-percentage (:plot-area-margin-percentage default-graph-properties)
                              default-props (:curve-properties default-graph-properties)
                              force-const-lines-display false
                              align-zero false}}]
  (let [;get x and y values of const lines into for x/y/min/max calculations
        ;if forced, else these should only appear if other data being displayed
        ;use that range
        {:keys [hs vs]} (if force-const-lines-display 
                          (reduce (fn [ {:keys [hs vs] :as acc}
                                        {:keys [const horizontal vertical]}]
                                    (cond 
                                      (and const horizontal)
                                      { :hs (conj hs horizontal)
                                        :vs vs}
                                      
                                      (and const vertical)
                                      { :hs hs
                                        :vs (conj vs vertical)}
                                  
                                      :else
                                      acc))
                                  {:hs [] :vs []} data)
                          {:hs [] :vs []})

        data* (map (|* merge default-props) data)

        dayss (map :days data*)
        max-days (apply max (apply concat vs dayss))
        xmax (or xmax (* (+ (quot max-days 10) 1) 10))
        [xmin* xmax*] (add-margins xmin xmax :percentage margin-percentage)
        ;width is being constructed dynamically to use browser scrolling and
        ;zooming in svg graphs
        width (* 9 (int (- xmax xmin))) 

        m|m (fn [min|max l] (when (seq l) (apply min|max l)))
        calc-y-min-max (fn [side]
                          (-?>> data*
                            (filter #(= (:assoc-y %1) side) ,,,)
                            (map :values ,,,)
                            (apply concat hs ,,,)
                            (#(when (seq %) %) ,,,)
                            (--<* (|* m|m min) (|* m|m max) ,,,)
                            (apply add-margins ,,,)))
        
        colors (map :color data*)
        y-min-max* {:left (calc-y-min-max :left)
                    :right (calc-y-min-max :right)}
        
        ;align two sided graphs to a zero line if possible
        y-min-max (if (and  align-zero 
                            (some (|* > 0) (:left y-min-max*))
                            (not-any? nil? (--<* :left :right y-min-max*)))
                    (let [[[yll ylh] [yrl yrh]] 
                          (--<* :left :right y-min-max*)
                          
                          left-ratio (/ ylh yll)
                          upper-factor (Math/abs (/ (* yrl left-ratio)
                                                    yrh))
                          yrh+ (- (* upper-factor yrh) yrh)]
                      { :left [yll ylh]
                        :right [yrl (+ yrh yrh+)]})
                    y-min-max*)
                        
        ;create chart which is used as background
        chart (xy-plot  :width width :height height
                        :x (:left x-margin) :y (:top y-margin)
                        :xmin xmin :xmax xmax
                        :y-range-left (:left y-min-max)
                        :y-range-right (:right y-min-max)
                       :grid-lines (/ (- xmax xmin) 10))

        scale-x (scale/linear :domain [xmin xmax] :range [0. width])
        scale-y { :left (scale/linear :domain (:left y-min-max) 
                                      :range [height 0.])
                  :right (scale/linear  :domain (:right y-min-max) 
                                        :range [height 0.])}

        ;transform data to include possible const lines vertically and horizontally
        data** (map (fn [{:keys [const horizontal vertical assoc-y] :as m}]
                      (merge  default-props m
                              (when (and const (or horizontal vertical)) 
                                (merge {:no-points true}
                                  (cond
                                    horizontal {:days [xmin xmax]
                                                :assoc-x :bottom
                                                :values [horizontal horizontal]}
                                    vertical {:days [vertical vertical]
                                              :assoc-y assoc-y
                                              :values (assoc-y y-min-max)}))))) 
                    data)]  
    [:div#bars {:style (style {:background-color :gray
                               :height (+ height (+ (:top y-margin) 
                                                    (:bottom y-margin)))
                               :width (+ width (+ (:left x-margin)
                                                  (:right x-margin)))})}
      
      [:svg
        (-> chart 
          
          (insert-into ,,, 
            (apply concat
              (for [{:keys [days values color color-f type assoc-y]} data**
                    :let [days* (map scale-x days)
                          scale-y* (assoc-y scale-y)
                          zero (scale-y* 0)]]
                (match type
                  :line [(polyline  days* (map scale-y* values) 
                                    :stroke color :fill :none)]
                  :bar  (map  (fn [day* value]
                                (let [value* (scale-y* value)]
                                  (line day* zero day* value*
                                        :stroke (if color-f 
                                                  (color-f value)
                                                  color) 
                                        :fill :none)))
                              days* values)))))
          
          (insert-into ,,,
            (apply concat 
              (for [{:keys [days values color assoc-y
                            label unit f no-points]} data** 
                    :when (not no-points)]
                (add-points (map scale-x days) 
                            (map (assoc-y scale-y) values) 
                            days (map f values)
                            {:stroke color :fill color
                            :onmouseover (str "enlargeCircle(evt, '" (name id) "', '" label "', '" unit "')")
                            :onmouseout (str "shrinkCircle(evt, '" (name id) "')")}))))

          (insert-into ,,, 
            [(point-label (str "valueDisplay_" (name id)) 0 0 ""
                      {:visibility :hidden
                      :fill :red
                      :transform "translate(-8, -20)"})])

          seq->vec
          :svg)]]))

(nc/defpartial plot-layout [id until]
  [:h1 (str "Schlag: " id)]
  (graph-javascript)
  (if-let [plot (bc/db-read-plot id)]
    (let [weathers bc/weather-map
          inputs (bc/create-input-seq plot weathers 
                                      (+ until 7) :spinkle-losses)
          inputs-7 (drop-last 7 inputs)
          prognosis-inputs (take-last 7 inputs)
          days (range (-> inputs first :abs-day) (+ until 7 1))
          
          sms-7* (bc/calc-soil-moistures* inputs-7 (:plot/initial-soil-moistures plot))
                    
          no-of-layers (-> sms-7* 
                          first 
                          :soil-moistures 
                          count)
                    
          sms-layers (for [i (range no-of-layers)] 
                        (map  (|->  :soil-moistures 
                                    (partial args-21->12 nth i)
                                    (partial args-231->123 bc/round :digits 5))
                              (rest sms-7*)))

          sms-days (map :abs-day (rest sms-7*))

          ;{soil-moistures-7 :soil-moistures 
          ;:as sms-7} (last sms-7*) 
          #_(calc-soil-moistures inputs-7 (:plot/initial-soil-moistures plot))
          
          ] 
      #_(clojure.string/join "<br>" sms-7*)  
      #_(clojure.string/join "<br>" inputs)  
      #_(print-str sms-days)
      
        
      [:div 
        
        [:div#baseData
          [:span#plotId "Nr.: " (:plot/number plot)] " "
          [:span#plotArea "Fläche: " (:plot/crop-area plot)] " "
          [:span#plotIrrArea "beregnete Fläche: " (:plot/irrigation-area plot)]
          #_(print-str plot)]
                
        [:div#graphs 
          [:span  [:span {:style "color:green"} "AET/PET Soll"] " / "
                  [:span {:style "color:goldenrod"} "AET/PET"] " / "
                  [:span 
                    [:span {:style "color:blue"} "N"] "-"
                    [:span {:style "color:red"} "V"] " [mm]"] " / "
                  [:span {:style "color:darkblue"} "Gabe [mm]"]]
          (graph :d2Plot [{ :const true
                            :horizontal 0
                            :assoc-y :left
                            :color :black}
                            
                          { :days (->> inputs 
                                    (filter (|->  :qu-target
                                                  (|* < 0))
                                            ,,,)
                                    (map :abs-day ,,,))
                            :values (->> inputs
                                      (map :qu-target ,,,)
                                      (filter (|* < 0) ,,,)
                                      (map  (|->  (|* * 100)
                                                  (|*kw bc/round :digits 1))
                                            ,,,))
                            :color :green
                            :assoc-y :left
                            :label "AET/PET S."
                            :unit "%"}  
                            
                          { :days (->> (rest sms-7*) 
                                    (filter (|->  :aet7pet
                                                  (|* < 0))
                                            ,,,)
                                    (map :abs-day ,,,))
                            :values (->> (rest sms-7*)
                                      (map :aet7pet ,,,)
                                      (filter (|* < 0) ,,,)
                                      (map  (|->  (|* * 100)
                                                  (|*kw bc/round :digits 1)) 
                                            ,,,))
                            :color :goldenrod
                            :assoc-y :left
                            :label "AET/PET"
                            :unit "%"
                          }    
                            
                          { :days days
                            :values (map  (|->  (--< :precipitation :evaporation)
                                                (|* apply -)
                                                (|*kw bc/round :digits 1)) 
                                          inputs)
                            :color-f #(if (< % 0) :red :blue)
                            :type :bar
                            :assoc-y :right
                            :label "N-V" :unit "mm"}
                            
                          { :days days
                            :values (->> inputs 
                                      (map :irrigation-amount ,,,)
                                      (filter (|* < 0) ,,,)
                                      (map  (|*kw bc/round :digits 1) 
                                            ,,,))
                            :color :darkblue
                            :type :bar
                            :assoc-y :right
                            :label "Gabe" :unit "mm"}]
                  :align-zero true)]
              
      #_[:div 
          [:span  [:span {:style "color:green"} "AET/PET Soll"] " / "
                  [:span {:style "color:goldenrod"} "AET/PET"] " / "
                  [:span 
                    [:span {:style "color:blue"} "N"] "-"
                    [:span {:style "color:red"} "V"] " [mm]"] " / "
                  [:span {:style "color:darkblue"} "Gabe [mm]"]]
          (graph :d1Plot [  
                          { :const true
                            :horizontal 0
                            :assoc-y :left
                            :color :black} 
                           
                          { :days (->> (rest sms-7*) 
                                    (filter (|->  :aet7pet
                                                  (|* < 0))
                                            ,,,)
                                    (map :abs-day ,,,))
                            :values (->> (rest sms-7*)
                                      (map :aet7pet ,,,)
                                      (filter (|* < 0) ,,,)
                                      (map  (|->  (|* * 100)
                                                  (|*kw bc/round :digits 1)) 
                                            ,,,))
                            :color :goldenrod
                            :assoc-y :left
                            :label "AET/PET"
                            :unit "%"
                          }    
                            
                          { :days days
                            :values (map  :precipitation  inputs)
                            :color-f #(if (< % 0) :red :blue)
                            :type :bar
                            :assoc-y :right
                            :label "N-V"}
                            
                          ]
                  :align-zero true :force-const-lines-display true)]        
              
              
       [:div
          [:span "DC Stadium"]
          (graph :d1Plot [{:days days
                           :values (map :rel-dc-day inputs)
                           :color :black
                           :label "DC"}])]
        
        #_[:div 
          [:span  [:span {:style "color:blue"} "Niederschlag (N) [mm]"] " / "
                  [:span {:style "color:red"} "Verdunstung (V) [mm]"] " / "
                  [:span {:style "color:black"} "N-V [mm]"]]
          (graph :d2Plot [{ :const true
                            :horizontal 0
                            :color :black}
                          { :days days
                            :values (map :precipitation inputs)
                            :color :blue
                            :label "Nied." :unit "mm"}
                          { :days days
                            :values (map :evaporation inputs)
                            :color :red
                            :label "Verd." :unit "mm"}
                          { :days days
                            :values (map  (|->  (--< :precipitation :evaporation)
                                                (partial apply -)) 
                                          inputs)
                            :color :black
                            :type :bar
                            :label "P-V" :unit "mm"}])]
        
        [:div 
          [:span [:span {:style "color:darkkhaki"} "Transpirationsfaktor [0-2]"]] " / "
          [:span [:span {:style "color:green"} "Bedeckungsgrad [0-100%]"]] " / "
          [:span [:span {:style "color:blue"} "Durchwurzelungstiefe [0-2m]"]] " / "
          [:span [:span {:style "color:brown"} "Soll AET/PET [0-1]"]]
          (graph :d3Plot [{ :const true
                            :horizontal 0
                            :color :black}
                          { :const true
                            :horizontal 1
                            :color :black}
                          { :days days
                            :values (map :transpiration-factor inputs)
                            :color :darkkhaki
                            :label "Trans."}
                          { :days days
                            :values (map (|-> :rounded-extraction-depth-cm
                                              (partial bh/swap / 100)) inputs)
                            :color :blue
                            :label "D.wurz.tiefe" :unit "m"}
                          { :days days
                            :values (map :cover-degree inputs)
                            :color :green
                            :label "Bed.grad" :unit "%" :f (partial * 100)}
                          { :days days
                            :values (map :qu-target inputs)
                            :color :brown
                            :label "S.AET/PET"}])]
        #_[:div 
          [:span [:span {:style "color:blue"} "Bodenfeuchteschichten"]]
          (concat
            (map-indexed  (fn [i sms]
                            (graph  (keyword (str "d4Plot" i))
                                    [ { :const true
                                        :horizontal 0
                                        :color :black}
                                      { :days sms-days
                                      :values sms
                                      :color :blue
                                      :unit "mm"
                                      :label (str "Bf. Schicht-" (inc i))}]
                                    :force-const-lines-display true
                                    :y-margin (cond 
                                                (= i 0) 
                                                { :top (-> default-graph-properties 
                                                        :y-margin 
                                                        :top)
                                                  :bottom 0}
                                                  
                                                (= (inc i) no-of-layers)
                                                { :top 0
                                                  :bottom (-> default-graph-properties 
                                                            :y-margin 
                                                            :bottom)}

                                                :else 
                                                { :top 0 :bottom 0})))
                          sms-layers))  
            
            
          #_(graph :d4Plot (map-indexed (fn [i sms]
                                        { :days sms-days
                                          :values sms
                                          :color :blue
                                          :label (str "Bf. Schicht-" (inc i))})
                                      sms-layers))]
      
      
        [:div#weatherTable
          [:table 
            [:tr 
              [:th "Datum"] 
              [:th "Niederschlag [mm]"] 
              [:th "Verdunstung [mm]"]
              [:th "Regengabe [mm]"]]
            (for [{:keys [abs-day
                          irrigation-amount
                          precipitation
                          evaporation]} (reverse inputs)]
              [:tr 
                [:td (ctf/unparse (ctf/formatter "dd.MM.YYYY") (bc/doy-to-date abs-day))]
                [:td precipitation]
                [:td evaporation]
                [:td irrigation-amount]])]]
        
        ])

    [:div#error "Fehler: Konnte Schlag mit Nummer: " id " nicht laden!"])
  
)


(nc/defpartial plots-layout [farm-id]
  [:div "all plots in farm " farm-id])

(defn create-plot [farm-id plot-data]
  (:id plot-data))

(nc/defpartial new-plot-layout [farm-id]
  (hf/form-to [:post (str "/farms/" farm-id "/plots/new")]
    [:div 
      (hf/label "id" "Schlagnummer")
      (hf/text-field "id" "1234")]
    [:div
      (hf/label "number" "Schlagnummer:")
      (hf/text-field "number" "")]
    (hf/submit-button "Schlag erstellen")))

