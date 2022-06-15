(ns {{root-ns}}.http-error.e-404-not-found-screen
  (:require
   [{{root-ns}}.app.default-theme.theme :as theme]
   [{{root-ns}}.http-error.error-screen-template :as template]
   [xilk.xp.web-app.ui :as x]))

;;;; View

(defn html [props]
  (template/html (::sorry-couldnt-find props)
                 (::error-detail-and-corrective-steps props)
                 (::error-404-not-found props)))

(def css
  nil)

(def strings
  {::not-found-title "Not Found"
   ::sorry-couldnt-find "Sorry, we couldn't find what you were looking for."
   ::error-detail-and-corrective-steps
   (str "What was here might have been removed, or there might be a mistake in "
        "the link. Please check the link, or go back or use the navigation "
        "menu to continue. Sorry again!")
   ::error-404-not-found "Error 404, Not Found"})

;;;; Controller

(defn handler [req]
  (-> req
      (x/create-props {:screen.html.head.title/str-kw ::not-found-title})
      (theme/render html)
      (x/response 404 "text/html")))
