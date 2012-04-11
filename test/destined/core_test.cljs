(ns destined.core-test
  (:use [destined.core :only [destine! selected options]]
        [jayq.core :only [$]])
  (:require [goog.dom :as gdom]))

(defn p [x]
  (do (.log js/console x)
      x))
(defn pp [x]
  (do (.log js/console (pr-str x))
      x))

(def main (.querySelector js/document "#main"))
(def $select (let [el (gdom/htmlToDocumentFragment "<select style='width: 100px'></select>")]
               (gdom/appendChild main el)
               ($ el)))

(let [d (destine! $select)]
  (let [raw-opts ["a" "b" "c"]]
    (options d raw-opts)

    (assert (= 3 (-> $select (.children) (.-length)))
            "set raw opts")
    (assert (= raw-opts (map :value (options d)))
            "get raw opts"))


  (let [opts [{:text "A" :value "1"}
              {:text "B" :value "2"}]]
    (options d opts)
    (assert (= 2 (-> $select (.children) (.-length)))
            "set opts")
    (assert (= (map :value opts)
               (map :value (options d)))
            "get opts")

    (selected d (:value (second opts)))
    (assert (= (:value (second opts))
               (selected d))
            "select option by value")



    )






  )








#_(def $multi-select (let [el (gdom/htmlToDocumentFragment "<select multiple='multiple'></select>")]
                       (gdom/appendChild main el)
                       ($ el)))

