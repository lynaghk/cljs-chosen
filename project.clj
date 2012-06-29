(defproject com.keminglabs/chosen "0.1.7-SNAPSHOT"
  :description "ClojureScript wrapper for Chosen <select> library"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [jayq "0.1.0-alpha4"]]
  :source-paths ["src/cljs"]
  :resource-paths ["resources"]


  :plugins [[lein-cljsbuild "0.2.2"]]
  :cljsbuild {:builds
              {:test {:source-path "test"
                      :compiler {:output-to "public/test.js"
                                 :optimizations :advanced
                                 :externs ["resources/closure-js/externs/cljs-chosen/chosen-externs.js" "externs/jquery.js"]}
                      :jar false}}
              :test-commands {"integration" ["phantomjs"
                                             "test/runner.coffee"]}})
