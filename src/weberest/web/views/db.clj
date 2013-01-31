(ns weberest.web.views.db
  (:require [weberest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [weberest 
             [datomic :as bd]
             [test-data :as btd]
             [helper :as bh]]
            [clj-time.core :as ctc]
            [clojure.java.io :as cjio]
            #_[noir 
             [validation :as vali]]
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

(defn apply-schema-to-db [datomic-connection]
  (d/transact datomic-connection
              (-> "private/db/berest-meta-schema.dtm"
                cjio/resource 
                slurp
                read-string))
  
  (d/transact datomic-connection 
              (-> "private/db/berest-schema.dtm"
                cjio/resource 
                slurp
                read-string)))

(defn create-db [{:keys [id] :as db-data}]
  (let [uri (str bd/datomic-base-uri id)] 
    (when (d/create-database uri)
      (apply-schema-to-db (d/connect uri)))
    id))

(defn install-test-data [id]
  (let [uri (str bd/datomic-base-uri id)]
       (bh/juxt* btd/add-maize
               btd/add-potato
               btd/add-winter-wheat
               btd/add-fallow
               (-> uri d/connect d/db))
    (btd/add-plot (-> uri d/connect d/db))))

(defn delete-db [id]
  (d/delete-database (str bd/datomic-base-uri id)))

(defn delete-error [id continue-url]
  [:div 
   [:p.error (str "Konnte Datenbank " id " nicht löschen!")]
   (he/link-to "" (str continue-url) #_"Zurück zur Übersicht")])

#_(hd/defelem error-item [[first-error]]
  [:p.error first-error])

#_(hd/defelem login-fields [{:keys [username password]}]
  [:div 
    (vali/on-error :username error-item)
    (hf/label "username" "Nutzername: ")
    (hf/text-field "username" username)]
  [:div 
    (vali/on-error :password error-item)  
    (hf/label "password" "Passwort: ")
    (hf/password-field "password" password)])

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

#_(defn valid? [{:keys [username password]}]
  (vali/rule (vali/min-length? username 3)
             [:username "Nutzername muss mindestens 3 Zeichen lang sein."])
  (vali/rule (vali/min-length? password 6)
             [:password "Passwort muss mindestens 6 Zeichen lang sein."])
  (not (vali/errors? :username :password)))


