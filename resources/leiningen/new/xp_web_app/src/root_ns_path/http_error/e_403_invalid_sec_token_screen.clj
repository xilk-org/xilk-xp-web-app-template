(ns {{root-ns}}.http-error.e-403-invalid-sec-token-screen
  (:require
   [{{root-ns}}.app.default-theme.theme :as theme]
   [{{root-ns}}.http-error.error-screen-template :as template]))

;;;; View

(defn html [props]
  (template/html (::sorry-cant props)
                 (::error-detail-and-corrective-steps props)
                 (::error-403-invalid-security-token props)))

(def css
  nil)

(def strings
  {::forbidden-title "Forbidden"
   ::sorry-cant "Sorry, we can't accommodate your request."
   ::error-detail-and-corrective-steps
   (str "Something got out of sync. Please go back, refresh your browser, "
        "and try again. Or, you can use the navigation menu to continue "
        "on to something else. Sorry again!")
   ::error-403-invalid-security-token
   "Error 403, Forbidden: Invalid Security Token"})

;;;; Controller

(defn handler [req]
  {:status 403
   :headers {"Content-Type" "text/html"}
   :body (theme/render {:content-fn html
                        :props {:screen.html.head.title/str-kw
                                ::forbidden-title}
                        :req req})})
