(ns {{root-ns}}.http-error.module-api
  "The API that app infrastructure uses to integrate this module.
  NOTE: Xilk Web App REPL tools automatically update this file."
  (:require
   [{{root-ns}}.http-error.e-400-bad-request-screen :as e-400-screen]
   [{{root-ns}}.http-error.e-403-invalid-sec-token-screen :as e-403-ist-screen]
   [{{root-ns}}.http-error.e-404-not-found-screen :as e-404-screen]
   [{{root-ns}}.http-error.e-405-method-not-allowed-screen :as e-405-screen]
   [{{root-ns}}.http-error.e-406-not-acceptable-screen :as e-406-screen]
   [{{root-ns}}.http-error.error-screen-template :as e-screen-template]))

(def routes
  nil)

(def css
  (str
   e-screen-template/css
   e-400-screen/css
   e-403-ist-screen/css
   e-404-screen/css
   e-405-screen/css
   e-406-screen/css))

(def strings
  (merge
   e-screen-template/strings
   e-400-screen/strings
   e-403-ist-screen/strings
   e-404-screen/strings
   e-405-screen/strings
   e-406-screen/strings))
