(ns xilk.xp.web-app.repl
  "REPL tooling library for Xilk Web App source code automation."
  (:require
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [xilk.xp.web-app.repl.internal.basic-comp-template :as basic-comp]
   [xilk.xp.web-app.repl.internal.basic-screen-template
    :as basic-screen]
   [xilk.xp.web-app.repl.internal.scaffolder :as scaffolder]
   [xilk.xp.web-app.repl.internal.state :as state]
   [xilk.xp.web-app.repl.internal.validator :as valid]))

;;;; Initialization

;; TODO: add tests
(defn init!
  "Initializes required state for REPL tooling.
  Use in the default REPL namespace source code, usually `user.clj`, to
  initialize automatically on REPL start.

  **Arguments**

  * `module-ns-prefix`: A string specifying the namespace name under which
     modules are defined, without a trailing period/dot. This is usually the
     root namespace of the app. Example: \"com.example.myapp\".

  * `opts`: Options, as a map or keyword arguments (optional).

    - `:app-module-kw`: A keyword whose name specifies the module containing
      all shared app data. Used only when the app module is under a non-default
      name, usually because the default app module name is already in use.
      Default: `:app`.

    - `:default-theme-kw`: A keyword whose name specifies the default theme
      used to render screens defined using [[screen-def]]. The theme must be in
      the app module, in a directory named using this name, followed by
      \"-theme\". Default: `:default`.

  **Examples**

  ```clj
  ;; Initialize without options, using defaults.
  (init! \"com.example.myapp\")

  ;; Initialize with a non-default app module, using a map.
  (init! \"com.example.myapp\" {:app-module-kw :shared})

  ;; Initialize with a non-default app module, using keyword arguments.
  (init! \"com.example.myapp\" :app-module-kw :shared)

  ;; Initialize with a non-default default theme, using a map.
  (init! \"com.example.myapp\" {:default-theme-kw :earthy})

  ;; Initialize with multiple non-default options, using a map.
  (init! \"com.example.myapp\" {:app-module-kw    :shared
                                :default-theme-kw :earthy})
  ```"
  [module-ns-prefix & {:keys [app-module-kw default-theme-kw]
                       :or   {app-module-kw    :app
                              default-theme-kw :default}
                       :as   opts}]
  {:pre [(string? module-ns-prefix)
         (or (nil?     (:app-module-kw opts))
             (keyword? (:app-module-kw opts)))
         (or (nil?     (:default-theme-kw opts))
             (keyword? (:default-theme-kw opts)))]}
  (alter-var-root #'state/module-ns-prefix (constantly module-ns-prefix))
  (alter-var-root #'state/app-module-kw    (constantly app-module-kw))
  (alter-var-root #'state/default-theme-kw (constantly default-theme-kw))
  nil)

;;;; Scaffold Data Definition

(defn comp-def
  "Creates a component definition. Returns it or `module-def` with it included.

  **Arguments**

  * `params`: A map containing data for creating the component definition.

    - Required Keys

      - `:kw`: A keyword whose name specifies this component. Must be unique
        among components in the same module. The component namespace is defined
        with this name, followed by \"-comp\".

    - Optional Keys

      - `:parent-module-kw`: A keyword whose name specifies the module in which
        the component is defined. If omitted, the component is either defined
        (arity-1) in the shared components module, `comps`, nested in the app
        module, or (arity-2) in the module defined by the `module-def` argument.
        Default: (arity-1) `:app.comps`, or (arity-2) the value of `:module/kw`
        in `module-def`.

      - `:template-fn`: A function that generates the initial source code for
        the component. Default: `basic-comp-template/create-data`.

  * (arity-2) `module-def`: A map containing a module definition. The component
     is defined in this module. The updated `module-def` is returned, including
     the component definition.

  **Examples**

  ```clj
  ;; Define in the shared components module, nested in the app module.
  (comp-def {:kw :button})

  ;; Define in a specified module.
  (comp-def {:kw :button, :parent-module-kw :survey})

  ;; Define within a module definition.
  (-> (module-def {:kw :survey})
      (comp-def {:kw :button}))

  ;; Define in a specified module with a non-default component template.
  (comp-def {:kw :button, :parent-module-kw :survey, :template-fn my/template})
  ```

  **See Also**

  [[add!]]
  [[module-def]]
  [[screen-def]]"
  {:arglists '([{:keys [kw parent-module-kw template-fn] :as params}]
               [module-def {:keys [kw template-fn] :as params}])
   :see-also ["add!" "module-def" "screen-def"]}
  ([{:keys [kw parent-module-kw template-fn]
     :or {parent-module-kw (-> state/app-module-kw name (str ".comps") keyword)
          template-fn      basic-comp/create-data}
     :as params}]
   {:pre [(map? params)
          (keyword?          (:kw params))
          (or (nil?          (:parent-module-kw params))
              (keyword?      (:parent-module-kw params)))
          (or (nil?          (:template-fn params))
              (fn?           (:template-fn params)))
          (s/valid? ::valid/comp-def-params params)]
    :post [(s/valid? ::valid/comp-def %)]}
   (scaffolder/complete-comp-def {:def/type :comp
                                  :unit/kw kw
                                  :unit/template-fn template-fn
                                  :module/kw parent-module-kw}))

  (#_{:clj-kondo/ignore [:unused-binding]}
   [module-def {:keys [kw template-fn] :as params}]
   {:pre [(map?            module-def)
          (keyword?        (:app.module/kw module-def))
          (= :module       (:def/type module-def))
          (keyword?        (:module/kw module-def))
          (string?         (:module/path module-def))
          (or (nil?        (:module/unit-defs module-def))
              (sequential? (:module/unit-defs module-def)))
          (or (nil?        (:wip? module-def))
              (boolean?    (:wip? module-def)))
          (s/valid? ::valid/module-def module-def)
          (map?        params)
          (keyword?    (:kw params))
          (or (nil?    (:template-fn params))
              (fn?     (:template-fn params)))
          (s/valid? ::valid/comp-def-params params)]
    :post [(s/valid? ::valid/module-def %)]}
   (let [cd (-> params
                (assoc :parent-module-kw (:module/kw module-def))
                comp-def)]
     (if (some? (:module/unit-defs module-def))
       (update module-def :module/unit-defs conj cd)
       (assoc module-def :module/unit-defs [cd])))))

(defn screen-def
  "Creates a screen definition. Returns it or `module-def` with it included.

  **Arguments**

  * `params`: A map containing data for creating the screen definition.

    - Required Keys

      - `:kw`: A keyword whose name specifies this screen. Must be unique among
        screens in the same module. The screen namespace is defined with this
        name, followed by \"-screen\".

      - (arity-1 only) `:parent-module-kw`: A keyword whose name specifies the
        module in which the screen is defined. Do not specify in arity-2, since
        the parent module is already defined by the `module-def` argument.
        Throws an error if specified in arity-2 for this reason.

      - `:route-path`: A string specifying the URL path that the screen is
        defined to handle. If the optional `:route-method` is not specified,
        only GET requests for this path are handled; requests of any other HTTP
        methods will result in `405 Method Not Allowed` error responses.

    - Optional Keys

      - `:route-method`: A keyword or a sequence of keywords that specify the
        HTTP methods that the screen is defined to handle via the `:route-path`.
        Requests of any unspecified methods will result in `405 Method Not
        Allowed` error responses.  Examples include `:post` and `[:get :post]`.
        Default: `:get`.

      - `:template-fn`: A function that generates the initial source code for
        the screen. Default: `basic-screen-template/create-data`.

      - `:theme-kw`: A keyword whose name specifies the theme used to render the
        screen. The theme must be in the app module, in a directory named using
        this name, followed by \"-theme\". Default: the `:default-theme-kw`
        specified in [[init!]], otherwise `:default`.

  * (arity-2) `module-def`: A map containing a module definition. The screen is
    defined in this module. The updated `module-def` is returned, including the
    screen definition.

  ```clj
  ;; Define in a specified module.
  (screen-def {:kw               :privacy
               :parent-module-kw :legal
               :route-path       \"/legal/privacy\"})

  ;; Define within a module definition.
  (-> (module-def {:kw :legal})
      (screen-def {:kw               :privacy
                   :parent-module-kw :legal
                   :route-path       \"/legal/privacy\"}))

  ;; Define in a specified module with a non-default route method.
  (screen-def {:kw               :privacy
               :parent-module-kw :legal
               :route-path       \"/legal/privacy\"
               :route-method     :post})

  ;; Define in a specified module with multiple route methods.
  (screen-def {:kw               :privacy
               :parent-module-kw :legal
               :route-path       \"/legal/privacy\"
               :route-method     [:get :post]})

  ;; Define in a specified module with a non-default screen template.
  (screen-def {:kw               :privacy
               :parent-module-kw :legal
               :route-path       \"/legal/privacy\"
               :template-fn      my/template})

  ;; Define in a specified module with a non-default theme.
  (screen-def {:kw               :privacy
               :parent-module-kw :legal
               :route-path       \"/legal/privacy\"
               :theme-kw         :document})
  ```

  **See Also**

  [[add!]]
  [[comp-def]]
  [[module-def]]"
  {:arglists '([{:keys [kw parent-module-kw route-path route-method template-fn
                        theme-kw]
                 :as params}]
               [module-def {:keys [kw route-path route-method template-fn
                                   theme-kw]
                            :as params}])
   :see-also ["add!" "comp-def" "module-def"]}
  ([{:keys [kw parent-module-kw route-path route-method
            template-fn theme-kw]
     :or {route-method  :get
          theme-kw      state/default-theme-kw
          template-fn   basic-screen/create-data}
     :as params}]
   {:pre [(map?         params)
          (keyword?     (:kw params))
          (keyword?     (:parent-module-kw params))
          (string?      (:route-path params))
          (or (nil?                   (:route-method params))
              (valid/http-method-kw?  (:route-method params))
              (and (sequential?       (:route-method params))
                   (every? valid/http-method-kw?
                                      (:route-method params))))
          (or (nil?     (:template-fn params))
              (fn?      (:template-fn params)))
          (or (nil?     (:theme-kw params))
              (keyword? (:theme-kw params)))
          (s/valid? ::valid/screen-def-arity-1-params params)]
    :post [(s/valid? ::valid/screen-def %)]}
   (let [rms (if (keyword? route-method) [route-method] route-method)]
     (scaffolder/complete-screen-def {:def/type           :screen
                                      :app.module/kw      state/app-module-kw
                                      :module/kw          parent-module-kw
                                      :theme/kw           theme-kw
                                      :unit/kw            kw
                                      :unit/template-fn   template-fn
                                      :unit.route/methods rms
                                      :unit.route/path    route-path})))

  ([module-def params]
   {:pre [(map?            module-def)
          (keyword?        (:app.module/kw module-def))
          (= :module       (:def/type module-def))
          (keyword?        (:module/kw module-def))
          (string?         (:module/path module-def))
          (or (nil?        (:module/unit-defs module-def))
              (sequential? (:module/unit-defs module-def)))
          (or (nil?        (:wip? module-def))
              (boolean?    (:wip? module-def)))
          (s/valid? ::valid/module-def module-def)
          (map?         params)
          (keyword?     (:kw params))
          (nil?         (:parent-module-kw params))
          (string?      (:route-path params))
          (or (nil?                   (:route-method params))
              (valid/http-method-kw?  (:route-method params))
              (and (sequential?       (:route-method params))
                   (every? valid/http-method-kw?
                                      (:route-method params))))
          (or (nil?     (:template-fn params))
              (fn?      (:template-fn params)))
          (or (nil?     (:theme-kw params))
              (keyword? (:theme-kw params)))
          (s/valid? ::valid/screen-def-arity-2-params params)]
    :post [(s/valid? ::valid/module-def %)]}
   (let [sd (-> params
                (assoc :parent-module-kw (:module/kw module-def))
                screen-def)]
     (if (some? (:module/unit-defs module-def))
       (update module-def :module/unit-defs conj sd)
       (assoc module-def :module/unit-defs [sd])))))

(defn module-def
  "Creates and returns a module definition. Must contain children to [[add!]].

  **Arguments**

  * `params`: A map containing data for creating the module definition.

    - Required Keys

      - `:kw`: A keyword whose name specifies this module. Must be unique among
        modules in the app.

    - Optional Keys

      - `:children`: A sequence of maps defining children of this module.

  **Examples**

  ```clj
  ;; Define using a thread-first macro to add children.
  (-> (module-def {:kw :dashboard})
      (screen-def {:kw :main
                   :route-path \"/dashboard\"})
      (comp-def   {:kw :trend-graph}))

  ;; Define with children inline.
  (module-def {:kw       :dashboard
               :children [(screen-def {:kw :main
                                       :route-path \"/dashboard\"})
                          (comp-def   {:kw trend-graph})]})
  ```

  **See Also**

  [[add!]]
  [[comp-def]]
  [[screen-def]]"
  {:see-also ["add!" "comp-def" "screen-def"]}
  [{:keys [kw children] :as params}]
  {:pre [(map?            params)
         (keyword?        (:kw params))
         (or (nil?        (:children params))
             (sequential? (:children params)))
         (s/valid? ::valid/module-def-params params)]
   :post [(s/valid? ::valid/module-def %)]}
  (let [wip-def {:app.module/kw state/app-module-kw
                 :def/type      :module
                 :module/kw     kw
                 :module/path   (scaffolder/module-path kw)
                 :wip?          true}]
    (if (seq children)
      (-> wip-def
          (assoc :module/unit-defs children)
          scaffolder/complete-module-def)
      wip-def)))

;;;; Scaffold Generation

;; TODO: add tests
(defn add!
  "Adds source code scaffolding. Side effects: writes to the file system.

  **Arguments**

  * `defs`: One or more maps containing scaffolding definition data.

  **Examples**

  ```clj
  ;; Add with one def inline.
  (add! (screen-def {:kw               :privacy
                     :parent-module-kw :legal
                     :route-path       \"/legal/privacy\"}))

  ;; Add with multiple defs inline.
  (add! (screen-def {:kw               :privacy
                     :parent-module-kw :legal
                     :route-path       \"/legal/privacy\"})
        (screen-def {:kw               :terms
                     :parent-module-kw :legal
                     :route-path       \"/legal/terms\"}))

  ;; Add using a thread-first macro to compose a module def with children.
  (-> (module-def {:kw :dashboard})
      (screen-def {:kw :main
                   :route-path \"/dashboard\"})
      (comp-def   {:kw :trend-graph})
      add!)
  ```

  **See Also**

  [[comp-def]]
  [[module-def]]
  [[screen-def]]"
  [& defs]
  {:pre [(every? (fn [d] (or (s/valid? ::valid/comp-def d)
                             (s/valid? ::valid/screen-def d)
                             (s/valid? ::valid/module-def d)))
                 defs)
         (every? (fn [d] (keyword? (:module/kw d))) defs)
         (->> defs
              (filter scaffolder/comp-or-screen?)
              (every? (fn [d] (-> d :module/path io/file .exists))))
         (->> defs
              (filter scaffolder/comp-or-screen?)
              (every? (fn [d] (-> d :module/path io/file .isDirectory))))
         (->> defs
              (filter scaffolder/comp-or-screen?)
              (every? (fn [d] (-> d :module.api/path io/file .exists))))
         (->> defs
              (filter scaffolder/comp-or-screen?)
              (not-any? (fn [d] (-> d :unit/path io/file .exists))))
         (->> defs
              (filter scaffolder/screen?)
              (every? (fn [d] (-> d :theme/path io/file .exists))))
         (->> defs
              (filter scaffolder/module?)
              (every? (fn [d] (-> d :module/unit-defs sequential?))))
         (->> defs
              (filter scaffolder/module?)
              (not-any? (fn [d] (-> d :module/path io/file .exists))))]}
  (doseq [d defs]
    (cond
      (scaffolder/module? d)
      (let [d (if (:wip? d) (scaffolder/complete-module-def d) d)]
        (scaffolder/add-module! d)
        (run! add! (:module/unit-defs d)))

      (scaffolder/comp-or-screen? d)
      (scaffolder/add-unit! d))))
