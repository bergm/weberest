(ns weberest.web.clients.webfui.core
  (:require [webfui.framework :as wf]
            [webfui.utilities :as wu]
            [goog.net.XhrIo :as xhr]
            [cljs.reader :as cljsr]
            [clojure.string :as string])  
  (:require-macros [webfui.framework.macros :as wm]))

(declare app-state)

(defn send [safe-state method uri fun]
  (xhr/send uri
            (fn [event]
              (let [response (.-target event)]
                (if (.isSuccess response)
                  (fun (.getResponseText response))
                  (reset! app-state safe-state))))
            (name method)))

(def initial-state {:plot-ids []
                    :selected-plot-id "zalf"
                    :until-day-month [1 8]
                    :weather-year 1993
                    :csv-separator ";"
                    :irrigation-data [[1 4 22] [2 5 10] [11 7 30]]
                    :temp-irrigation-data [nil nil nil]})

(def app-state (atom initial-state))

(send initial-state :get "rest/farms/111/plots" #(->> %
                                                      cljsr/read-string 
                                                      (swap! app-state assoc :plot-ids ,,,)))

#_(defn render-display [{:keys [amount amount-decimal accumulator]}]
  (check-overflow (cond (not amount) (format-accumulator accumulator)
                        amount-decimal (.toFixed amount amount-decimal)
                        :else (str amount))))

(defn is-leap-year [year]
  (= 0 (rem (- 2012 year) 4)))

(defn create-option [value selected-value & [display-value]]
  [:option (merge {:value value} 
                  (when (= value selected-value) 
                    {:selected "selected"})) 
           (or display-value value)])

(defn create-number-input [& opts]
  [:input (apply merge {:type "number"} (apply hash-map opts))])

(defn create-irrigation-inputs [& [row-no day month amount]]
  (let [common-opts [:data-row-no row-no 
                     :watch :irrigation-data-changed]]
    [:div 
     (apply create-number-input :data-id :day :placeholder "Tag" :value day common-opts)
     (apply create-number-input :data-id :month :placeholder "Monat" :value month common-opts)
     (apply create-number-input :data-id :amount :placeholder "Menge [mm]" :value amount common-opts)
     [:input (merge {:type "button" :data-row-no row-no}
                    (if row-no 
                      {:mouse :remove-irrigation-row
                       :value "Zeile entfernen"}
                      {:mouse :add-irrigation-row
                       :value "Zeile hinzufügen"}))]]))

(defn indexed [col]
  (->> col
       (interleave (range) ,,,)
       (partition 2 ,,,)))

(defn render-all [state]
  (let [year (:weather-year state)
        [until-day until-month] (:until-day-month state)]
    [:form {:name "test-data-form"}
     [:fieldset 
      [:legend "Schlag Id"] 
      [:select
       (for [pid (:plot-ids state)]
         (create-option pid (:selected-plot-id state)))]]
     
     [:fieldset
      [:legend "Rechnen bis Datum"]
      (create-number-input :placeholder "Tag" :value until-day)
      (create-number-input :placeholder "Monat" :value until-month)]
     
     [:fieldset
      [:legend "Wetterdaten für Jahr"]
      [:select {:id "weather-year" :type "text" :list "year-list"}
       (for [y (range 1993 (inc 1998))] 
         (create-option y year))]]
     
     [:fieldset 
      [:legend "Beregnungsdaten"]
      (for [[row-no [day month amount]] (indexed (:irrigation-data state))]
        (create-irrigation-inputs row-no day month amount))
      (apply create-irrigation-inputs nil (:temp-irrigation-data state))]
     
     [:input {:type "button" :mouse :calc-and-download :value "Berechnen & CSV-Downloaden"}]
     
     ]))


(wm/add-mouse-watch :calc-and-download [state first-element last-element]
                    (when (wu/clicked first-element last-element)
                      (let [[day month] (:until-day-month state)
                            url (str "rest/farms/111/plots/" (:selected-plot-id state) ".csv" 
                                     ;"?format=csv"
                                     "?until-day=" day "&until-month=" month
                                     "&weather-year=" (:weather-year state) 
                                     "&irrigation-data=" (prn-str (:irrigation-data state)))]
                        (-> js/window
                            (.open ,,, url)))))

(wm/add-mouse-watch :remove-irrigation-row [state first-element last-element]
                    (when (wu/clicked first-element last-element)
                      (let [row-no (js/parseInt (wu/get-attribute first-element :data-row-no))]
                        {:irrigation-data (keep-indexed #(when-not (= %1 row-no) %2) (:irrigation-data state))})))

(wm/add-mouse-watch :add-irrigation-row [state first-element last-element]
                    (js/alert (str (:temp-irrigation-data state)))
                    (when (wu/clicked first-element last-element)
                      (let [temp-id (:temp-irrigation-data state)
                            id (:irrigation-data state)]
                        (when (not-any? nil? temp-id)
                          {:irrigation-data (conj id temp-id)
                           :temp-irrigation-data [nil nil nil]}))))

(wm/add-dom-watch :irrigation-data-changed [state new-element]
                  (let [{:keys [data-id value data-row-no]} (second new-element)
                        row-no (when (not (string/blank? data-row-no)) 
                                 (js/parseInt data-row-no))
                        irr-data (:irrigation-data state)
                        irr-data-row (if row-no
                                       (nth irr-data row-no)
                                       (:temp-irrigation-data state))]
                    (when-not (string/blank? value)
                      (let [[day month amount] irr-data
                            new-id (case (keyword data-id)
                                     :day [(js/parseInt value) month amount]
                                     :month [day (js/parseInt value) amount]
                                     :amount [day month (js/parseDouble value)])]
                        (js/alert (str "row-no: " row-no "new-id: " new-id " res: " (assoc irr-data row-no new-id)))
                        (if row-no
                          {:irrigation-data (assoc irr-data row-no new-id)}
                          {:temp-irrigation-data new-id})))))

#_(add-mouse-watch :num [state first-element last-element]
                   (when (clicked first-element last-element)
                   (let [{:keys [amount amount-decimal]} state
                         digit (js/parseInt (name (get-attribute first-element :id)))]
                     (if amount-decimal
                       {:amount (+ amount (/ digit 10 (apply * (repeat amount-decimal 10))))
                        :amount-decimal (inc amount-decimal)}
                       {:amount (+ (* amount 10) digit)}))))

#_(add-mouse-watch :op [state first-element last-element]
                 (when (clicked first-element last-element)
                   (let [{:keys [amount operation accumulator]} state]
                     {:amount nil
                      :amount-decimal nil
                      :accumulator (if (and amount operation)
                                     ((operations operation) accumulator amount)
                                     (or amount accumulator))
                      :operation (get-attribute first-element :id)})))

#_(add-mouse-watch :period [state first-element last-element]
                 (when (clicked first-element last-element)
                   (when-not (:amount-decimal state)
                     {:amount-decimal 0})))

#_(add-mouse-watch :ac [state first-element last-element]
                 (when (clicked first-element last-element)
                   (assoc initial-state :memory (:memory state))))

#_(add-mouse-watch :ms [state first-element last-element]
                 (when (clicked first-element last-element)
                   (let [{:keys [amount accumulator]} state]
                     {:memory (or amount accumulator)})))

#_(add-mouse-watch :mr [state first-element last-element]
                 (when (clicked first-element last-element)
                   (let [{:keys [memory]} state]
                     {:amount memory})))



#_(defn memory-loaded [text]
  (let [memory (read-string text)]
    (swap! my-state assoc :memory memory :amount memory)))

#_(defn memory-saved []
  (swap! my-state assoc :memory :unknown))

#_(add-watch my-state :my-watch
           (fn [_ _ old new]
             (let [{:keys [amount memory]} new]
               (when (= amount :unknown)
                 (if (= (:amount old) :unknown)
                   (reset! my-state old)
                   (send old :get "memory" memory-loaded)))
               (when (not= memory :unknown)
                 (if (not= (:memory old) :unknown)
                   (reset! my-state old)
                   (send old :put (str "memory/" memory) memory-saved))))))

(wf/launch-app app-state render-all)

