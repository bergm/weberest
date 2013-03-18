(ns weberest.web.views.common
    (:require [hiccup
               [element :as he]
               [page :as hp]
               [def :as hd]]))

(hd/defhtml layout [& content]
  (hp/html5 {:xml? true}
    [:head
     [:title "Berest"]
     (hp/include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.0/css/bootstrap-combined.min.css")
     [:style
      "body { margin: 2em; }"
      "textarea { width: 80%; height: 200px }"]]
    [:body
     content]))

(hd/defhtml layout+js [& content]
  (hp/html5 {:xml? true}
    [:head
     [:title "Berest"]
     (hp/include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.0/css/bootstrap-combined.min.css")
     #_(include-css "/css/reset.css")
     #_(include-js "/js/g.raphael-min.js")
     #_(include-js "/js/g.line-min.js")
     (hp/include-js "/cljs/main.js")
     [:style
      "body { margin: 2em; }"
      "textarea { width: 80%; height: 200px }"]]
    [:body
     content]))

(hd/defhtml layout-webfui [name]
         (hp/html5
          [:head
           [:title "webfui-client"]
           #_(hp/include-css (str "/css/" name ".css"))
           #_(he/javascript-tag "var CLOSURE_NO_DEPS = true;")
           (hp/include-js (str "/cljs/" name ".js"))]
          [:body]))
