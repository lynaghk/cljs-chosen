(ns destined.core
  (:use-macros [clojure.core.match.js :only [match]])
  (:use [jayq.core :only [$]]
        [clojure.string :only [join]]))

(defprotocol ISelectable
  (selected
    [this]
    [this values]))
(defprotocol IOptions
  (options
    [this]
    [this options]))

(defn- ->coll [c]
  (if-not (coll? c) [c] c))

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
  (match [o]
         [(m :when map?)]
         (let [{:keys [value text selected disabled group]} m]
           (join " " ["<option"
                      (if value (str "value='" value "'"))
                      (if disabled "disabled='disabled'")
                      (if selected "selected='selected'")
                      ">" text "</option>"]))
         [(s :when string?)] (str "<option>" s "</option>")))


(defn- reset-dom-options! [$el options]
  ;;Remove old options
  (-> $el (.children) (.remove))
  ;;Insert new options
  (doseq [[group opts] (group-by :group options)]
    (let [opts-html (join "\n" (map opt->html opts))]
      (if (nil? group)
        ;;just append options
        (-> $el (.append opts-html))
        ;;otherwise, options within an <optgroup>.
        (-> $el (.append (str "<optgroup label='" group "'>" opts-html "</optgroup>")))))))


(defn destined
  "Turn <select> element (or selector string) el into a Destined selector."
  [el & {:keys [search-contains]
         :or {search-contains false}}]
  (let [$el ($ el)
        multiple? (= "multiple" (.attr $el "multiple"))
        !a (atom {:options (el-options $el)
                  :selected (selected-values $el)})]

    (-> $el (.destined (doto (js-obj) ;;todo, moar Chosen options.
                         (aset "search_contains" search-contains)))

        ;;When user manipulates the destined, update atom.
        (.on "change" #(swap! !a assoc :selected (selected-values $el))))

    ;;When code updates atom, update the destined.
    (add-watch !a :_update-dom
               (fn [_ _ _ {:keys [selected]}]

                 ;;Update selection on DOM.
                 ;;Only select the first matching item, in case there are multiple options with the same value.
                 (doseq [val selected]
                   (-> $el (.first (str "option[value='" val "']"))
                       (.attr "selected" "selected")))

                 ;;If the options marked as selected on the DOM are different than what the atom has stored, update the atom.
                 ;;This can happen if, e.g., a selected option is removed.
                 (let [dom-selected (selected-values $el)]
                   (when (not= selected dom-selected)
                     (swap! !a assoc :selected dom-selected)))

                 ;;Trigger Chosen-internals update so it'll match the DOM
                 (.trigger $el "liszt:updated")))


    (reify
      ISelectable
      (selected [_]
        (let [sel (:selected @!a)]
          (if multiple? sel (first sel))))
      (selected [_ values]
        (swap! !a assoc :selected (->coll values)))

      IOptions
      (options [_]
        (:options @!a))
      (options [_ options]
        (reset-dom-options! $el options)
        (swap! !a assoc :options (el-options $el)))

      ;;Proxy to internal atom.
      ;;Is implementing IWatchable a good idea?
      IWatchable
      (-notify-watches [_ _ _])
      (-add-watch [_ key f]
        ;;Only call watchers when selection changes
        (add-watch !a key (fn [_ _ {old-selected :selected} {selected :selected}]
                            (when (not= old-selected selected)
                              (f selected)))))
      (-remove-watch [_ key]
        (remove-watch !a key)))))
