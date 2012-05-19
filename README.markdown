Chosen
======
[Chosen](http://harvesthq.github.com/chosen/) is a great lil' JavaScript library that makes <select> boxes friendly.
This is a ClojureScript interface.

```clojure
(ns my-site
  (:use [chosen :only [ichooseu! selected options]]))

;;convert an existing <select> into a chosen
(def c (ichooseu! (.querySelect js/document "#my-select")))

(options c ["A" "B" {:value "17" :text "C"}])

(selected c "B")

(selected c) ;;=> "B"

(add-watch c (fn [new-selection]
               (.log js/console new-selection)))
```
To use, just add

```clojure
[com.keminglabs/chosen "0.1.5"]
```

to your `project.clj` and

```clojure
(ns my-cljs-ns
  (:use [chosen.core :only [ichooseu! selected options]]))
```

to the namespace you want to use it in.
Your page will still need to have jQuery available, as well as the Chosen JavaScript plugin and CSS (`chosen.jquery.js` and `chosen.css`, sold separately).
Add to your compilation options:

```clojure
  {
    :optimizations :advanced
    :externs ["chosen-externs.js" "externs/jquery.js"]
    ...
  }
```

to use Closure's advanced mode compilation.


API
---
`(ichooseu! node-or-selector)`: Turns an existing `<select>` into a Chosen selector.
Keyword opts:

+ `:search-contains` search matches in middle of string

`ichooseu!` returns an object implementing two getter/setters:

`(options chosen)` get available options

`(options chosen opts)` sets available options.
Options can be strings or maps of the form 

```clojure
{:text "Apples" :value "A" :group "fruit"}
```

Options will be grouped by `group` and the appropriate `<optgroup>` tags created.

`(select chosen)` get currently selected value(s). This will be a set the chosen wraps a multiselect (`<select multiple="multiple">`).

`(select chosen vals)` set selection.
`vals` can be a single value, or, for multiselects, a seq of values.

Chosen objects implement `IWatchable`, so you can add watches to them just like you can with atoms.
Watchers are called with a single argument, the new selection.

Testing
-------
For ClojureScript-specific integration testing, you can run our highly advanced, PhantomJS-powered "list-of-assertions" testing framework:

    lein cljsbuild test

or, if you're too cool to go headless:

    lein cljsbuild once

then open up `public/index.html` in your browser.



TODO
----
Allow Clojure data structures (including closures) to be values.
