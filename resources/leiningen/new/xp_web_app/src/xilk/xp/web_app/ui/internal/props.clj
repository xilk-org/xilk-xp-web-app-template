(ns ^:no-doc xilk.xp.web-app.ui.internal.props
  "INTERNAL: UI props transformer and editor."
  (:require
   [clojure.string :as str]
   [tongue.core :as tongue]
   [xilk.xp.web-app.ui.internal.state :as state]))

(defn tr-lang
  "Returns the language in `dicts` that best matches `lang`."
  ([lang] (tr-lang lang state/app-dicts))
  ([lang dicts]
   (if (some? lang)
     (or
      ;; NOTE: calls private tongue function to reuse its cache. This may
      ;; fail upon upgrading tongue versions if the implementation changes.
      (loop [tags (#'tongue/tags lang)]
        (when-some [tag (first tags)]
          (if (contains? dicts tag)
            tag
            (recur (next tags)))))
      (:tongue/fallback dicts))
     (:tongue/fallback dicts))))

(defn assoc-str-props
  "Returns props with localized and do-not-localized strings assoc'd."
  [{:req/keys [lang] :as props}]
  (reduce (fn [props [k v]]
            (cond
              (map? v)
              (if (true? (:do-not-localize v))
                (assoc props k (:str v))
                (assoc props k (state/loc-str-fn lang k)))

              (or (fn? v)
                  (and (string? v)
                       (.contains v "{")))
              props

              :else
              (assoc props k (state/loc-str-fn lang k))))
   props
   state/all-base-lang-strs))

(defn first-accept-language
  "Returns the first language tag in the Accept-Language header of `req`."
  [req]
  (when-let [accept-language (get-in req [:headers "accept-language"])]
    (if-let [idx (str/index-of accept-language ",")]
      (subs accept-language 0 idx)
      accept-language)))

(defn props-from-req
  "Returns a map containing props extracted from a Ring request.

  **Arguments**

  * `req`: A map containing a Ring request.

  **Examples**

  ```clj
  ;; Extract and merge props from request in a render function.
  (defn render
    [req content-fn props]
    (-> props
        (merge (x/props-from-req req))
        (add-main-content content-fn)
        html-doc))
  ```"
  [{:keys [uri]
    {:strs [_accept-language]} :headers
    :as req}]
  (let [req-lang (first-accept-language req)
        app-lang (tr-lang req-lang)]
    {:app/lang app-lang
     :req/lang req-lang
     :req/uri uri}))
