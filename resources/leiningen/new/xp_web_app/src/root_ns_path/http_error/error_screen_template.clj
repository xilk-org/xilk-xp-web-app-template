(ns {{root-ns}}.http-error.error-screen-template
  (:require
   [garden.units :refer [em]]
   [xilk.xp.web-app.ui :as x]))

(defn html [heading detail http-error]
  (x/html
   [:h2 {:class ::heading} heading]
   [:p {:class ::detail} detail]
   [:p {:class ::http-error} http-error]))

(def strings
  nil)

(def css
  (x/css
   [[::heading {:padding-bottom (em 1)}]
    [::detail {:padding-bottom (em 1)}]
    [::http-error {:font-style :italic}]]))
