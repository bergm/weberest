(ns weberest.web.server
    (:require [weberest.web.routes :as routes]
              [ring.adapter.jetty :as jetty]))

(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
       (jetty/run-jetty routes/berest-app {:port port})))
