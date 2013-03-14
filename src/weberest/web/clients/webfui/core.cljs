(ns weberest.web.clients.webfui.core
  (:require [webfui.framework :as wf]
            [webfui.utilities :as wu]
            [goog.net.XhrIo :as xhr]
            [cljs.reader :as cljsr])  
  #_(:use [webfui.framework :only [launch-app]]
        [webfui.utilities :only [get-attribute clicked]]
        [cljs.reader :only [read-string]])
  (:use-macros [webfui.framework.macros :only [add-mouse-watch]]))

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
                    :until-julian-day 250
                    :until-day-month []
                    :weather-year 1993
                    :csv-separator ";"
                    :irrigation-data []})

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

(defn render-all [state]
  (let [year (:weather-year state)]
   [:form {:name "test-data-form"}
    [:p 
     [:label {:for "plot-id"} "Schlag Id: "] 
     [:select {:id "plot-id"}
      (for [pid (:plot-ids state)]
        (create-option pid (:selected-plot-id state)))]]
    
    [:p 
     [:label {:for "until-doy"} "bis lfd. Tag: "]
     [:input {:id "until-doy" :type "number" 
              :min "1" :max (if (is-leap-year year) "366" "365") 
              :value (str (:until-julian-day state))}]]
    
    [:p
     [:label {:for "until-date"} "[bis Datum]: "]
     [:input {:id "until-date" :type "date"}]]
    
    [:p 
     [:label {:for "weather-year"} "Jahr (Wetter): "]
     [:select {:id "weather-year" :type "text" :list "year-list"}
      (for [y (range 1993 (inc 1998))] 
        (create-option y year))]]
    
    ]
  )
  
  
  
  
  
  #_[:table [:tbody [:tr [:td {:colspan 4}
                        [:div#display (render-display state)]]]
           (for [row calculator-keys]
             [:tr (for [[sym label mouse] row]
                    [:td {:colspan ({:eq 2} sym 1)}
                     [:div {:id sym
                            :mouse mouse}
                      label]])])]])

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