(ns weberest.web.views.crop
  (:require [weberest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [weberest.core :as bc]
            #_[noir [core :as nc]
                  [validation :as vali]]
            [hiccup.form :as hf]))

(defn crop-layout [id]
  [:div "crop no " id]
  #_(if-let [plot (bc/db-read-plot id)]
    (let []
      [:h1 (str "Schlag: " id)]
        [:div#plotData 
          [:div#currentDCData 
            (str "DC: ")]])
    ([:div#error "Fehler: Konnte Schlag mit Nummer: " id " nicht laden!"])))

(defn crops-layout []
  [:div "all crops"])

(defn create-crop [crop-data]
  (:id crop-data))

(defn new-crop-layout []
  (hf/form-to [:post "/crops/new"]
    [:div 
      (hf/label "id" "Fruchtartnummer")
      (hf/text-field "id" "111")]
    [:div
      (hf/label "name" "Fruchtartname:")
      (hf/text-field "name" "")]
    #_(login-fields user)
    (hf/submit-button "Fruchtart erstellen")))

