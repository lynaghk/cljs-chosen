(defproject com.keminglabs/chosen "0.1.7-SNAPSHOT"
  :description "ClojureScript wrapper for Chosen <select> library"
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :source-paths ["src/cljs"]
  :resource-paths ["resources"]


  :plugins [[lein-cljsbuild "0.2.5"]]
  :cljsbuild {:builds
              {:test {:source-path "test"
                      :compiler {:output-to "public/test.js"
                                 :optimizations :advanced
                                 :externs ["resources/closure-js/externs/cljs-chosen/chosen-externs.js"
                                           "resources/closure-js/externs/cljs-chosen/jquery-1.7.2-externs.js"]}
                      :jar false}}
              :test-commands {"integration" ["phantomjs"
                                             "test/runner.coffee"]}})
