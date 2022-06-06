(ns {{root-ns}}.app.xilk2022-theme.main-nav-comp
  "Main nav component."
  (:require
   [garden.units :refer [em]]
   [xilk.xp.web-app.ui :as x]))

(defn li [item-str-kw href {::keys [nav-item__here-kws nav-item__link-kws]
                            :keys [req/uri] :as props}]
  (let [item-str (item-str-kw props)]
    (x/html
     [:li {:class ::nav-item}
      (if (= href uri)
        [:div {:class ["el__text-center" nav-item__here-kws]}
         item-str]
        [:a {:class [::nav-item__link nav-item__link-kws]
             :href href}
         [:div {:class "el__text-center"}
          item-str]])])))

(defn main-nav
  [{::keys [_nav-item__here-kws _nav-item__active-kws] :as props}]
  (x/html
   [:nav
    [:ul {:class ::nav-list
          :role :list}
     (li ::home "/" props)]]))

(def css
  (x/css
   [[::nav-list {:display :flex
                 :justify-content :space-between
                 :align-items :center
                 :padding 0}]

    [::nav-item {:padding-left (em 1.5)}]

    [::nav-item__link {:text-decoration :none}]]))

(def strings
  {::home "Home"})
