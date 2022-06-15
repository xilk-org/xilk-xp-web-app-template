(ns ^:no-doc xilk.xp.web-app.repl.internal.basic-screen-template
  "INTERNAL: new screen source code generator.
  The `create-data` API is unstable; custom symbols may be removed.")

(defn src
  [{:keys [handler-syms screen-kw-name screen-ns screen-title theme-ns]
    :as _screen-params}]
  ;; TODO: pass this in from scaffolder
  (let [screen-title-kw-name (str screen-kw-name "-title")]
    (str
     "(ns " screen-ns "
  \"" screen-title " screen view and controller.\"
  (:require
   [" theme-ns " :as theme]
   [garden.units :refer [px]]
   [xilk.xp.web-app.ui :as x]))

;;;; View

(defn html [props]
  (x/html
   [:h1 {:class ::heading}
        (::" screen-kw-name " props)]
   [:p (::FIXME props)]))

(def css
  (x/css
   [::heading {:border [[(px 2) :solid]]}]))

(def strings
  {::" screen-title-kw-name " \"" screen-title "\"
   ::" screen-kw-name " \"" screen-title "\"
   ::FIXME \"FIXME\"})

;;;; Controller
"    (reduce (fn [s hs]
               (str s "
(defn " (str hs) " [req]
  (-> req
      (x/create-props {:screen.html.head/title ::" screen-title-kw-name "})
      (theme/screen-resp html)))\n"))
             ""
             handler-syms))))

(defn create-data
  [{:keys [_handler-syms _screen-kw-name _screen-ns _screen-title _theme-ns]
    :as screen-params}]
  {:src (src screen-params)
   :css-sym-str "css"
   :strings-sym-str "strings"})
