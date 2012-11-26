(ns berest.web.views.farm
  (:require [berest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [berest.bcore :as bc]
            [noir [core :as nc]
                  [validation :as vali]]
            [hiccup.form :as hf]))

(nc/defpartial farm-layout [id]
  [:div "farm no " id]
  #_(if-let [plot (bc/db-read-plot id)]
    (let []
      [:h1 (str "Schlag: " id)]
        [:div#plotData 
          [:div#currentDCData 
            (str "DC: ")]])
    ([:div#error "Fehler: Konnte Schlag mit Nummer: " id " nicht laden!"])))

(nc/defpartial farms-layout []
  [:div "all farms"])

(defn create-farm [farm-data]
  (:id farm-data))

(nc/defpartial error-item [[first-error]]
  [:p.error first-error])

(nc/defpartial login-fields [{:keys [username password]}]
  [:div 
    (vali/on-error :username error-item)
    (hf/label "username" "Nutzername: ")
    (hf/text-field "username" username)]
  [:div 
    (vali/on-error :password error-item)  
    (hf/label "password" "Passwort: ")
    (hf/password-field "password" password)])

(nc/defpartial new-farm-layout []
  (hf/form-to [:post "/farms/new"]
    [:div 
      (hf/label "id" "Betriebsnummer")
      (hf/text-field "id" "111")]
    [:div
      (hf/label "name" "Betriebsname:")
      (hf/text-field "name" "")]
    #_(login-fields user)
    (hf/submit-button "Betrieb erstellen")))

(defn valid? [{:keys [username password]}]
  (vali/rule (vali/min-length? username 3)
             [:username "Nutzername muss mindestens 3 Zeichen lang sein."])
  (vali/rule (vali/min-length? password 6)
             [:password "Passwort muss mindestens 6 Zeichen lang sein."])
  (not (vali/errors? :username :password)))


