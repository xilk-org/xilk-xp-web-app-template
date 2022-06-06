(ns {{root-ns}}.home.module-api
  "The API that app infrastructure uses to integrate this module.
  NOTE: Xilk Web App REPL tools automatically update this file."
  (:require
   [{{root-ns}}.home.home-screen :as home-screen]))

(def routes
  [["/" {:name :home/home
         :get home-screen/get-handler}]])

(def css
  (str
   home-screen/css))

(def strings
  (merge
   home-screen/strings))
