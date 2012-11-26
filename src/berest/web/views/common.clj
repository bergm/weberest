(ns berest.web.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

(defpartial layout [& content]
  (html5
    [:head
      [:title "Berest"]
      #_(include-css "/css/reset.css")
      #_(include-js "/js/g.raphael-min.js")
      #_(include-js "/js/g.line-min.js")]
      [:body
        content
        #_[:div#wrapper
          content]]
      #_(include-js "/cljs/main.js")))