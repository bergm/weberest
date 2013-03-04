(ns weberest.web.views.farm
  (:require [weberest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [weberest.core :as bc]
            #_[noir 
             [validation :as vali]]
            [hiccup
             [element :as he]
             [def :as hd]
             [form :as hf]
             [page :as hp]]))

(defn farm-layout [user-id id]
  [:div "user-id" user-id "& farm no " id]
  #_(if-let [plot (bc/db-read-plot id)]
    (let []
      [:h1 (str "Schlag: " id)]
        [:div#plotData 
          [:div#currentDCData 
            (str "DC: ")]])
    ([:div#error "Fehler: Konnte Schlag mit Nummer: " id " nicht laden!"])))

(defn farms-layout [user-id]
  [:div "all farms"
   #_(he/javascript-tag "weberest.web.views.client.hello_world()")
   (hf/submit-button {:id "xxx"} "press for hello")
   #_(hp/include-js "/cljs/main.js")])

(defn create-farm [user-id farm-data]
  (:id farm-data))

(defn new-farm-layout [user-id]
  (hf/form-to [:post "/farms/new"]
    [:div 
      (hf/label "id" "Betriebsnummer")
      (hf/text-field "id" "111")]
    [:div
      (hf/label "name" "Betriebsname:")
      (hf/text-field "name" "")]
    #_(login-fields user)
    (hf/submit-button "Betrieb erstellen")))


