(ns {{root-ns}}.app.xilk2022-theme.header-comp
  (:require
   [{{root-ns}}.app.xilk2022-theme.main-nav-comp :refer [main-nav] :as main-nav]
   [garden.units :refer [em]]
   [xilk.xp.web-app.ui :as x]))

(defn header
  [{::keys [class-kws nav-item__here-kws nav-item__link-kws] :as props}]
  (x/html
   [:header {:class [::container class-kws]}
    [:div (::app-name props)]
    (main-nav (assoc props
                     ::main-nav/nav-item__here-kws nav-item__here-kws
                     ::main-nav/nav-item__link-kws nav-item__link-kws))]))

(def css
  (x/css
   [[::container {:display :flex
                  :justify-content :space-between
                  :align-items :center
                  :padding [[0 (em 0.5)]]}]]))

(def strings
  {::app-name "{{name}}"})
