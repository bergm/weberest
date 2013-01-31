(ns weberest.web.views.common
  (:use [hiccup.def :only [defhtml]]
        [hiccup.page :only [include-css include-js html5]]))

(defhtml layout [& content]
  (html5 {:xml? true}
    [:head
     [:title "Berest"]
     #_(include-css "/css/reset.css")
     #_(include-js "/js/g.raphael-min.js")
     #_(include-js "/js/g.line-min.js")
     (include-js "/cljs/main.js")]
    [:body
     content
     #_[:div#wrapper
        content]]))