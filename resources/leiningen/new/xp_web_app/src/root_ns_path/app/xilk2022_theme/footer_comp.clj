(ns {{root-ns}}.app.xilk2022-theme.footer-comp
  "Footer component."
  (:require
   [garden.units :refer [em]]
   [xilk.xp.web-app.ui :as x]))

(defn footer
  [{::keys [class-kws link-kws] :as props}]
  (x/html
   [:footer {:class [::layout class-kws]}

    [:div {:class ::content}

     [:div {:class ::info}

      [:p {:class ::copyright}
       "© 2022 " (::copyright-holder props)]

      [:address {:class ::contact}
       [:ul {:role :list}

        [:li {:class ::address__item}
         [:a {:class link-kws
              :href (str "mailto:" (::contact-email props))}
          (::contact-email props)]]

        [:li {:class ::address__item}
         [:a {:class link-kws
              :href (::twitter-url props)}
          (::twitter-link props)]]]]]

     [:nav {:class ::nav}
      [:div {:class ::nav-layout}
       [:ul {:role :list}

        [:li
         "FIXME"]]]]]]))

(def css
  (x/css
   [[::layout {:display :grid
               :grid-template-columns [["minmax(0.5rem, 1fr)"
                                        "minmax(0, 70rem)"
                                        "minmax(0.5rem, 1fr)"]]}]
    [::content {:grid-column 2
                :display :grid
                :grid-template-columns "1fr 1fr"
                :align-items :center
                :padding [[(em 0.5) 0]]}]

    [::contact {:font-style :normal}]

    [::nav {:justify-self :right}]]))

(def strings
  {::copyright-holder {:str "FIXME"
                       :do-not-localize? true}
   ::contact-email {:str "FIXME@example.com"
                    :do-not-localize? true}
   ::twitter-url {:str "https://twitter.com/FIXME"
                  :do-not-localize? true}
   ::twitter-link {:str "Twitter-FIXME"
                   :do-not-localize? true}})
