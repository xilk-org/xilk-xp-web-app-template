(ns ^:no-doc xilk.xp.web-app.repl.internal.basic-comp-template
  "INTERNAL: new component source code generator.
  The `create-data` API is unstable; custom symbols may be removed.")

(defn src
  [{:keys [comp-kw-name comp-ns comp-title] :as _comp-params}]
  (str
   "(ns " comp-ns "
  \"" comp-title " component.\"
  (:require
   [garden.units :refer [px]]
   [xilk.xp.web-app.ui :as x]))

(defn " comp-kw-name " [props]
  (x/html
   [:div {:class ::container}
    [:p (::" comp-kw-name " props)]
    [:p (::FIXME props)]]))

(def css
  (x/css
   [::container {:border [[(px 2) :solid]]}]))

(def strings
  {::" comp-kw-name " \"" comp-title"\"
   ::FIXME \"FIXME\"})\n"))

(defn create-data
  [{:keys [_comp-kw-name _comp-ns _comp-title] :as comp-params}]
  {:src (src comp-params)
   :css-sym-str "css"
   :strings-sym-str "strings"})
