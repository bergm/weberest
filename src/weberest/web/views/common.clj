(ns weberest.web.views.common
  (:use [hiccup.def :only [defhtml]]
        [hiccup.page :only [include-css include-js html5]]))

(defhtml layout [& content]
  (html5 {:xml? true}
    [:head
     [:title "Berest"]
     (include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.0/css/bootstrap-combined.min.css")
     [:style
      "body { margin: 2em; }"
      "textarea { width: 80%; height: 200px }"]]
    [:body
     content]))

(defhtml layout+js [& content]
  (html5 {:xml? true}
    [:head
     [:title "Berest"]
     (include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.0/css/bootstrap-combined.min.css")
     #_(include-css "/css/reset.css")
     #_(include-js "/js/g.raphael-min.js")
     #_(include-js "/js/g.line-min.js")
     (include-js "/cljs/main.js")
     [:style
      "body { margin: 2em; }"
      "textarea { width: 80%; height: 200px }"]]
    [:body
     content]))