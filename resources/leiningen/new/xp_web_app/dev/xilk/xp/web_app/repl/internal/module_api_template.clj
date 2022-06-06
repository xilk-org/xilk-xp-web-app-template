(ns ^:no-doc xilk.xp.web-app.repl.internal.module-api-template
  "INTERNAL: new module API source code generator.
  The `create-data` API is unstable; custom symbols may be removed.")

(defn src
  [{:keys [module-api-ns] :as _module-api-params}]
  (str
   "(ns " module-api-ns "
  \"The API that app infrastructure uses to integrate this module.
  NOTE: Xilk Web App REPL tools automatically update this file.\")

(def routes
  nil)

(def css
  (str
   nil))

(def strings
  (merge
   nil))"))

(defn create-data
  [{:keys [_module-api-ns] :as module-api-params}]
  {:src (src module-api-params)
   :routes-sym-str "routes"
   :css-sym-str "css"
   :strings-sym-str "strings"})
