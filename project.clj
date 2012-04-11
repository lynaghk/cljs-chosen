(defproject com.keminglabs/chosen "0.0.1-SNAPSHOT"
  :description "ClojureScript wrapper for Chosen <select> library"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.match "0.2.0-alpha9"]

                 [jayq "0.1.0-alpha3"]]
  :source-paths ["src/cljs"]
  :resource-paths ["resources"]


  :plugins [[lein-cljsbuild "0.1.6"]]
  :cljsbuild {:builds
              [{:source-path "test"
                :compiler {:pretty-print true
                           :output-to "public/test.js"
                           :optimizations :simple}
                :jar false}]})
