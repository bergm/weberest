(ns berest.web.views.routes
  (:require [berest.web.views.common :as common]
            #_[net.cgrand.enlive-html :as html]
            [noir [core :as nc]
                  [session :as nsession]
                  [response :as nresponse]]
            [berest.web.views [farm :as farm]
                              [plot :as plot]
                              [climate :as climate]
                              [technology :as tech]
                              [crop :as crop]
                              [settings :as set]]
            [berest.web.views.login :as login]
            [ring.util.response :as rur]
            [cemerick.friend :as friend]))

;;farms
;;----------------------------------------------------------------

(nc/defpage "/farms/:farm-id" {:keys [farm-id]}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (if (= farm-id "new")
        (farm/new-farm-layout)
        (farm/farm-layout farm-id)))))

; upon creation of farm go to new farm or failure to input form again
(nc/defpage [:post "/farms/new"] {:as new-farm-data}
  (friend/authenticated
    (if-let [new-farm-id (farm/create-farm new-farm-data)]
      (nresponse/redirect (str "/farms/" new-farm-id))
      (nc/render "/farms/new"))))

(nc/defpage "/farms" {:keys []}
  (friend/authenticated 
  (common/layout (farm/farms-layout))))

;;plots
;;----------------------------------------------------------------

(nc/defpage "/farms/:farm-id/plots/:plot-id" {:keys [farm-id plot-id until]}
  (friend/authenticated
    (common/layout 
      (if (= plot-id "new")
        (plot/new-plot-layout farm-id)
        (plot/plot-layout plot-id 
                          (if until 
                            (Integer/parseInt until)
                            250))))))

(nc/defpage [:post "/farms/:farm-id/plots/new"] 
  {:keys [farm-id] :as new-plot-data}
  (friend/authenticated
    (if-let [new-plot-id (plot/create-plot farm-id new-plot-data)]
      (nresponse/redirect (str "/farms/" farm-id "/plots/" new-plot-id))
      (nresponse/redirect (str "/farms/" farm-id "/plots/new")))))

(nc/defpage "/farms/:farm-id/plots" {:keys [farm-id]}
  (friend/authenticated
    (common/layout (plot/plots-layout farm-id))))

;;climate
;;----------------------------------------------------------------

(nc/defpage "/climate-stations/:station-id" {:keys [station-id]}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (if (= station-id "new")
        (climate/new-climate-station-layout)
        (climate/climate-station-layout station-id)))))

; upon creation of farm go to new farm or failure to input form again
(nc/defpage [:post "/climate-stations/new"] {:as new-cs-data}
  (friend/authenticated
    (if-let [new-cs-id (climate/create-climate-station new-cs-data)]
      (nresponse/redirect (str "/climate-stations/" new-cs-id))
      (nc/render "/climate-stations/new"))))

(nc/defpage "/climate-stations" {:keys []}
  (friend/authenticated 
  (common/layout (climate/climate-stations-layout))))


;;crops
;;----------------------------------------------------------------

(nc/defpage "/crops/:crop-id" {:keys [crop-id]}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (if (= crop-id "new")
        (crop/new-crop-layout)
        (crop/crop-layout crop-id)))))

; upon creation of farm go to new farm or failure to input form again
(nc/defpage [:post "/crops/new"] {:as new-crop-data}
  (friend/authenticated
    (if-let [new-crop-id (crop/create-crop new-crop-data)]
      (nresponse/redirect (str "/crops/" new-crop-id))
      (nc/render "/crops/new"))))

(nc/defpage "/crops" {:keys []}
  (friend/authenticated 
  (common/layout (crop/crops-layout))))

;;technology
;;----------------------------------------------------------------

(nc/defpage "/technologies/:tech-id" {:keys [tech-id]}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (if (= tech-id "new")
        (tech/new-technology-layout)
        (tech/technology-layout tech-id)))))

; upon creation of farm go to new farm or failure to input form again
(nc/defpage [:post "/technologies/new"] {:as new-tech-data}
  (friend/authenticated
    (if-let [new-tech-id (tech/create-technology new-tech-data)]
      (nresponse/redirect (str "/technologies/" new-tech-id))
      (nc/render "/technologies/new"))))

(nc/defpage "/technologies" {:keys []}
  (friend/authenticated 
  (common/layout (tech/technologies-layout))))

;;settings
;;----------------------------------------------------------------

(nc/defpage "/settings" {:keys []}
  ;(friend/authorize #{:user}
  (friend/authenticated
    (common/layout 
      (set/settings-layout))))

; upon creation of farm go to new farm or failure to input form again
(nc/defpage [:post "/settings"] {:as new-settings}
  (friend/authenticated
    (if (set/save-settings new-settings)
      (nc/render "/settings")
      (nc/render "/settings"))))

;;login / logout
;;----------------------------------------------------------------

(nc/defpage "/login" {:as user}
  (common/layout (login/layout user)))

#_(nc/defpage [:post "/login"] {:as user}
  (if (login/valid? user)
    (common/layout
      [:p "User added!"])    
    (nc/render "/login")))

(nc/custom-handler "/logout" []
  (friend/logout (fn [_] 
                    (nsession/clear!)
                    (nresponse/redirect "/login"))))

;;start
;;----------------------------------------------------------------

(nc/defpage "/" []
  (common/layout [:h1 "WeBEREST"]))


;;test
;;----------------------------------------------------------------

#_(nc/defpage "/bla" []
  (rur/redirect "http://www.web.de"))

