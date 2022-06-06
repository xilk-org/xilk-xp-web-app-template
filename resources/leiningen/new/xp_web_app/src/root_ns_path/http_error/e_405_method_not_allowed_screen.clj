(ns {{root-ns}}.http-error.e-405-method-not-allowed-screen
  (:require
   [{{root-ns}}.app.default-theme.theme :as theme]
   [{{root-ns}}.http-error.error-screen-template :as template]))

;;;; View

(defn html [props]
  (template/html (::sorry-something-went-wrong props)
                 (::error-detail-and-corrective-steps props)
                 (::error-405-method-not-allowed props)))

(def css
  nil)

(def strings
  {::method-not-allowed-title "Method Not Allowed"
   ::sorry-something-went-wrong "Sorry, something went wrong."
   ::error-detail-and-corrective-steps
   (str "We likely made a mistake that led to this, so we'll take a look and "
        "get it fixed as soon as we can. Please go back or use the navigation "
        "menu to continue. Sorry again!")
   ::error-405-method-not-allowed "Error 405, Method Not Allowed"})

;;;; Controller

(defn handler [req]
  {:status 405
   :headers {"Content-Type" "text/html"}
   :body (theme/render {:content-fn html
                        :props {:screen.html.head.title/str-kw
                                ::method-not-allowed-title}
                        :req req})})
