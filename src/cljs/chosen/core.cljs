(ns chosen.core
  (:use-macros [clojure.core.match.js :only [match]])
  (:use [jayq.core :only [$]]
        [clojure.string :only [join]])
  (:require [goog.string :as gstring]))

(defprotocol ISelectable
  (selected
    [this]
    [this values]))
(defprotocol IOptions
  (options
    [this]
    [this opts]))

(defn- ->coll [c]
  (if-not (or (set? c) (sequential? c))
    [c] c))

(defn- option-values [option-els]
  (map #(.-value %) option-els))

(defn- selected-values [el]
  (set (option-values (.find ($ el) "option:selected"))))

(defn- el-options [el]
  (map (fn [e]
         (let [$e ($ e)
               group (-> $e (.parent "optgroup") (.attr "label"))]
           {:text (.text $e)
            :value (.val $e)
            :selected (.-selected e)
            :disabled (.-disabled e)
            :group (if (undefined? group) nil group)}))
       (.find ($ el) "option")))

;;Hiccup would be nice, but I don't want to add it as a dependency.
(defn- opt->html [o]
  (let [{:keys [value text selected disabled group]} o]
    (str (join " " ["<option"
                    (str "value=\"" (gstring/htmlEscape (or value text)) "\"")
                    (if disabled "disabled='disabled'")
                    (if selected "selected='selected'")
                    ">"])
         text "</option>")))

(defn- optionify
  "Turns a val into a map you can pass to opts-html."
  [x] (if (map? x) x {:value x :text (str x)}))

(defn- reset-dom-options! [$el options]
  ;;Remove old options
  (-> $el (.children) (.remove))
  ;;Insert new options
  (doseq [[group opts] (group-by :group (map optionify (->coll options)))]
    (let [opts-html (join "\n" (map opt->html opts))]
      (if (nil? group)
        ;;just append options
        (-> $el (.append opts-html))
        ;;otherwise, options within an <optgroup>.
        (-> $el (.append (str "<optgroup label='" group "'>" opts-html "</optgroup>")))))))

(deftype Chosen [$el !a multiple?]
  ISelectable
  (selected [_]
    (let [sel (:selected @!a)]
      (if multiple? sel (first sel))))
  (selected [_ values]
    (swap! !a assoc :selected (->coll values)))

  IOptions
  (options [_]
    (:options @!a))
  (options [_ opts]
    (reset-dom-options! $el opts)
    (swap! !a assoc :options (el-options $el)))

  IDeref
  (-deref [this] (selected this))

  ;;Proxy to internal atom.
  ;;Is implementing IWatchable a good idea?
  IWatchable
  (-notify-watches [_ _ _])
  (-add-watch [_ key f]
    ;;Only call watchers when selection changes
    (add-watch !a key (fn [_ _ {old-sel :selected} {sel :selected}]
                        (when (not= old-sel sel)
                          (f (if multiple? sel (first sel)))))))
  (-remove-watch [_ key]
    (remove-watch !a key)))

(defn ichooseu!
  "Turn <select> element (or selector string) el into a Chosen selector."
  [el & {:keys [search-contains]
         :or {search-contains false}}]
  (let [$el ($ el)
        multiple? (= "multiple" (.attr $el "multiple"))
        !a (atom {:options (el-options $el)
                  :selected (selected-values $el)})]

    (-> $el (.chosen (doto (js-obj) ;;todo, moar Chosen options.
                       (aset "search_contains" search-contains)))

        ;;When user manipulates the chosen, update atom.
        (.on "change" #(swap! !a assoc :selected (selected-values $el))))

    ;;When code updates atom, update the chosen.
    (add-watch !a :_update-dom
               (fn [_ _ _ {:keys [selected]}]

                 ;;Update selection on DOM.
                 ;;Only select the first matching item, in case there are multiple options with the same value.
                 (-> $el (.find "option[selected='selected']")
                     (.removeAttr "selected"))
                 (doseq [val selected]
                   (-> $el (.find (str "option[value='" val "']"))
                       (.first)
                       (.attr "selected" "selected")))

                 ;;If the options marked as selected on the DOM are different than what the atom has stored, update the atom.
                 ;;This can happen if, e.g., a selected option is removed.
                 (let [dom-selected (selected-values $el)]
                   (when (not= selected dom-selected)
                     (swap! !a assoc :selected dom-selected)))

                 ;;Trigger Chosen-internals update so it'll match the DOM
                 (.trigger $el "liszt:updated")))

    (Chosen. $el !a multiple?)))
