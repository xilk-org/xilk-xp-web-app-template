(ns {{root-ns}}.app.xilk2022-theme.module-api
  "The API that app infrastructure uses to integrate this theme.

  Unlike modules with screens, theme modules define no routes. Instead, a
  stylesheet route for this theme must be defined in `app.stylesheets`. This
  makes it easier to keep stylesheet routes consistent and without conflict,
  especially when using third party themes."
  (:require
   [{{root-ns}}.app.xilk2022-theme.footer-comp :as footer-comp]
   [{{root-ns}}.app.xilk2022-theme.header-comp :as header-comp]
   [{{root-ns}}.app.xilk2022-theme.main-nav-comp :as main-nav-comp]
   [{{root-ns}}.app.xilk2022-theme.screen :as screen]))

(def css
  (str
   screen/css
   header-comp/css
   footer-comp/css
   main-nav-comp/css))

(def strings
  (merge
   screen/strings
   header-comp/strings
   footer-comp/strings
   main-nav-comp/strings))
