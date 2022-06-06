(ns {{root-ns}}.app.strings
  "App string data and localizations for rendering in the app UI.

  NOTE: Base language strings for features/modules are defined in their
  respective namespaces to facilitate development. They are then aggregated in
  the `module-api` namespace for each feature/module. These data namespaces must
  be `require`d here, and their strings must be included in the
  `all-base-lang-strs` to be available for localization and rendering.

  `dict` refers to a complete set of strings for a particular language, ready
  for rendering.

  `strings` refers to incomplete sets of string data in the base language, to be
  aggregated and transformed into a base language dict. These may include
  additional data, such as notes to localizers, which are removed prior to
  being used in a dict."
  (:require
   [{{root-ns}}.app.comps.module-api :as app.comps]
   [{{root-ns}}.app.starter-theme.module-api :as starter-theme]
   [{{root-ns}}.app.xilk2022-theme.module-api :as xilk2022-theme]
   [{{root-ns}}.home.module-api :as home]
   [{{root-ns}}.http-error.module-api :as http-error]))

(def base-lang
  "The language in which all app strings are initially defined."
  :en)

(def all-base-lang-strs
  "Map defining all base language string data in the app.

  NOTE: Default language string data for all features/modules must be included
  here to be available for localization and rendering."
  (merge starter-theme/strings
         xilk2022-theme/strings
         ;; Module strings
         app.comps/strings
         http-error/strings
         home/strings))

(def localizable-base-lang-strs
  "Map defining all localizable string data in the app, in the base language.

  This is the primary localization reference. Transform this map into whatever
  format is used for localization."
  (apply dissoc all-base-lang-strs
         (filter #(true? (:do-not-localize? (% all-base-lang-strs)))
                 (keys all-base-lang-strs))))

(def app-dicts
  "Dictionaries used for rendering all strings in all languages in the app UI.

  NOTE: Localizations must be defined here to be available for rendering."
  {:tongue/fallback base-lang

   base-lang (update-vals all-base-lang-strs
                          #(if (map? %) (:str %) %))

   :ja {}
   ,})
