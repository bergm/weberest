(ns berest.web.views.login
  (:require [berest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [hiccup.form :as hf]
            [berest.bcore :as bc]
            [noir [core :as nc]
                  [validation :as vali]]))

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

(nc/defpartial layout [user]
  (hf/form-to [:post "/login"]
    (login-fields user)
    (hf/submit-button "Anmelden")))

(defn valid? [{:keys [username password]}]
  (vali/rule (vali/min-length? username 3)
             [:username "Nutzername muss mindestens 3 Zeichen lang sein."])
  (vali/rule (vali/min-length? password 6)
             [:password "Passwort muss mindestens 6 Zeichen lang sein."])
  (not (vali/errors? :username :password)))
