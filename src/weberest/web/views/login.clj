(ns weberest.web.views.login
  (:require
    clojure.repl
    [weberest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [hiccup
             [def :as hd]
             [form :as hf]]
            [weberest.core :as bc]
            #_[bouncer [core :as boc] [validators :as bov]]
            #_[noir 
             [validation :as vali]]))

(defn error-item [[first-error]]
  [:p.error first-error])

;(hd/defelem login-fields [{:keys [username password]}]
(defn login-fields [{:keys [username password]}]
  [:div 
   [:div 
    #_(vali/on-error :username error-item)
    (hf/label "username" "Nutzername: ")
    (hf/text-field "username" username)]
   [:div 
    #_(vali/on-error :password error-item)  
    (hf/label "password" "Passwort: ")
    (hf/password-field "password" password)]])

(defn layout [user]
  (hf/form-to [:post "/login"]
              (login-fields (if user user {:username "xxx" :password "yyyy"}))
              (hf/submit-button "Anmelden")))

#_(defn valid? [{:keys [username password]}]
  (vali/rule (vali/min-length? username 3)
             [:username "Nutzername muss mindestens 3 Zeichen lang sein."])
  (vali/rule (vali/min-length? password 6)
             [:password "Passwort muss mindestens 6 Zeichen lang sein."])
  (not (vali/errors? :username :password)))
