(ns {{root-ns}}.app.starter-theme.module-api
  "The API that app infrastructure uses to integrate this theme.

  Unlike modules with screens, theme modules define no routes. Instead, a
  stylesheet route for this theme must be defined in `app.stylesheets`. This
  makes it easier to keep stylesheet routes consistent and without conflict,
  especially when using third party themes."
  (:require
   [{{root-ns}}.app.starter-theme.screen :as screen]))

(def css
  (str
   screen/css))

(def strings
  (merge
   screen/strings))
