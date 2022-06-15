(ns xilk.xp.web-app.repl-test
  (:require
   [clojure.test :refer [deftest is]]
   [xilk.xp.web-app.repl :as sut]
   [xilk.xp.web-app.repl.internal.basic-comp-template :as basic-comp]
   [xilk.xp.web-app.repl.internal.basic-screen-template
    :as basic-screen]))

;;;; comp-def

;;; comp-def Arity 1 - Happy Path

(deftest comp-def__with-required-params__returns-shared-comp-def
  (is (= {:def/type :comp
          :module/kw :app.comps
          :module/path "src/{{root-ns-path}}/app/comps"
          :module.api/path "src/{{root-ns-path}}/app/comps/module_api.clj"
          :unit/kw :cmp
          :unit/libspec ['{{root-ns}}.app.comps.cmp-comp :as 'cmp-comp]
          :unit/path "src/{{root-ns-path}}/app/comps/cmp_comp.clj"
          :unit/src
          "(ns {{root-ns}}.app.comps.cmp-comp\n  \"Cmp component.\"\n  (:require\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n(defn cmp [props]\n  (x/html\n   [:div {:class ::container}\n    [:p (::cmp props)]\n    [:p (::FIXME props)]]))\n\n(def css\n  (x/css\n   [::container {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::cmp \"Cmp\"\n   ::FIXME \"FIXME\"})\n"
          :unit/strings-sym 'cmp-comp/strings
          :unit/css-sym 'cmp-comp/css
          :unit/template-fn basic-comp/create-data}
         (sut/comp-def {:kw :cmp}))))

(deftest comp-def__with-parent-module-kw__returns-comp-def
  (is (= {:def/type :comp
          :module/kw :mod-1
          :module/path "src/{{root-ns-path}}/mod_1"
          :module.api/path "src/{{root-ns-path}}/mod_1/module_api.clj"
          :unit/kw :cmp
          :unit/libspec ['{{root-ns}}.mod-1.cmp-comp :as 'cmp-comp]
          :unit/path "src/{{root-ns-path}}/mod_1/cmp_comp.clj"
          :unit/src
          "(ns {{root-ns}}.mod-1.cmp-comp\n  \"Cmp component.\"\n  (:require\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n(defn cmp [props]\n  (x/html\n   [:div {:class ::container}\n    [:p (::cmp props)]\n    [:p (::FIXME props)]]))\n\n(def css\n  (x/css\n   [::container {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::cmp \"Cmp\"\n   ::FIXME \"FIXME\"})\n"
          :unit/strings-sym 'cmp-comp/strings
          :unit/css-sym 'cmp-comp/css
          :unit/template-fn basic-comp/create-data}
         (sut/comp-def {:kw               :cmp
                        :parent-module-kw :mod-1}))))

(deftest comp-def__with-template-fn__returns-comp-def
  (let [custom-fn (constantly {:src "CUSTOM SOURCE CODE"
                               :strings-sym-str "custom-strs-sym"
                               :css-sym-str "custom-stys-sym"})]
    (is (= {:def/type :comp
            :module/kw :app.comps
            :module/path "src/{{root-ns-path}}/app/comps"
            :module.api/path "src/{{root-ns-path}}/app/comps/module_api.clj"
            :unit/kw :cmp
            :unit/libspec ['{{root-ns}}.app.comps.cmp-comp :as 'cmp-comp]
            :unit/path "src/{{root-ns-path}}/app/comps/cmp_comp.clj"
            :unit/src "CUSTOM SOURCE CODE"
            :unit/strings-sym 'cmp-comp/custom-strs-sym
            :unit/css-sym 'cmp-comp/custom-stys-sym
            :unit/template-fn custom-fn}
           (sut/comp-def {:kw          :cmp
                          :template-fn custom-fn})))))

;;; comp-def Arity 1 - Validation - Required Params

(deftest comp-def__with-nil-params__throws-error
  (is (thrown? AssertionError (sut/comp-def nil))))

(deftest comp-def__with-nil-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:kw nil}))))

(deftest comp-def__with-invalid-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:kw "INVALID"}))))

;;; comp-def Arity 1 - Validation - Optional Params

(deftest comp-def__with-invalid-parent-module-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:kw               :scr
                                             :parent-module-kw "INVALID"}))))

(deftest comp-def__with-invalid-template-fn__throws-error
  (is (thrown? AssertionError (sut/comp-def {:kw          :scr
                                             :template-fn :INVALID}))))

;;; comp-def Arity 2 - Happy Path

(deftest comp-def__2-ary__with-no-unit-defs-in-module-def__returns-updated-module-def
  (is (= {:def/type :module
          :app.module/kw :app
          :module/kw :mod
          :module/path "/mod"
          :module/unit-defs
          [{:def/type :comp
            :module/kw :mod
            :module/path "src/{{root-ns-path}}/mod"
            :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
            :unit/kw :cmp
            :unit/libspec ['{{root-ns}}.mod.cmp-comp :as 'cmp-comp]
            :unit/path "src/{{root-ns-path}}/mod/cmp_comp.clj"
            :unit/src
            "(ns {{root-ns}}.mod.cmp-comp\n  \"Cmp component.\"\n  (:require\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n(defn cmp [props]\n  (x/html\n   [:div {:class ::container}\n    [:p (::cmp props)]\n    [:p (::FIXME props)]]))\n\n(def css\n  (x/css\n   [::container {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::cmp \"Cmp\"\n   ::FIXME \"FIXME\"})\n"
            :unit/strings-sym 'cmp-comp/strings
            :unit/css-sym 'cmp-comp/css
            :unit/template-fn basic-comp/create-data}]}
         (sut/comp-def {:def/type      :module
                        :app.module/kw :app
                        :module/kw     :mod
                        :module/path   "/mod"}
                       {:kw :cmp}))))

(deftest comp-def__2-ary__with-unit-defs-in-module-def__returns-updated-module-def
  (is (= {:def/type :module
          :app.module/kw :app
          :module/kw :mod
          :module/path "/mod"
          :module/unit-defs
          [{:FAKE 1}
           {:FAKE 2}
           {:def/type :comp
            :module/kw :mod
            :module/path "src/{{root-ns-path}}/mod"
            :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
            :unit/kw :cmp
            :unit/libspec ['{{root-ns}}.mod.cmp-comp :as 'cmp-comp]
            :unit/path "src/{{root-ns-path}}/mod/cmp_comp.clj"
            :unit/src
            "(ns {{root-ns}}.mod.cmp-comp\n  \"Cmp component.\"\n  (:require\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n(defn cmp [props]\n  (x/html\n   [:div {:class ::container}\n    [:p (::cmp props)]\n    [:p (::FIXME props)]]))\n\n(def css\n  (x/css\n   [::container {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::cmp \"Cmp\"\n   ::FIXME \"FIXME\"})\n"
            :unit/strings-sym 'cmp-comp/strings
            :unit/css-sym 'cmp-comp/css
            :unit/template-fn basic-comp/create-data}]}
         (sut/comp-def {:def/type         :module
                        :app.module/kw    :app
                        :module/kw        :mod
                        :module/path      "/mod"
                        :module/unit-defs [{:FAKE 1} {:FAKE 2}]}
                       {:kw :cmp}))))

;;; comp-def Arity 2 - Validation - Required Params

(deftest comp-def__2-ary__with-nil-module-def-and-nil-params__throws-error
  (is (thrown? AssertionError (sut/comp-def nil nil))))

(deftest comp-def__2-ary__with-nil-module-def__throws-error
  (is (thrown? AssertionError (sut/comp-def nil {:kw :cmp}))))

(deftest comp-def__2-ary__with-nil-params__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw :app
                                             :module/kw     :mod
                                             :module/path   "src/a/mod"}
                                            nil))))

(deftest comp-def__2-ary__with-nil-app.module-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw nil
                                             :module/kw     :mod
                                             :module/path   "src/a/mod"}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-invalid-app.module-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw "INVALID"
                                             :module/kw     :mod
                                             :module/path   "src/a/mod"}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-nil-def-type__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      nil
                                             :app.module/kw :app
                                             :module/kw     :mod
                                             :module/path   "src/a/mod"}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-invalid-def-type__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :INVALID
                                             :app.module/kw :app
                                             :module/kw     :mod
                                             :module/path   "src/a/mod"}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-nil-module-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw :app
                                             :module/kw     nil
                                             :module/path   "src/a/mod"}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-invalid-module-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw :app
                                             :module/kw     "INVALID"
                                             :module/path   "src/a/mod"}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-nil-module-path__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw :app
                                             :module/kw     :mod
                                             :module/path   nil}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-invalid-module-path__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw :app
                                             :module/kw     :mod
                                             :module/path   :INVALID}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-nil-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw :app
                                             :module/kw     :mod
                                             :module/path   "src/a/mod"}
                                            {:kw nil}))))

(deftest comp-def__2-ary__with-invalid-kw__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type      :module
                                             :app.module/kw :app
                                             :module/kw     :mod
                                             :module/path   "src/a/mod"}
                                            {:kw "INVALID"}))))

;;; comp-def Arity 2 - Validation - Optional Params

(deftest comp-def__2-ary__with-invalid-module-unit-defs__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type         :module
                                             :app.module/kw    :app
                                             :module/kw        :mod
                                             :module/path      "src/a/mod"
                                             :module/unit-defs {}}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-invalid-wip?__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type         :module
                                             :app.module/kw    :app
                                             :module/kw        :mod
                                             :module/path      "src/a/mod"
                                             :wip?             :INVALID}
                                            {:kw :cmp}))))

(deftest comp-def__2-ary__with-invalid-template-fn__throws-error
  (is (thrown? AssertionError (sut/comp-def {:def/type         :module
                                             :app.module/kw    :app
                                             :module/kw        :mod
                                             :module/path      "src/a/mod"}
                                            {:kw          :cmp
                                             :template-fn "INVALID"}))))

;;;; module-def

;;; module-def - Happy Path

(deftest module-def__with-required-params__returns-wip-module-def
  (is (= {:def/type :module
          :module/kw :dashboard
          :app.module/kw :app
          :module/path "src/{{root-ns-path}}/dashboard"
          :wip? true}
         (sut/module-def {:kw :dashboard}))))

(deftest module-def__with-children__returns-module-def
  (is (= {:def/type :module
          :module/kw :about
          :app.module/kw :app
          :app.stylesheets/path "src/{{root-ns-path}}/app/stylesheets.clj"
          :app.strings/path "src/{{root-ns-path}}/app/strings.clj"
          :app.routes/path "src/{{root-ns-path}}/app/routes.clj"
          :module/path "src/{{root-ns-path}}/about"
          :module.api/libspec '[{{root-ns}}.about.module-api :as about]
          :module.api/path "src/{{root-ns-path}}/about/module_api.clj"
          :module.api/routes-sym 'about/routes
          :module.api/strings-sym 'about/strings
          :module.api/css-sym 'about/css
          :module.api/src
          "(ns {{root-ns}}.about.module-api\n  \"The API that app infrastructure uses to integrate this module.\n  NOTE: Xilk Web App REPL tools automatically update this file.\")\n\n(def routes\n  nil)\n\n(def css\n  (str\n   nil))\n\n(def strings\n  (merge\n   nil))\n"
          :module/unit-defs [{:FAKE 1, :module/kw :about}
                             {:FAKE 2, :module/kw :about}
                             {:FAKE 3, :module/kw :about}]}
         (sut/module-def {:kw :about
                          :children [{:FAKE 1} {:FAKE 2} {:FAKE 3}]}))))

;;; module-def - Validation - Required Params

(deftest module-def__with-nil-params__throws-error
  (is (thrown? AssertionError (sut/module-def nil))))

(deftest module-def__with-empty-params__throws-error
  (is (thrown? AssertionError (sut/module-def {}))))

(deftest module-def__with-invalid-kw__throws-error
  (is (thrown? AssertionError (sut/module-def {:kw "INVALID"}))))

;;; module-def - Validation - Optional Params

(deftest module-def__with-invalid-children__throws-error
  (is (thrown? AssertionError (sut/module-def {:kw       :mod
                                               :children {}}))))

;;;; screen-def

;;; screen-def Arity 1 - Happy Path

(deftest screen-def__with-required-params__returns-screen-def
  (is (= {:def/type :screen
          :app.module/kw :app
          :module/kw :mod
          :module/path "src/{{root-ns-path}}/mod"
          :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
          :theme/kw :default
          :theme/path "src/{{root-ns-path}}/app/default_theme/theme.clj"
          :unit/kw :scr
          :unit/libspec ['{{root-ns}}.mod.scr-screen :as 'scr-screen]
          :unit/path "src/{{root-ns-path}}/mod/scr_screen.clj"
          :unit/route ["/mod/scr" {:name :mod/scr
                                   :get 'scr-screen/get-handler}]
          :unit.route/methods [:get]
          :unit.route/path "/mod/scr"
          :unit/src
          "(ns {{root-ns}}.mod.scr-screen\n  \"Scr screen view and controller.\"\n  (:require\n   [{{root-ns}}.app.default-theme.theme :as theme]\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n;;;; View\n\n(defn html [props]\n  (x/html\n   [:h1 {:class ::heading}\n        (::scr props)]\n   [:p (::FIXME props)]))\n\n(def css\n  (x/css\n   [::heading {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::scr-title \"Scr\"\n   ::scr \"Scr\"\n   ::FIXME \"FIXME\"})\n\n;;;; Controller\n\n(defn get-handler [req]\n  (-> req\n      (x/create-props {:screen.html.head.title/str-kw ::scr-title})\n      (theme/screen-resp html)))\n"
          :unit/strings-sym 'scr-screen/strings
          :unit/css-sym 'scr-screen/css
          :unit/template-fn basic-screen/create-data}
         (sut/screen-def {:kw               :scr
                          :parent-module-kw :mod
                          :route-path       "/mod/scr"}))))

(deftest screen-def__with-post-route-method__returns-screen-def
  (is (= {:def/type :screen
          :app.module/kw :app
          :module/kw :mod
          :module/path "src/{{root-ns-path}}/mod"
          :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
          :theme/kw :default
          :theme/path "src/{{root-ns-path}}/app/default_theme/theme.clj"
          :unit/kw :scr
          :unit/libspec ['{{root-ns}}.mod.scr-screen :as 'scr-screen]
          :unit/path "src/{{root-ns-path}}/mod/scr_screen.clj"
          :unit/route ["/mod/scr" {:name :mod/scr
                                   :post 'scr-screen/post-handler}]
          :unit.route/methods [:post]
          :unit.route/path "/mod/scr"
          :unit/src
          "(ns {{root-ns}}.mod.scr-screen\n  \"Scr screen view and controller.\"\n  (:require\n   [{{root-ns}}.app.default-theme.theme :as theme]\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n;;;; View\n\n(defn html [props]\n  (x/html\n   [:h1 {:class ::heading}\n        (::scr props)]\n   [:p (::FIXME props)]))\n\n(def css\n  (x/css\n   [::heading {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::scr-title \"Scr\"\n   ::scr \"Scr\"\n   ::FIXME \"FIXME\"})\n\n;;;; Controller\n\n(defn post-handler [req]\n  (-> req\n      (x/create-props {:screen.html.head.title/str-kw ::scr-title})\n      (theme/screen-resp html)))\n"
          :unit/strings-sym 'scr-screen/strings
          :unit/css-sym 'scr-screen/css
          :unit/template-fn basic-screen/create-data}
         (sut/screen-def {:kw               :scr
                          :parent-module-kw :mod
                          :route-path       "/mod/scr"
                          :route-method     :post}))))

(deftest screen-def__with-get-and-post-route-method__returns-screen-def
  (is (= {:def/type :screen
          :app.module/kw :app
          :module/kw :mod
          :module/path "src/{{root-ns-path}}/mod"
          :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
          :theme/kw :default
          :theme/path "src/{{root-ns-path}}/app/default_theme/theme.clj"
          :unit/kw :scr
          :unit/libspec ['{{root-ns}}.mod.scr-screen :as 'scr-screen]
          :unit/path "src/{{root-ns-path}}/mod/scr_screen.clj"
          :unit/route ["/mod/scr" {:name :mod/scr
                                   :get 'scr-screen/get-handler
                                   :post 'scr-screen/post-handler}]
          :unit.route/methods [:get :post]
          :unit.route/path "/mod/scr"
          :unit/src
          "(ns {{root-ns}}.mod.scr-screen\n  \"Scr screen view and controller.\"\n  (:require\n   [{{root-ns}}.app.default-theme.theme :as theme]\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n;;;; View\n\n(defn html [props]\n  (x/html\n   [:h1 {:class ::heading}\n        (::scr props)]\n   [:p (::FIXME props)]))\n\n(def css\n  (x/css\n   [::heading {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::scr-title \"Scr\"\n   ::scr \"Scr\"\n   ::FIXME \"FIXME\"})\n\n;;;; Controller\n\n(defn get-handler [req]\n  (-> req\n      (x/create-props {:screen.html.head.title/str-kw ::scr-title})\n      (theme/screen-resp html)))\n\n(defn post-handler [req]\n  (-> req\n      (x/create-props {:screen.html.head.title/str-kw ::scr-title})\n      (theme/screen-resp html)))\n"
          :unit/strings-sym 'scr-screen/strings
          :unit/css-sym 'scr-screen/css
          :unit/template-fn basic-screen/create-data}
         (sut/screen-def {:kw               :scr
                          :parent-module-kw :mod
                          :route-path       "/mod/scr"
                          :route-method     [:get :post]}))))

(deftest screen-def__with-template-fn__returns-screen-def
  (let [custom-fn (constantly {:src "BESPOKE SOURCE CODE"
                               :strings-sym-str "bespoke-strs-sym"
                               :css-sym-str "bespoke-stys-sym"})]
    (is (= {:def/type :screen
            :app.module/kw :app
            :module/kw :mod
            :module/path "src/{{root-ns-path}}/mod"
            :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
            :theme/kw :default
            :theme/path "src/{{root-ns-path}}/app/default_theme/theme.clj"
            :unit/kw :scr
            :unit/libspec ['{{root-ns}}.mod.scr-screen :as 'scr-screen]
            :unit/path "src/{{root-ns-path}}/mod/scr_screen.clj"
            :unit/route ["/mod/scr" {:name :mod/scr
                                     :get 'scr-screen/get-handler}]
            :unit.route/methods [:get]
            :unit.route/path "/mod/scr"
            :unit/src "BESPOKE SOURCE CODE"
            :unit/strings-sym 'scr-screen/bespoke-strs-sym
            :unit/css-sym 'scr-screen/bespoke-stys-sym
            :unit/template-fn custom-fn}
           (sut/screen-def {:kw               :scr
                            :parent-module-kw :mod
                            :route-path       "/mod/scr"
                            :template-fn      custom-fn})))))

(deftest screen-def__with-theme-kw__returns-screen-def
  (is (= {:def/type :screen
          :app.module/kw :app
          :module/kw :mod
          :module/path "src/{{root-ns-path}}/mod"
          :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
          :theme/kw :casual
          :theme/path "src/{{root-ns-path}}/app/casual_theme/theme.clj"
          :unit/kw :scr
          :unit/libspec ['{{root-ns}}.mod.scr-screen :as 'scr-screen]
          :unit/path "src/{{root-ns-path}}/mod/scr_screen.clj"
          :unit/route ["/mod/scr" {:name :mod/scr
                                   :get 'scr-screen/get-handler}]
          :unit.route/methods [:get]
          :unit.route/path "/mod/scr"
          :unit/src
          "(ns {{root-ns}}.mod.scr-screen\n  \"Scr screen view and controller.\"\n  (:require\n   [{{root-ns}}.app.casual-theme.theme :as theme]\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n;;;; View\n\n(defn html [props]\n  (x/html\n   [:h1 {:class ::heading}\n        (::scr props)]\n   [:p (::FIXME props)]))\n\n(def css\n  (x/css\n   [::heading {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::scr-title \"Scr\"\n   ::scr \"Scr\"\n   ::FIXME \"FIXME\"})\n\n;;;; Controller\n\n(defn get-handler [req]\n  (-> req\n      (x/create-props {:screen.html.head.title/str-kw ::scr-title})\n      (theme/screen-resp html)))\n"
          :unit/strings-sym 'scr-screen/strings
          :unit/css-sym 'scr-screen/css
          :unit/template-fn basic-screen/create-data}
         (sut/screen-def {:kw               :scr
                          :parent-module-kw :mod
                          :route-path       "/mod/scr"
                          :theme-kw         :casual}))))

;;; screen-def Arity 1 - Validation - Required Params

(deftest screen-def__with-nil-params__throws-error
  (is (thrown? AssertionError (sut/screen-def nil))))

(deftest screen-def__with-nil-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               nil
                                               :parent-module-kw :mod
                                               :route-path       "/mod/scr"}))))

(deftest screen-def__with-invalid-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               "INVALID"
                                               :parent-module-kw :mod
                                               :route-path       "/mod/scr"}))))

(deftest screen-def__with-nil-parent-module-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw nil
                                               :route-path       "/mod/scr"}))))

(deftest screen-def__with-invalid-parent-module-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw "INVALID"
                                               :route-path       "/mod/scr"}))))

(deftest screen-def__with-nil-route-path__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw :mod
                                               :route-path       nil}))))

(deftest screen-def__with-invalid-route-path__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw :mod
                                               :route-path       :INVALID}))))

;;; screen-def Arity 1 - Validation - Optional Params

(deftest screen-def__with-invalid-single-route-method__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw :mod
                                               :route-path       "/mod/scr"
                                               :route-method     :INVALID}))))

(deftest screen-def__with-invalid-multiple-route-methods__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw :mod
                                               :route-path       "/mod/scr"
                                               :route-method     [:get
                                                                  :INVALID]}))))

(deftest screen-def__with-invalid-template-fn__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw :mod
                                               :route-path       "/mod/scr"
                                               :template-fn      "INVALID"}))))

(deftest screen-def__with-invalid-theme-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:kw               :scr
                                               :parent-module-kw :mod
                                               :route-path       "/mod/scr"
                                               :theme-kw         "INVALID"}))))

;;; screen-def Arity 2 - Happy Path

(deftest screen-def__2-ary__with-no-unit-defs-in-module-def__returns-updated-module-def
  (is (= {:def/type :module
          :app.module/kw :app
          :module/kw :mod
          :module/path "/mod"
          :module/unit-defs
          [{:def/type :screen
            :app.module/kw :app
            :module/kw :mod
            :module/path "src/{{root-ns-path}}/mod"
            :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
            :theme/kw :default
            :theme/path "src/{{root-ns-path}}/app/default_theme/theme.clj"
            :unit/kw :scr
            :unit/libspec ['{{root-ns}}.mod.scr-screen :as 'scr-screen]
            :unit/path "src/{{root-ns-path}}/mod/scr_screen.clj"
            :unit/route ["/mod/scr" {:name :mod/scr
                                     :get 'scr-screen/get-handler}]
            :unit.route/methods [:get]
            :unit.route/path "/mod/scr"
            :unit/src
            "(ns {{root-ns}}.mod.scr-screen\n  \"Scr screen view and controller.\"\n  (:require\n   [{{root-ns}}.app.default-theme.theme :as theme]\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n;;;; View\n\n(defn html [props]\n  (x/html\n   [:h1 {:class ::heading}\n        (::scr props)]\n   [:p (::FIXME props)]))\n\n(def css\n  (x/css\n   [::heading {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::scr-title \"Scr\"\n   ::scr \"Scr\"\n   ::FIXME \"FIXME\"})\n\n;;;; Controller\n\n(defn get-handler [req]\n  (-> req\n      (x/create-props {:screen.html.head.title/str-kw ::scr-title})\n      (theme/screen-resp html)))\n"
            :unit/strings-sym 'scr-screen/strings
            :unit/css-sym 'scr-screen/css
            :unit/template-fn basic-screen/create-data}]}
         (sut/screen-def {:def/type      :module
                          :app.module/kw :app
                          :module/kw     :mod
                          :module/path   "/mod"}
                         {:kw :scr, :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-unit-defs-in-module-def__returns-updated-module-def
  (is (= {:def/type :module
          :app.module/kw :app
          :module/kw :mod
          :module/path "/mod"
          :module/unit-defs
          [{:FAKE 1}
           {:def/type :screen
            :app.module/kw :app
            :module/kw :mod
            :module/path "src/{{root-ns-path}}/mod"
            :module.api/path "src/{{root-ns-path}}/mod/module_api.clj"
            :theme/kw :default
            :theme/path "src/{{root-ns-path}}/app/default_theme/theme.clj"
            :unit/kw :scr
            :unit/libspec ['{{root-ns}}.mod.scr-screen :as 'scr-screen]
            :unit/path "src/{{root-ns-path}}/mod/scr_screen.clj"
            :unit/route ["/mod/scr" {:name :mod/scr
                                     :get 'scr-screen/get-handler}]
            :unit.route/methods [:get]
            :unit.route/path "/mod/scr"
            :unit/src
            "(ns {{root-ns}}.mod.scr-screen\n  \"Scr screen view and controller.\"\n  (:require\n   [{{root-ns}}.app.default-theme.theme :as theme]\n   [garden.units :refer [px]]\n   [xilk.xp.web-app.ui :as x]))\n\n;;;; View\n\n(defn html [props]\n  (x/html\n   [:h1 {:class ::heading}\n        (::scr props)]\n   [:p (::FIXME props)]))\n\n(def css\n  (x/css\n   [::heading {:border [[(px 2) :solid]]}]))\n\n(def strings\n  {::scr-title \"Scr\"\n   ::scr \"Scr\"\n   ::FIXME \"FIXME\"})\n\n;;;; Controller\n\n(defn get-handler [req]\n  (-> req\n      (x/create-props {:screen.html.head.title/str-kw ::scr-title})\n      (theme/screen-resp html)))\n"
            :unit/strings-sym 'scr-screen/strings
            :unit/css-sym 'scr-screen/css
            :unit/template-fn basic-screen/create-data}]}
         (sut/screen-def {:def/type      :module
                          :app.module/kw :app
                          :module/kw     :mod
                          :module/path   "/mod"
                          :module/unit-defs [{:FAKE 1}]}
                         {:kw :scr, :route-path "/mod/scr"}))))

;;; screen-def Arity 2 - Validation - Required Params

(deftest screen-def__2-ary__with-nil-module-def-and-nil-params__throws-error
  (is (thrown? AssertionError (sut/screen-def nil nil))))

(deftest screen-def__2-ary__with-nil-module-def__throws-error
  (is (thrown? AssertionError (sut/screen-def nil {:kw         :scr
                                                   :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-nil-params__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              nil))))

(deftest screen-def__2-ary__with-nil-app.module-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw nil
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-invalid-app.module-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw "INVALID"
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-nil-def-type__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      nil
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-invalid-def-type__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :INVALID
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-nil-module-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     nil
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-invalid-module-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     "INVALID"
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-nil-module-path__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   nil}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-invalid-module-path__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   :INVALID}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-nil-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         nil
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-invalid-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         "INVALID"
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-nil-route-path__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path nil}))))

(deftest screen-def__2-ary__with-invalid-route-path__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw         :scr
                                               :route-path :INVALID}))))

(deftest screen-def__2-ary__with-unneeded-parent-module-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type      :module
                                               :app.module/kw :app
                                               :module/kw     :mod
                                               :module/path   "src/a/mod"}
                                              {:kw               :scr
                                               :route-path       "/mod/scr"
                                               :parent-module-kw :mod}))))

;;; screen-def Arity 2 - Validation - Optional Params

(deftest screen-def__2-ary__with-invalid-module-unit-defs__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type         :module
                                               :app.module/kw    :app
                                               :module/kw        :mod
                                               :module/path      "src/a/mod"
                                               :module/unit-defs {}}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-invalid-wip?__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type         :module
                                               :app.module/kw    :app
                                               :module/kw        :mod
                                               :module/path      "src/a/mod"
                                               :wip?             :INVALID}
                                              {:kw         :scr
                                               :route-path "/mod/scr"}))))

(deftest screen-def__2-ary__with-invalid-single-route_method__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type         :module
                                               :app.module/kw    :app
                                               :module/kw        :mod
                                               :module/path      "src/a/mod"}
                                              {:kw               :scr
                                               :route-path       "/mod/scr"
                                               :route-method     :INVALID}))))

(deftest screen-def__2-ary__with-invalid-multiple-route_methods__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type         :module
                                               :app.module/kw    :app
                                               :module/kw        :mod
                                               :module/path      "src/a/mod"}
                                              {:kw               :scr
                                               :route-path       "/mod/scr"
                                               :route-method     [:INVALID
                                                                  :post]}))))

(deftest screen-def__2-ary__with-invalid-template-fn__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type         :module
                                               :app.module/kw    :app
                                               :module/kw        :mod
                                               :module/path      "src/a/mod"}
                                              {:kw               :scr
                                               :route-path       "/mod/scr"
                                               :template-fn      "INVALID"}))))

(deftest screen-def__2-ary__with-invalid-theme-kw__throws-error
  (is (thrown? AssertionError (sut/screen-def {:def/type         :module
                                               :app.module/kw    :app
                                               :module/kw        :mod
                                               :module/path      "src/a/mod"}
                                              {:kw               :scr
                                               :route-path       "/mod/scr"
                                               :theme-kw         "INVALID"}))))
