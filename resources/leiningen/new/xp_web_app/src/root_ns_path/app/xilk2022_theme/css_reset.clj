(ns {{root-ns}}.app.xilk2022-theme.css-reset
  (:refer-clojure :exclude [*])
  (:require
   [garden.selectors :as gs
                     :refer [a after attr= before defselector ol ul]]
   [garden.stylesheet :refer [at-media]]
   [garden.units :refer [ms percent vh]]))

(defselector * "*")

(def css-reset
  ;; Acknowledgment: https://github.com/hankchizljaw/modern-css-reset
  [[:*
    (* after)
    (* before) {:box-sizing :border-box}]

   [:blockquote
    :body
    :dd
    :dl
    :figure
    :h1
    :h2
    :h3
    :h4
    :ol
    :p
    :ul
    {:margin 0}]

   ;; For lists without markers, set role="list". More info:
   ;; https://www.scottohara.me/blog/2019/01/12/lists-and-safari.html
   [(ol (attr= :role :list))
    (ul (attr= :role :list)) {:list-style :none
                              :padding-left 0}]

   [:html:focus-within {:scroll-behavior :smooth}]

   [:body {:min-height (vh 100)
           :line-height 1.5
           :text-rendering :optimizeSpeed}]

   [(a (gs/not "[class]")) {:text-decoration-skip-ink :auto}]

   [:img
    :picture {:display :block
              :max-width (percent 100)}]

   [:button
    :input
    :select
    :textarea {:font :inherit}]

   (at-media {:prefers-reduced-motion :reduce}
    [:html:focus-within {:scroll-behavior :auto}]
    [:*
     (* after)
     (* before) {:animation-duration [[(ms 0.01) :!important]]
                 :animation-iteration-count [[1 :!important]]
                 :transition-duration [[(ms 0.01) :!important]]
                 :scroll-behavior [[:auto :!important]]}])])
