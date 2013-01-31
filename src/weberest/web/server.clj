(ns weberest.web.server
  #_(:require #_[noir [server :as server]
                  [core :as nc]]
            [cemerick.friend :as friend]
            (cemerick.friend  [workflows :as workflows]
                              [credentials :as creds])))

#_(server/load-views-ns 'berest.web.views)

#_(def users 
  { "admin" { :username "admin"
              :password (creds/hash-bcrypt "admin")
              :roles #{:admin}}
    "michael" { :username "michael"
                :password (creds/hash-bcrypt "michael")
                :roles #{:user}}})

#_(server/add-middleware
  friend/authenticate
  { :credential-fn (partial creds/bcrypt-credential-fn users)
    :workflows [(workflows/interactive-form)]
    :login-uri "/login"
    :unauthorized-redirect-uri "/login"
    :default-landing-uri "/"})

#_(nc/pre-route [:get ["/:path" :path #"(?!login|logout)*"]] {:as req}
  (friend/authenticated
; We don't need to do anything, we just want to make sure we're
; authenticated.
    nil))

#_(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8090"))]
    (server/start port {:mode mode
                       :ns 'berest.web})))

#_(defonce server (-main))

#_(-main)

