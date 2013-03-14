(defproject berest "1.0.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.1"]
                 [incanter/incanter-core "1.4.0"]
                 [com.datomic/datomic-free "0.8.3704"]
                 [clj-time "0.4.2"]
                 [egamble/let-else "1.0.4"]
                 [clojure-csv/clojure-csv "2.0.0-alpha1"]
                 #_[org.clojure/data.csv "0.1.2"]
                 [compojure "1.1.3"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-jetty-adapter "0.3.8"]
                 [lib-noir "0.3.3"]
                 [hiccup "1.0.2"]
                 #_[enlive "1.0.1"]
                 #_[the/parsatron "0.0.2"] ;current version directly copied to src
                 #_[org.clojure/tools.macro "0.1.1"]
                 [cheshire "4.0.3"]
                 [clj-info "0.2.6"]
                 [com.keminglabs/c2 "0.2.0"]
                 [org.clojure/core.match "0.2.0-alpha11"]
                 [org.clojars.pallix/analemma "1.0.0"]
                 [com.cemerick/friend "0.1.3"]
                 [org.clojure/algo.generic "0.1.0"]
                 [bouncer "0.2.0"]
                 [enfocus "1.0.0-beta2"]
                 [prismatic/dommy "0.0.1"]
                 [domina "1.0.0"]
                 [org.clojure/google-closure-library-third-party "0.0-2029"]
                 [formative "0.3.2"]
                 [webfui "0.2.1"]
                 [ring-edn "0.1.0"]
                 ]
  :exclusions [org.mortbay.jetty/servlet-api]              
  :plugins [[lein-cljsbuild "0.3.0"]]
  :cljsbuild {:builds [#_{:id "main"
                        ; The path to the top-level ClojureScript source directory:
                        :source-paths ["src"]
                        ;:source-path "src"
                        ; The standard ClojureScript compiler options:
                        ; (See the ClojureScript compiler documentation for details.)
                        :compiler {:output-to "resources/public/cljs/main.js"  ; default: main.js in current directory
                                   :output-dir "resources/public/cljs"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       #_{:id "calculator-ajax"
                        :source-path "src-cljs/calculator_ajax"
                        :compiler {:output-to "resources/public/js/calculator_ajax.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       {:id "webfui-client"
                        :source-paths ["src/weberest/web/clients/webfui"]
                        ;:source-path "src/weberest/web/clients/webfui"
                        :compiler {:output-to "resources/public/js/webfui_client.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :ring {;:handler weberest.web.routes/berest-app
         :handler weberest.web.routes/berest-rest-api
         :auto-refresh? true
         }
  ;:main ^{:skip-aot true} weberest.web.server
  ;:main ^{:skip-aot true} weberest.core
  )