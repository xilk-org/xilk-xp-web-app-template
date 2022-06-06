(ns {{root-ns}}.app
  "App initialization, configuration, and entry point."
  (:gen-class)
  (:require
   [{{root-ns}}.app.routes :as routes]
   [{{root-ns}}.app.strings :as strs]
   [{{root-ns}}.http-error.e-403-invalid-sec-token-screen :as e-403-ist-screen]
   [donut.system :as ds]
   [ring.adapter.jetty :as rj]
   [ring.middleware.defaults :refer [wrap-defaults] :as rmd]
   [xilk.xp.web-app.ui :as x]))

;;;; Initialization

;;; Xilk Web App UI Library
(x/init! routes/router strs/all-base-lang-strs strs/app-dicts)

;;;; Configuration

;;; Top-level Handler (Ring)

(def handler
  (-> routes/handler
      (wrap-defaults (-> rmd/site-defaults
                         (assoc-in [:security :anti-forgery]
                                   {:error-handler e-403-ist-screen/handler})
                         (assoc-in [:session :cookie-attrs :max-age]
                                   (* 60 60 24 365))
                         (assoc-in [:session :cookie-name] "session")))))

(comment
  (handler {:request-method :get, :uri "/"})
  (handler {:request-method :get, :uri "/MISMATCH"}))

;;; System (donut.system)

(def system
  {::ds/defs
   {:http
    {:handler handler

     :port (or (some-> (System/getenv) (get "PORT") parse-long)
               80)

     :server {:start (fn http-server-start
                       [{:keys [app-handler options] :as _conf} _ _]
                       (rj/run-jetty app-handler options))
              :stop  (fn http-server-stop
                       [_ instance _]
                       (.stop instance))
              :conf  {:app-handler (ds/ref :handler)
                      :options     {:port (ds/ref :port)
                                    :join? false}}}}}})

(defmethod ds/named-system :prod
  [_]
  system)

;;;; Entry point

(defn -main []
  (ds/start :prod))
