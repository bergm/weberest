(defproject berest "1.0.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.4.0"]
                  #_[com.novemberain/monger "1.0.0-beta4"]
                  [org.clojure/math.numeric-tower "0.0.1"]
                  #_[incanter "1.3.0"]
                  [incanter/incanter-core "1.4.0"]
                  [com.datomic/datomic-free "0.8.3538"]
                  [clj-time "0.4.2"]
                  #_[units "0.2.3"]
                  #_[frinj "0.1.3"]
                  #_[unfix "1.0"]
                  [egamble/let-else "1.0.4"]
                  [clojure-csv/clojure-csv "2.0.0-alpha1"]
                  [noir "1.3.0-beta10"]
                  #_[enlive "1.0.1"]
                  #_[the/parsatron "0.0.2"] ;current version directly copied to src
                  #_[org.clojure/tools.macro "0.1.1"]
                  [cheshire "4.0.3"]
                  [clj-info "0.2.6"]
                  [com.keminglabs/c2 "0.2.0"]
                  [org.clojure/core.match "0.2.0-alpha11"]
                  [org.clojars.pallix/analemma "1.0.0"]
                  [com.cemerick/friend "0.1.2"]
                 ]
  :plugins [[lein-cljsbuild "0.2.5"]]
  :cljsbuild {:builds [{; The path to the top-level ClojureScript source directory:
                        :source-path "src-cljs"
                        ; The standard ClojureScript compiler options:
                        ; (See the ClojureScript compiler documentation for details.)
                        :compiler {:output-to "resources/public/cljs/main.js"  ; default: main.js in current directory
                                   :output-dir "resources/public/cljs"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :main ^{:skip-aot true} berest.web.server
  ;:main ^{:skip-aot true} berest.core
  )