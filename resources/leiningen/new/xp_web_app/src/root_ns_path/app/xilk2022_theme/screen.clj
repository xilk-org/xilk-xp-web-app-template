(ns {{root-ns}}.app.xilk2022-theme.screen
  (:require
   [clojure.string :as str]
   [{{root-ns}}.app.xilk2022-theme.colors :refer [dk-bg lt-bg]]
   [{{root-ns}}.app.xilk2022-theme.css-reset :refer [css-reset]]
   [{{root-ns}}.app.xilk2022-theme.footer-comp :refer [footer] :as footer]
   [{{root-ns}}.app.xilk2022-theme.header-comp :refer [header] :as header]
   [garden.stylesheet :refer [at-media]]
   [garden.units :refer [em px]]
   [hiccup.page :as hp]
   [xilk.xp.web-app.ui :as x]))

;;;; View

;;; Constants

(def min-touch-target-dim (px 48))

;;; HTML

(defn head [{:keys [screen.html.head/added-els] :as props}]
  (x/html
   (into [:head
          [:meta {:charset :utf-8}]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1"}]
          [:title (:html.doc/title props)]
          [:link {:rel :stylesheet
                  :href (x/route-path :stylesheet/xilk2022)
                  :type "text/css"}]]
         added-els)))

(defn body [{::keys [main-content]
             :screen.main/keys [to-container-edges?]
             :as props}]
  (let [main-fit-kw (if to-container-edges? ::to-container-edges ::inset)]
    (x/html
     [:body
      [:div {:class ::layout}
       (header (assoc props
                      ::header/class-kws [::header ::to-container-edges]
                      ::header/nav-item__here-kws [::header__nav-item_here
                                                   :.a__touch-target]
                      ::header/nav-item__link-kws [::header__nav-item_link
                                                   :.a__touch-target]))

       [:main {:class [::main main-fit-kw]}
        main-content]

       (footer (assoc props
                      ::footer/class-kws [::footer ::to-container-edges]
                      ::footer/link-kws [::footer__link
                                         :.a__touch-target]))]])))

(defn html-doc
  [{:keys [app/lang] :as props}]
  (str
   (x/html
    (hp/doctype :html5)
    [:html {:lang lang}
     (head props)
     (body props)])))

;;; CSS

(def internal-css-to-reduce-screen-flash-on-load
  (x/css
   [;; Light Theme: text & background colors for body & layout elements
    (at-media {:prefers-color-scheme :light}
     [:body {:color (:body/text lt-bg)
             :background-color (:body/bg lt-bg)}]

     [::header {:color (:header/fg lt-bg)
                :background-color (:header/bg lt-bg)}]

     [::footer {:color (:footer/fg lt-bg)
                :background-color (:footer/bg lt-bg)}])

    ;; Dark Theme: text & background colors for body & layout elements

    (at-media {:prefers-color-scheme :dark}
     [:body {:color (:body/text dk-bg)
             :background-color (:body/bg dk-bg)}]

     [::header {:color (:header/fg dk-bg)
                :background-color (:header/bg dk-bg)}]

     [::footer {:color (:footer/fg dk-bg)
                :background-color (:footer/bg dk-bg)}])]))

(def css
  (x/css
   (into
    css-reset
    [;; Light Theme: default colors not already defined in internal stylesheet

     (at-media {:prefers-color-scheme :light}
      [:a {:color (:body.link/any-link lt-bg)}]
      [:a:hover {:color (:body.link/hover lt-bg)}]
      [:a:active {:color (:body.link/active lt-bg)}]

      [::header__nav-item_here {:color (:header.nav-item.here/fg lt-bg)}]

      [::header__nav-item_link {:color (:header.nav-item.link/any-link lt-bg)}]
      [::header__nav-item_link:hover {:color (:header.nav-item.link/hover lt-bg)}]
      [::header__nav-item_link:active {:color (:header.nav-item.link/active lt-bg)}]

      [::footer__link {:color (:footer.link/any-link lt-bg)}]
      [::footer__link:hover {:color (:footer.link/hover lt-bg)}]
      [::footer__link:active {:color (:footer.link/active lt-bg)}])

     ;; Dark Theme: default colors not already defined in internal stylesheet

     (at-media {:prefers-color-scheme :dark}
      [:a {:color (:body.link/any-link dk-bg)}]
      [:a:hover {:color (:body.link/hover dk-bg)}]
      [:a:active {:color (:body.link/active dk-bg)}]

      [::header__nav-item_here {:color (:header.nav-item.here/fg dk-bg)}]

      [::header__nav-item_link {:color (:header.nav-item.link/any-link dk-bg)}]
      [::header__nav-item_link:hover {:color (:header.nav-item.link/hover dk-bg)}]
      [::header__nav-item_link:active {:color (:header.nav-item.link/active dk-bg)}]

      [::footer__link {:color (:footer.link/any-link lt-bg)}]
      [::footer__link:hover {:color (:footer.link/hover lt-bg)}]
      [::footer__link:active {:color (:footer.link/active lt-bg)}])

     ;; Element Defaults

     [:body {:font-family "HelveticaNeue, Helvetica, Arial, sans-serif"}]

     [:h1 :h2 :h3 :h4 :h5 :h6
      {:font-family (str "HelveticaNeue-Light, \"Helvetica Neue Light\", "
                         "sans-serif-light, HelveticaNeue, Helvetica, Roboto, "
                         "Arial, sans-serif")
       :font-weight 300}]

     [:h1 {:padding-bottom (em 0.335)}]
     [:h2 {:padding [[(em 0.375) 0]]}]
     [:h3 {:padding [[(em 0.415) 0]]}]
     [:h4 :blockquote :dir :dl :fieldset :form :menu :p
      {:padding [[(em 0.56) 0]]}]
     [:h5 {:padding [[(em 0.75) 0]]}]
     [:h6 {:padding [[(em 0.835) 0]]}]

     ;; Pad like :h4 selector list above, but keep padding-left for list markers.
     [:ol
      :ul {:padding-top (em 0.56)
           :padding-bottom (em 0.56)}]

     ;; Utility Classes

     [:.a__touch-target {:display :flex
                         :flex-direction :column
                         :justify-content :center
                         :min-height min-touch-target-dim
                         :min-width min-touch-target-dim}]

     [:.el__text-center {:text-align :center}]

     ;; Screen Layout

     [::layout {:display :grid
                :grid-template-columns [["minmax(1rem, 1fr)"
                                         "minmax(0, 60rem)"
                                         "minmax(1rem, 1fr)"]]}]

     [::header {:min-height min-touch-target-dim}]

     [::main {:padding [[(em 0.5) 0]]}]

     [::to-container-edges {:grid-column "1 / 4"
                            :padding 0}]

     [::inset {:grid-column 2}]])))

;;; Strings

(def strings
  {::html-doc-title-generic "{{name}}"
   ::html-doc-title-template "{1} | {{name}}"})

;;;; Render Helpers

(defn title [lang title-fragment]
  (cond
    (string? title-fragment)
    (str/replace (::html-doc-title-template strings)
                 "{1}"
                 title-fragment)

    (keyword? title-fragment)
    (->> title-fragment
         (x/loc-str lang)
         (x/loc-str lang ::html-doc-title-template))

    :else
    (x/loc-str lang ::html-doc-title-generic)))

(defn add-title [{:app/keys [lang] :as props} title-fragment]
  (assoc props :html.doc/title (title lang title-fragment)))

(def internal-stylesheet-el
  [:style internal-css-to-reduce-screen-flash-on-load])

(defn prepend-html-head-added-els
  [props el]
  (update props :screen.html.head/added-els
          #(into [el] %)))

(defn add-main-content [props content-fn]
  (assoc props ::main-content (content-fn props)))
