(ns weberest.web.clients.webfui.core
  (:require [webfui.framework :as wf]
            [webfui.utilities :as wu]
            [goog.net.XhrIo :as xhr]
            [cljs.reader :as cljsr]
            [clojure.string :as string]
            [domina :as dom])  
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
                    :until-day-month [10 10]
                    :weather-year 1993
                    :csv-separator ";"
                    :irrigation-data [[1 4 22] [2 5 10] [11 7 30]]
                    :temp-irrigation-data [nil nil nil]
                    :simulate? false})

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
      (create-number-input :placeholder "Tag" :value until-day :watch :until-day)
      (create-number-input :placeholder "Monat" :value until-month :watch :until-month)]
     
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
     
     #_[:fieldset
      [:legend "Simulation"]
      [:input {:id "sim" :type "checkbox" :watch :simulation}] 
      #_[:input {:type "text" :watch :simulation}]
      #_[:label {:for "sim"} " Beregnungsgaben irgnorieren und Empfehlungen automatisch geben!"]]
     
     [:input {:type "button" :mouse :calc-and-download :value "Berechnen & CSV-Downloaden"}]
     [:input {:type "button" :mouse :sim-and-download :value "Simulieren & CSV-Downloaden"}]
     
     [:a {:href "https://dl.dropboxusercontent.com/u/29574974/Weberest/output-analysis.xlsx"} "Analyse Excel-File herunterladen"]
     
     ]))


(wm/add-mouse-watch :calc-and-download [state first-element last-element]
                    (when (wu/clicked first-element last-element)
                      (let [[day month] (:until-day-month state)
                            wy (dom/by-id "weather-year")
                            url (str "rest/farms/111/plots/" (:selected-plot-id state) ".csv" 
                                     ;"?format=csv"
                                     "?sim=false&until-day=" day "&until-month=" month
                                     "&weather-year=" (js/parseInt (.-value (.item (.-options wy) (.-selectedIndex wy)))) #_(:weather-year state) 
                                     "&irrigation-data=" (prn-str (:irrigation-data state)))]
                        (-> js/window
                            (.open ,,, url)))))

(wm/add-mouse-watch :sim-and-download [state first-element last-element]
                    (when (wu/clicked first-element last-element)
                      (let [[day month] (:until-day-month state)
                            wy (dom/by-id "weather-year")
                            url (str "rest/farms/111/plots/" (:selected-plot-id state) ".csv" 
                                     ;"?format=csv"
                                     "?sim=true&until-day=" day "&until-month=" month
                                     "&weather-year=" (js/parseInt (.-value (.item (.-options wy) (.-selectedIndex wy))))) #_(:weather-year state)]
                        (-> js/window
                            (.open ,,, url)))))

(wm/add-mouse-watch :remove-irrigation-row [state first-element last-element]
                    (when (wu/clicked first-element last-element)
                      (let [row-no (js/parseInt (wu/get-attribute first-element :data-row-no))]
                        {:irrigation-data (->> (:irrigation-data state)
                                               (keep-indexed #(when-not (= %1 row-no) %2) ,,,)
                                               (into [] ,,,))})))

(wm/add-mouse-watch :add-irrigation-row [state first-element last-element]
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
                      (let [[day month amount] irr-data-row
                            new-id (case (keyword data-id)
                                     :day [(js/parseInt value) month amount]
                                     :month [day (js/parseInt value) amount]
                                     :amount [day month (js/parseFloat value)])]
                        (if row-no
                          {:irrigation-data (assoc irr-data row-no new-id)}
                          {:temp-irrigation-data new-id})))))

(wm/add-dom-watch :until-day [state new-element]
                  (let [{:keys [value]} (second new-element)]
                    {:until-day-month [(js/parseInt value) (-> state :until-day-month second)]}))

(wm/add-dom-watch :until-month [state new-element]
                  (let [{:keys [value]} (second new-element)]
                    {:until-day-month [(-> state :until-day-month first) (js/parseInt value) ]}))

(wf/launch-app app-state render-all)

