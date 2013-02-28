(ns weberest.web.views.common
  (:use [hiccup.def :only [defhtml]]
        [hiccup.page :only [include-css include-js html5]]))

(defhtml layout [& content]
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
      "textarea { width: 80%; height: 200px }"
      #_"#field-weather-data { width: 50% }"
      #_".form-table { width: 100%; }"
      #_".form-table th { text-align: left; }"
      #_".form-table h3 { border-bottom: 1px solid #ddd; }"
      #_".form-table .label-cell { vertical-align: top; text-align: right; padding-right: 10px; padding-top: 10px; }"
      #_".form-table td { vertical-align: top; padding-top: 5px; }"
      #_".form-table .checkbox-row label { display: inline; margin-left: 5px; }"
      #_".form-table .checkbox-row .input-shell { margin-bottom: 10px; }"
      #_".form-table .submit-row th, .form-table .submit-row td { padding: 30px 0; }"
      #_".form-table .problem th, .form-table .problem td { color: #b94a48; background: #fee; }"]]
    [:body
     content
     #_[:div#wrapper
        content]]))