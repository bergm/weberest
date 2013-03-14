(ns weberest.web.views.db
  (:require [weberest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [weberest 
             [datomic :as bd]
             [test-data :as btd]
             [helper :as bh]]
            [clj-time.core :as ctc]
            [clojure.java.io :as cjio]
            [hiccup 
             [element :as he]
             [def :as hd]
             [form :as hf]]
            [datomic.api :as d]))

(defn db-layout [id]
  [:div "db no " id]
  #_(if-let [plot (bc/db-read-plot id)]
    (let []
      [:h1 (str "Schlag: " id)]
        [:div#plotData 
          [:div#currentDCData 
            (str "DC: ")]])
    ([:div#error "Fehler: Konnte Schlag mit Nummer: " id " nicht laden!"])))

(defn dbs-layout []
  [:div "all dbs"])

(defn create-db [{:keys [id]}]
  (bd/create-db id))

(defn delete-db [id]
  (d/delete-database (str bd/datomic-base-uri id)))

(defn delete-error [id continue-url]
  [:div 
   [:p.error (str "Konnte Datenbank " id " nicht löschen!")]
   (he/link-to "" (str continue-url) #_"Zurück zur Übersicht")])

(defn new-db-layout []
  (hf/form-to [:post "/dbs/new"]
    [:div 
      (hf/label "id" "DB idBetriebsnummer")
      (hf/text-field "id" "111")]
    [:div
      (hf/label "name" "DB-Name")
      (hf/text-field "name" "")]
    #_(login-fields user)
    (hf/submit-button "DB erstellen")))



