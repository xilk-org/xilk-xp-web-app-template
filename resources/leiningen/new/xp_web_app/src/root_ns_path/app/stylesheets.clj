(ns {{root-ns}}.app.stylesheets
  "Stylesheet data, handlers, and routes.

  Each Xilk screen and component defines locally-scoped CSS in its own
  namespace. Each Xilk module aggregates the CSS data from its screens and
  components in its `module-api` namespace.

  This namespace must `require` all module `module-api` namespaces, and
  aggregate their CSS data for inclusion in stylesheets. The boilerplate code
  required for this is automatically generated when using
  `xilk.xp.web-app.repl` to build new modules at the REPL.

  Stylesheets are defined for each theme, with accompanying handlers and routes
  for linking by app screens."
  (:require
   [{{root-ns}}.app.comps.module-api :as app.comps]
   [{{root-ns}}.app.starter-theme.module-api :as starter-theme]
   [{{root-ns}}.app.xilk2022-theme.module-api :as xilk2022-theme]
   [{{root-ns}}.home.module-api :as home]
   [{{root-ns}}.http-error.module-api :as http-error]
   [xilk.xp.web-app.ui :as x]))

;;;; Module CSS Data Aggregation

(def module-css
  (str app.comps/css
       http-error/css
       home/css))

;;;; Xilk2022 Theme Stylesheet

(def xilk2022-theme-ss
  (str xilk2022-theme/css
       module-css))

(defn xilk2022-theme-ss-dev-handler
  [_req]
  (-> xilk2022-theme-ss
      x/response-ok-css))

;;;; Starter Theme Stylesheet

(def starter-theme-ss
  (str starter-theme/css
       module-css))

(defn starter-theme-ss-dev-handler
  [_req]
  (-> starter-theme-ss
      x/response-ok-css))

;;;; Routes

(def routes
  [["/xilk2022.css" {:name :stylesheet/xilk2022
                     :get xilk2022-theme-ss-dev-handler}]
   ["/starter.css" {:name :stylesheet/starter
                    :get starter-theme-ss-dev-handler}]])
