(ns ^:no-doc xilk.xp.web-app.ui.internal.css
  "INTERNAL: CSS class selector keyword transformer."
  (:require
   [clojure.string :as str]
   [garden.util :as gu]))

(defn trim-starting-colon-and-period [kw-str]
  (if (= (second kw-str) \.)
    (subs kw-str 2)
    (subs kw-str 1)))

(defn css-class-str
  "Transforms a qualified or Garden class selector keyword into a class string.
  Used internally, prior to compiling/generating HTML, to replace convenient
  *class selector keywords* in HTML `class` attributes with matching *CSS class
  strings*.

  **Arguments**

  * `class-sel-kw`: Either a qualified or a Garden (unqualified, prefixed by a
    \".\") class selector keyword. Throws an error if unqualified and not
    prefixed by a period ( \".\").

  **Examples**

  ```clj
  ;; Transform a qualified keyword.
  (css-class-str ::alert-comp/modal__active)

  ;; Transform a Garden class selector keyword.
  (css-class-str :.full-screen)
  ```"
  [class-sel-kw]
  {:pre [(or (qualified-keyword? class-sel-kw)
             (= \. (-> class-sel-kw str second)))]}
  (-> class-sel-kw
      str
      trim-starting-colon-and-period
      (str/replace "/" "__")
      (str/replace "." "_")))

(defn garden-sel-kw
  "Transforms a qualified keyword into a Garden class selector keyword.
  Used internally, prior to compiling stylesheets with Garden, to replace
  convenient auto-resolved keywords in style definitions with matching Garden
  keywords.

  **Arguments**

  * `qualified-kw`: A qualified keyword. Throws an error if unqualified.

  **Examples**

  ```clj
  ;; Transform a qualified keyword.
  (garden-sel-kw ::alert-comp/modal__active)
  ```"
  [qualified-kw]
  {:pre [(qualified-keyword? qualified-kw)]}
  (->> qualified-kw
       css-class-str
       (str ".")
       keyword))

(defn seq-of-rules
  "Returns a sequence of rules, with redundant parent sequence removed, if any."
  [rules]
  (if (and (= 1 (count rules))
           (or (-> rules first first sequential?)
               (-> rules first first gu/at-rule?)))
    (first rules)
    rules))

(defn transform-qualified-sel-kws-for-garden-css-compilation
  "Returns style rules with qualified selector kws transformed for compilation."
  [rules]
  (mapv
   #(cond
      ;; Vector Rule
      (and (sequential? %) (seq %))
      (let [kw (first %)]
        (if (qualified-keyword? kw)
          (assoc % 0 (garden-sel-kw kw))
          %))

      ;; Garden CSSAtRule
      (and (map? %) (some? (-> % :value :rules)))
      (update-in %
                 [:value :rules]
                 (comp
                  seq
                  ;; FIXME: optimize to prevent stack overflow
                  transform-qualified-sel-kws-for-garden-css-compilation))

      :else %)
   rules))

(defn xilk-html-attr-map->hiccup [attr-m]
  (if-let [class-val (:class attr-m)]
    (cond
      (sequential? class-val)
      (assoc attr-m :class (->> class-val
                                flatten
                                (map (fn [cls]
                                       (if (keyword? cls)
                                         (css-class-str cls)
                                         cls)))))

      (keyword? class-val)
      (assoc attr-m :class (css-class-str class-val))

      :else
      attr-m)
    attr-m))

(defn transform-qualified-sel-kws-for-hiccup-html-compilation
  "Returns hiccup with qualified selector kws transformed for compilation."
  [hiccup]
  (if (sequential? hiccup)
    (let [map-fn (if (vector? hiccup) mapv map)]
      (map-fn (fn [form]
                (cond
                  (sequential? form)
                  ;; FIXME: optimize to prevent stack overflow
                  (transform-qualified-sel-kws-for-hiccup-html-compilation form)

                  (map? form)
                  (xilk-html-attr-map->hiccup form)

                  :else
                  form))
              hiccup))
    hiccup))
