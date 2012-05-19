(defproject com.keminglabs/chosen "0.1.5"
  :description "ClojureScript wrapper for Chosen <select> library"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.match "0.2.0-alpha9"]

                 [jayq "0.1.0-alpha4"]]
  :source-paths ["src/cljs"]
  :resource-paths ["resources"]


  :plugins [[lein-cljsbuild "0.1.10"]]
  :cljsbuild {:builds
              {:test {:source-path "test"
                      :compiler {:output-to "public/test.js"
                                 :optimizations :advanced
                                 :externs ["resources/chosen-externs.js" "externs/jquery.js"]}
                      :jar false}}
              :test-commands {"integration" ["phantomjs"
                                             "test/runner.coffee"]}})
