(ns destined.core-test
  (:use [destined.core :only [destined selected options]]))

(defn p [x]
  (do (.log js/console x)
      x))
(defn pp [x]
  (do (.log js/console (pr-str x))
      x))


(p "hello there")
#_(let [c (destined "#test")]
  (set! (.-o js/window)
        #(pp (selected c)))

  (set! (.-reselect js/window)
        #(selected c "D"))

  (set! (.-m js/window)
        #(pp (options c  [{:text "1" :value 1}
                          {:text "2" :value 2 :group "Awesome"}
                          {:text "3" :value 3  :group "Awesome"}])))

  (add-watch c :test #(pp %)))
