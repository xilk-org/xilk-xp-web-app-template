(ns {{root-ns}}.app.routes
  "App route data, a [[router]], and a routing [[handler]].

  Each Xilk module defines routes for its screens in its `module-api` namespace.

  This namespace must `require` all module `module-api` namespaces, and
  aggregate their route data for inclusion in the router. The boilerplate code
  required for this is automatically generated when using
  `xilk.xp.web-app.repl` to build new modules at the REPL."
  (:require
   [{{root-ns}}.app.stylesheets :as stylesheets]
   [{{root-ns}}.home.module-api :as home]
   [{{root-ns}}.http-error.e-404-not-found-screen :as e-404-screen]
   [{{root-ns}}.http-error.e-405-method-not-allowed-screen :as e-405-screen]
   [{{root-ns}}.http-error.e-406-not-acceptable-screen :as e-406-screen]
   [reitit.ring :as ring]))

(def app-route-data
  (concat stylesheets/routes
          ;; Module routes
          home/routes))

(comment
  (require '[clojure.pprint :refer [pprint]])
  (pprint app-route-data))

(def router
  (ring/router app-route-data))

(def static-resource-handler
  (ring/create-resource-handler {:path "/s/"}))

(def http-error-handler
  (ring/create-default-handler {:not-found e-404-screen/handler
                                :method-not-allowed e-405-screen/handler
                                :not-acceptable e-406-screen/handler}))

(def handler
  (ring/ring-handler router (ring/routes static-resource-handler
                                         http-error-handler)))
