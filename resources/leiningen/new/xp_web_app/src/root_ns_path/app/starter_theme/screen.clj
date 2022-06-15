(ns {{root-ns}}.app.starter-theme.screen
  (:require
   [clojure.string :as str]
   [hiccup.page :as hp]
   [xilk.xp.web-app.ui :as x]))

;;;; View

;;; HTML

(defn head [{:keys [screen.html.head/added-els] :as props}]
  (x/html
   (into [:head
          [:meta {:charset :utf-8}]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1"}]
          [:title (:html.doc/title props)]
          [:link {:rel :stylesheet
                  :href (x/route-path :stylesheet/starter)
                  :type "text/css"}]]
         added-els)))

(defn body [{::keys [main-content] :as _props}]
  (x/html
   [:body
    main-content]))

(defn html-doc
  [{:keys [app/lang] :as props}]
  (str
   (x/html
    (hp/doctype :html5)
    [:html {:lang lang}
     (head props)
     (body props)])))

;;; CSS

(def css
  (x/css
   [[:body {:background-color :white}]]))

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

(defn prepend-html-head-added-els
  [props el]
  (update props :screen.html.head/added-els
          #(into [el] %)))

(defn add-main-content [props content-fn]
  (assoc props ::main-content (content-fn props)))
