(ns {{root-ns}}.app.comps.module-api
  "The API that app infrastructure uses to integrate this module.
  NOTE: Xilk Web App REPL tools automatically update this file.

  Unlike modules with screens, the shared components module defines no routes."
  (:require
   [{{root-ns}}.app.comps.file-info-comp :as file-info-comp]))

(def css
  (str
   file-info-comp/css))

(def strings
  (merge
   file-info-comp/strings))
