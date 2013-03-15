(ns weberest.web.clients.webfui.core
  (:require [webfui.framework :as wf]
            [webfui.utilities :as wu]
            [goog.net.XhrIo :as xhr]
            [cljs.reader :as cljsr])  
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
                    :irrigation-data [[1 4 22] [2 5 10] [11 7 30]]})

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

(defn create-number-input [watch placeholder & [value]]
  [:input (merge {:type "number" :watch watch :placeholder placeholder} 
                 (when value {:value value}))])

(defn create-irrigation-inputs [& [day month amount]]
  [:div 
   (create-number-input :day-watch "Tag" day)
   (create-number-input nil "Monat" month)
   (create-number-input nil "Menge [mm]" amount)])

#_(wm/add-dom-watch :day-watch [state new-element]
                  (let [{:keys [value]} (second new-element)]
                    (when (valid-integer value)
                      {id (js/parseInt value)})))

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
      (create-number-input "Tag" until-day)
      (create-number-input "Monat" until-month)]
     
     [:fieldset
      [:legend "Wetterdaten für Jahr"]
      [:select {:id "weather-year" :type "text" :list "year-list"}
       (for [y (range 1993 (inc 1998))] 
         (create-option y year))]]
     
     [:fieldset 
      [:legend "Beregnungsdaten"]
      (for [[day month amount] (:irrigation-data state)]
        (create-irrigation-inputs day month amount))
      (create-irrigation-inputs)]
     
     #_[:input {:type "submit" :mouse :calc-and-download :value "Berechnen & CSV-Downloaden" :onsubmit "false"}]
     
     [:div {:style "color:green;background-color:red;" :mouse :calc-and-download} "Berechnen & CSV-Downloaden"]
     
     ]))


(wm/add-mouse-watch 
 :calc-and-download [state first-element last-element]
 (when (wu/clicked first-element last-element)
   (let [[day month] (:until-day-month state)
         url (str "rest/farms/111/plots/" (:selected-plot-id state) 
                  "?format=csv"
                  "&until-day=" day "&until-month=" month
                  "&weather-year=" (:weather-year state) 
                  "&irrigation-data=" (prn-str (:irrigation-data state)))]
     (send initial-state :get url #()))))
  
  #_(add-dom-watch :new-irrigation [state new-element]
                 nil
                 #_(js/alert (pr-str new-element))
               #_(let [{:keys [value]} (second new-element)]
                 (when (valid-integer value)
                   {id (js/parseInt value)})))
    
  
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