(ns weberest.web.routes
  (:use [compojure.core :as compojure :only [ANY GET POST PUT DELETE defroutes]]
        [clojure.core.match :only [match]]) 
  (:require [weberest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [compojure 
             [handler :as handler]
             [route :as route]]
            [weberest.web.views 
             [farm :as farm]
             [db :as db] 
             [plot :as plot]
             #_[climate :as climate]
             #_[technology :as tech]
             [crop :as crop]
             #_[settings :as set]
             [login :as login]]
            [ring.util.response :as rur]
            [ring.middleware.content-type :as rmc]
            [cemerick.friend :as friend]
            [cemerick.friend  [workflows :as workflows]
                              [credentials :as creds]]))

;;farms
;;----------------------------------------------------------------

(defroutes farm-routes
  (compojure/context 
    "/farms" request
    
    (GET "/:farm-id" [farm-id]
         ;(friend/authorize #{::user}
         (friend/authenticated
           (common/layout 
             (if (= farm-id "new")
               (farm/new-farm-layout)
               (farm/farm-layout farm-id)))))
    
    ; upon creation of farm go to new farm or failure to input form again
    (POST "/new" {new-farm-data :form-params}
          (friend/authenticated
            (if-let [new-farm-id (farm/create-farm new-farm-data)]
              (rur/redirect (str "/farms/" new-farm-id))
              (rur/redirect "/farms/new")))))
  
  (GET "/farms" []
       (friend/authenticated 
         (common/layout (farm/farms-layout)))))
  
;;plots
;;----------------------------------------------------------------

(defroutes plot-routes
  (compojure/context 
    "/farms/:farm-id" [farm-id]
    
    (compojure/context 
      "/plots" []
      
      (GET "/new" []
           (common/layout (plot/new-plot-layout farm-id)))
      
      (POST "/new" {new-plot-data :form-params}
            (friend/authenticated
              (if-let [new-plot-id (plot/create-plot farm-id new-plot-data)]
                (rur/redirect (str "/farms/" farm-id "/plots/" new-plot-id))
                (rur/redirect (str "/farms/" farm-id "/plots/new")))))
            
      (GET "/test" []
           ;(friend/authenticated
            (common/layout (plot/test-plot-layout farm-id)));)
      
      (POST "/test.csv" {test-data :params}
            ;(friend/authenticated
             (-> (plot/calc-test-plot farm-id test-data)
                 rur/response
                 (rur/content-type "text/csv")));)
      
      (GET "/:plot-id" [plot-id until]
           (friend/authenticated
             (common/layout 
              (plot/plot-layout plot-id 
                                (if until 
                                    (Integer/parseInt until)
                                    250))))))
            
    (GET "/plots" {:keys [farm-id]}
         (friend/authenticated
           (common/layout (plot/plots-layout farm-id))))))

;;climate
;;----------------------------------------------------------------

#_(nc/defpage "/climate-stations/:station-id" {:keys [station-id]}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (if (= station-id "new")
        (climate/new-climate-station-layout)
        (climate/climate-station-layout station-id)))))

; upon creation of farm go to new farm or failure to input form again
#_(nc/defpage [:post "/climate-stations/new"] {:as new-cs-data}
  (friend/authenticated
    (if-let [new-cs-id (climate/create-climate-station new-cs-data)]
      (nresponse/redirect (str "/climate-stations/" new-cs-id))
      (nc/render "/climate-stations/new"))))

#_(nc/defpage "/climate-stations" {:keys []}
  (friend/authenticated 
  (common/layout (climate/climate-stations-layout))))


;;crops
;;----------------------------------------------------------------

(defroutes 
 crop-routes
 
 (compojure/context 
  "/users/:user-id" [user-id]
  
  (GET "/crops/:crop-id" [crop-id]
       ;(friend/authorize #{:user}
       (friend/authenticated
        (common/layout 
         (if (= crop-id "new")
             (crop/new-crop-layout)
             (crop/crop-layout crop-id)))))
  
  (POST "/crops/new" {new-crop-data :form-params}
        (friend/authenticated
         (if-let [new-crop-id (crop/create-crop new-crop-data)]
                 (rur/redirect (str "/crops/" new-crop-id))
                 (rur/redirect "/crops/new"))))
  
  (GET "/crops" []
       (friend/authenticated 
        (common/layout (crop/crops-layout))))))

;;technology
;;----------------------------------------------------------------

#_(nc/defpage "/technologies/:tech-id" {:keys [tech-id]}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (if (= tech-id "new")
        (tech/new-technology-layout)
        (tech/technology-layout tech-id)))))

; upon creation of farm go to new farm or failure to input form again
#_(nc/defpage [:post "/technologies/new"] {:as new-tech-data}
  (friend/authenticated
    (if-let [new-tech-id (tech/create-technology new-tech-data)]
      (nresponse/redirect (str "/technologies/" new-tech-id))
      (nc/render "/technologies/new"))))

#_(nc/defpage "/technologies" {:keys []}
  (friend/authenticated 
  (common/layout (tech/technologies-layout))))

;;settings
;;----------------------------------------------------------------

#_(nc/defpage "/settings" {:keys []}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (set/settings-layout))))

; upon creation of farm go to new farm or failure to input form again
#_(nc/defpage [:post "/settings"] {:as new-settings}
  (friend/authenticated
    (if (set/save-settings new-settings)
      (nc/render "/settings")
      (nc/render "/settings"))))

;;login / logout
;;----------------------------------------------------------------

(defroutes login-out-routes
  (GET "/login" {user :form-params}
        (common/layout (login/layout user)))

  #_(POST "/login" {p :params} (prn p))
  
  (friend/logout 
    (ANY "/logout" request 
         (rur/redirect "/")))
  
  #_(GET "/logout" []
       (friend/logout (fn [_] 
                        (nsession/clear!)
                        (nresponse/redirect "/login")))))

;;admin only
;;---------------------------------------------------------------

(defroutes db-ops-routes
  (compojure/context 
    "/dbs" []
    
    (GET "/new" []
         (common/layout (db/new-db-layout)))
    
    (POST "/new" {new-db-data :form-params}
          (friend/authenticated
            (if-let [new-db-id (db/create-db new-db-data)]
              (rur/redirect (str "/dbs/" new-db-id))
              (rur/redirect (str "/dbs/")))))
    
    (GET "/:db" [id]
         (common/layout (db/db-layout id)))
    
    (DELETE "/:id" [id :as request]
            (if (db/delete-db id)
              (rur/redirect (str "/dbs"))
              (db/delete-error request)))
    
    (PUT "/:id/test-data" [id]
         (db/install-test-data id)))
  
  (GET "/dbs" []
       (common/layout (db/dbs-layout))))

;;start
;;----------------------------------------------------------------

(defroutes start-routes
  (GET "/" []
       (common/layout [:h1 "WeBEREST"])))

(defroutes user-routes
  start-routes
  login-out-routes
  farm-routes
  plot-routes
  (route/resources "/")
  (route/not-found "Page not found."))

(defroutes admin-routes
  start-routes
  
  (route/not-found "404"))

;;test
;;----------------------------------------------------------------

#_(nc/defpage "/bla" []
  (rur/redirect "http://www.web.de"))

;;app
;-----------------------------------------------------------------

(def users 
  {"admin" {:username "admin"
            :password (creds/hash-bcrypt "admin")
            :roles #{::admin}}
   "michael" {:username "michael"
              :password (creds/hash-bcrypt "michael")
              :roles #{::user}}})

(derive ::admin ::user)

(def api-users 
  {"api-key" {:username "api-key"
              :password (creds/hash-bcrypt "api-pass")
              :roles #{::api}}})

(def berest-app
  (-> user-routes 
      (friend/authenticate
       ,,,
       {:credential-fn (partial creds/bcrypt-credential-fn users)
        :workflows [(workflows/interactive-form)]
        :login-uri "/login"
        :unauthorized-redirect-uri "/login"
        :default-landing-uri "/"})
      handler/site))

#_(def berest-api
  (-> api-routes
      (friend/authenticate
       ,,,
       {:allow-anon? true
        :unauthenticated-handler #(workflows/http-basic-deny mock-app-realm %)
        :workflows [(workflows/http-basic
                     :credential-fn (partial creds/bcrypt-credential-fn api-users)
                     :realm mock-app-realm)]})
      handler/api))
