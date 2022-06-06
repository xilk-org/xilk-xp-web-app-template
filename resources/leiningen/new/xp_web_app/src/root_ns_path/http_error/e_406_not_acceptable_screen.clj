(ns {{root-ns}}.http-error.e-406-not-acceptable-screen
  (:require
   [{{root-ns}}.app.default-theme.theme :as theme]
   [{{root-ns}}.http-error.error-screen-template :as template]))

;;;; View

(defn html [props]
  (template/html (::sorry-couldnt-find props)
                 (::error-detail-and-corrective-steps props)
                 (::error-406-not-acceptable props)))

(def css
  nil)

(def strings
  {::not-acceptable-title "Not Acceptable"
   ::sorry-something-went-wrong "Sorry, something went wrong."
   ::error-detail-and-corrective-steps
   (str "We likely made a mistake that led to this, so we'll take a look and "
        "get it fixed as soon as we can. Please go back or use the navigation "
        "menu to continue. Sorry again!")
   ::error-406-not-acceptable "Error 406, Not Acceptable"})

;;;; Controller

(defn handler [req]
  {:status 406
   :headers {"Content-Type" "text/html"}
   :body (theme/render {:content-fn html
                        :props {:screen.html.head.title/str-kw
                                ::not-acceptable-title}
                        :req req})})
