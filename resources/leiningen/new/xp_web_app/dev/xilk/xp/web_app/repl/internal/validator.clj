(ns ^:no-doc xilk.xp.web-app.repl.internal.validator
  "INTERNAL: public API argument validator."
  (:require
   [clojure.spec.alpha :as s]))

;;;; Shared

(s/def ::kw keyword?)
(s/def ::parent-module-kw keyword?)
(s/def ::template-fn fn?)

(s/def :app.module/kw keyword?)
(s/def :def/type #{:comp :module :screen})
(s/def :module/kw keyword?)
(s/def :module/path string?)
(s/def :module.api/path string?)
(s/def :module/unit-defs sequential?)
(s/def :unit/kw keyword?)
(s/def :unit/libspec sequential?)
(s/def :unit/path string?)
(s/def :unit/src string?)
(s/def :unit/strings-sym symbol?)
(s/def :unit/css-sym symbol?)
(s/def ::wip? boolean?)

;;;; Comp

(s/def ::comp-def-params
  (s/keys :req-un [::kw]
          :opt-un [::parent-module-kw ::template-fn]))

(s/def ::comp-def
  (s/and
   (s/keys :req [:def/type :module/kw :module/path :module.api/path
                 :unit/kw :unit/libspec :unit/path :unit/src :unit/strings-sym
                 :unit/css-sym])
   (fn [d] (= :comp (:def/type d)))))

;;;; Screen

(defn http-method-kw? [kw]
  (-> kw
      #{:connect :delete :get :head :options :patch :post :put :trace}
      boolean))

(s/def ::route-method-item http-method-kw?)
(s/def ::route-method (s/or :single   ::route-method-item
                            :multiple (s/+ ::route-method-item)))
(s/def ::route-path string?)
(s/def ::theme-kw keyword?)

(s/def ::screen-def-arity-1-params
  (s/keys :req-un [::kw ::parent-module-kw ::route-path]
          :opt-un [::route-method ::theme-kw ::template-fn]))

(s/def ::screen-def-arity-2-params
  (s/keys :req-un [::kw ::route-path]
          :opt-un [::route-method ::theme-kw ::template-fn]))

(s/def :unit/route sequential?)

(s/def ::screen-def
  (s/and
   (s/keys :req [:def/type :module/kw :module/path :module.api/path
                 :unit/kw :unit/libspec :unit/path :unit/route :unit/src
                 :unit/strings-sym :unit/css-sym])
   (fn [d] (= :screen (:def/type d)))))

;;;; Module

(s/def ::children sequential?)

(s/def ::module-def-params (s/keys :req-un [::kw]
                                   :opt-un [::children]))

(s/def ::module-def
  (s/and
   (s/keys :req [:app.module/kw :def/type :module/kw
                 :module/path]
           :opt-un [:module/unit-defs ::wip?])
   (fn [d] (= :module (:def/type d)))))
