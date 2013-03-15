(ns weberest.web.routes
  (:use [compojure.core :as compojure :only [ANY GET POST PUT DELETE defroutes]]
        [clojure.core.match :only [match]]) 
  (:require [weberest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [compojure 
             [handler :as handler]
             [route :as route]]
            [weberest.test-data :as btd]
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
            [ring.middleware.edn :as redn]
            [cemerick.friend :as friend]
            [cemerick.friend  [workflows :as workflows]
                              [credentials :as creds]]))

;;farms
;;----------------------------------------------------------------

(defn- user-id [request]
  (-> request
      friend/current-authentication
      :username))

(defroutes farm-routes
  (compojure/context 
   "/farms" req
   
   (GET "/" req	
        (common/layout+js (farm/farms-layout (user-id req))))
   
   (GET "/:farm-id" [farm-id]
        (common/layout+js 
         (if (= farm-id "new")
           (farm/new-farm-layout (user-id req))
           (farm/farm-layout (user-id req) farm-id))))
   
   ; upon creation of farm go to new farm or failure to input form again
   (POST "/new" {new-farm-data :form-params}
         (if-let [new-farm-id (farm/create-farm (user-id req) new-farm-data)]
           (rur/redirect (str "/farms/" new-farm-id))
           (rur/redirect "/farms/new")))))

;;plots
;;----------------------------------------------------------------

(defroutes plot-routes
  (compojure/context 
   "/farms/:farm-id" [farm-id :as req]
   
   (compojure/context 
    "/plots" []
    
    (GET "/" []
         (common/layout (plot/plots-layout (user-id req) farm-id))))
   
   (GET "/new" []
        (common/layout (plot/new-plot-layout (user-id req) farm-id)))
   
   (POST "/new" {new-plot-data :form-params}
         (if-let [new-plot-id (plot/create-plot (user-id req) farm-id new-plot-data)]
           (rur/redirect (str "/farms/" farm-id "/plots/" new-plot-id))
           (rur/redirect (str "/farms/" farm-id "/plots/new"))))
   
   (GET "/test" []
        (common/layout (plot/test-plot-layout (user-id req) farm-id)))
   
   (POST "/test.csv" {test-data :params}
         (-> (plot/calc-test-plot (user-id req) farm-id test-data)
             rur/response
             (rur/content-type ,,, "text/csv")))
   
   (GET "/:plot-id" [plot-id until]
        (common/layout+js 
         (plot/plot-layout (user-id req) farm-id plot-id 
                           (if until 
                             (Integer/parseInt until)
                             250))))))

(defroutes rest-plot-routes
  (compojure/context 
   "/rest/farms/:farm-id" [farm-id :as req]
   
   (compojure/context 
    "/plots" []
    
    (GET "/" []
         (-> (plot/rest-plot-ids :edn "admin" #_(user-id req) farm-id)
             pr-str
             rur/response
             (#(do (println (str %)) %) ,,,)
             (rur/content-type ,,, "application/edn")))
   
    (GET "/:plot-id" [plot-id format & data]
         (condp = format
           "csv" (-> (plot/calc-plot "berest" #_(user-id req) farm-id plot-id data)
                     rur/response
                     (rur/content-type "text/csv"))
           :else (rur/not-found (str "Format '" format "' is not supported!"))))
   
    
   #_(GET "/new" []
        (common/layout (plot/new-plot-layout (user-id req) farm-id)))
   
   #_(POST "/new" {new-plot-data :form-params}
         (if-let [new-plot-id (plot/create-plot (user-id req) farm-id new-plot-data)]
           (rur/redirect (str "/farms/" farm-id "/plots/" new-plot-id))
           (rur/redirect (str "/farms/" farm-id "/plots/new"))))
   
   #_(GET "/test" []
        (common/layout (plot/test-plot-layout (user-id req) farm-id)))
   
   #_(POST "/test.csv" {test-data :params}
         (-> (plot/calc-test-plot (user-id req) farm-id test-data)
             rur/response
             (rur/content-type "text/csv")))
   
   #_(GET "/:plot-id" [plot-id until]
        (common/layout+js 
         (plot/plot-layout (user-id req) farm-id plot-id 
                           (if until 
                             (Integer/parseInt until)
                             250)))))))

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
   "/crops" []
  
   (GET "/" req
        (common/layout (crop/crops-layout (user-id req))))
   
   (GET "/:crop-id" [crop-id :as req]
        (common/layout 
         (if (= crop-id "new")
           (crop/new-crop-layout (user-id req))
           (crop/crop-layout (user-id req) crop-id))))
   
   (POST "/new" {new-crop-data :form-params :as req}
         (if-let [new-crop-id (crop/create-crop (user-id req) new-crop-data)]
           (rur/redirect (str "/crops/" new-crop-id))
           (rur/redirect "/crops/new")))))

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
   "/users/admin/dbs" []
   
   (GET "/" []	
        (common/layout (db/dbs-layout)))
   
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
        (btd/install-test-data id))))

;;test
;;----------------------------------------------------------------

(defroutes test-routes
  (GET "/webfui" []
       (common/layout-webfui "webfui_client")))

;;start
;;----------------------------------------------------------------

(defroutes start-routes
  (GET "/" []
       (common/layout [:h1 "WeBEREST"])))

(defroutes user-routes
  (route/resources "/")
  rest-plot-routes
  start-routes
  test-routes
  login-out-routes
  (friend/wrap-authorize farm-routes #{::user})
  (friend/wrap-authorize plot-routes #{::user})
  (route/not-found "Page not found."))

(defroutes admin-routes
  start-routes
  
  (route/not-found "404"))

(defroutes rest-routes
  (route/resources "/")
  test-routes
  rest-plot-routes
  (route/not-found "Page not found."))



;;app
;-----------------------------------------------------------------

(def users 
     {"admin" {:username "admin"
               :password (creds/hash-bcrypt "zadmin")
               :roles #{::admin}}
      "berest" {:username "berest"
                :password (creds/hash-bcrypt "zalf")
                :roles #{::user}}
      "dev" {:username "dev"
             :password (creds/hash-bcrypt "zdev")
             :roles #{::admin}}})

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
      redn/wrap-edn-params
      handler/site))

(def berest-rest-api
  (-> rest-routes
      #_(friend/authenticate
       ,,,
       {:allow-anon? true
        :unauthenticated-handler #(workflows/http-basic-deny mock-app-realm %)
        :workflows [(workflows/http-basic
                     :credential-fn (partial creds/bcrypt-credential-fn api-users)
                     :realm mock-app-realm)]})
      redn/wrap-edn-params
      handler/api))

#_(def berest-api
  (-> rest-routes
      (friend/authenticate
       ,,,
       {:allow-anon? true
        :unauthenticated-handler #(workflows/http-basic-deny mock-app-realm %)
        :workflows [(workflows/http-basic
                     :credential-fn (partial creds/bcrypt-credential-fn api-users)
                     :realm mock-app-realm)]})
      
      handler/api))
