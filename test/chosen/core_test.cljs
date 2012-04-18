(ns chosen.core-test
  (:use [chosen.core :only [ichooseu! selected options]]
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

(let [d (ichooseu! $select)]
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
    (assert (= (selected d) @d)
            "deref is the same as selected")))






(def $multi-select (let [el (gdom/htmlToDocumentFragment "<select style='width: 100px' multiple='multiple'></select>")]
                     (gdom/appendChild main el)
                     ($ el)))

(let [d (ichooseu! $multi-select)]
  (let [raw-opts ["a" "b" "c"]]
    (options d raw-opts)

    (assert (= 3 (-> $multi-select (.children) (.-length)))
            "set raw opts")
    (assert (= raw-opts (map :value (options d)))
            "get raw opts"))


  (let [opts [{:text "A" :value "1"}
              {:text "B" :value "2"}]]
    (options d opts)
    (assert (= 2 (-> $multi-select (.children) (.-length)))
            "set opts")
    
    (options d [])
    (assert (= 0 (-> $multi-select (.children) (.-length)))
            "clear opts")
    
    (options d (set opts))
    (assert (= 2 (-> $multi-select (.children) (.-length)))
            "set opts with set")

    (options d opts)
    (assert (= (map :value opts)
               (map :value (options d)))
            "get opts")

    (selected d (:value (second opts)))
    (let [res (selected d)]
      (assert (set? res) "result from multi-select is a set")
      (assert (= 1 (count res)))
      (assert (res (:value (second opts))) "multi-select result contains selected option"))


    (selected d (map :value opts))
    (let [res (selected d)]
      (assert (set? res) "result from multi-select is a set")
      (assert (= 2 (count res)))
      (assert (= (set (map :value opts)) res)
              "select both options by value"))


    (selected d (map :value opts)) ;;select both options
    (options d (first opts)) ;;then remove one
    (assert ((selected d) (:value (first opts)))
            "selected option removed from selection if it's removed from the DOM")
    (assert (= (selected d) @d)
            "deref is the same as selected")))



(p "Hurray! All tests passed.")
(p "__exit__")
