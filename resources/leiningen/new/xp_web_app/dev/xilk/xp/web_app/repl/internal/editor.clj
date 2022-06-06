(ns ^:no-doc xilk.xp.web-app.repl.internal.editor
  "INTERNAL: infrastructure source code editor."
  (:require
   [rewrite-clj.node :as n]
   [rewrite-clj.zip :as z]))

;;;; Helpers

(defn- to-root [zloc]
  (-> zloc
      z/root
      (z/edn {:track-position? true})))

(defn- align-space-count [zloc-node]
  {:pre [(map? zloc-node)
         (some? (:position zloc-node))]}
  (-> zloc-node z/position second dec))

(defn- insert-into-ns-require [zloc libspec]
  (let [zloc-ns (-> zloc (z/find-value z/next 'ns))]
    (if-let [zloc-require-form (z/find zloc-ns #(= (-> % z/down z/sexpr)
                                                   :require))]
      ;; Insert into existing require form
      (let [indent (-> zloc-require-form z/down z/rightmost align-space-count)]
        (-> zloc-require-form
            (z/append-child* (n/newlines 1))
            (z/append-child (n/spaces indent))
            (z/append-child libspec)))
      ;; Insert a new require form, then insert into that
      (-> zloc-ns
          z/up
          (z/append-child* (n/newlines 1))
          (z/append-child (n/spaces 2))
          (z/append-child '(:require))
          z/down z/rightmost
          (z/append-child* (n/newlines 1))
          (z/append-child (n/spaces 3))
          (z/append-child libspec)))))

;;;; routes ns

(defn- insert-into-rtr-route-data
  "Returns the `zloc-routes-track-position` with the `routes-sym` added.

  Expects `zloc-routes-track-position` to contain the following form:
  `(def app-route-data
    (concat example-module/routes ,,,))`"
  [zloc-routes-track-position routes-sym]
  (let [zloc-concat-form (-> zloc-routes-track-position
                             (z/find-value z/next 'app-route-data)
                             z/rightmost)
        indent (-> zloc-concat-form z/down z/rightmost align-space-count)]
    (-> zloc-concat-form
        (z/append-child* (n/newlines 1))
        (z/append-child (n/spaces indent))
        (z/append-child routes-sym))))

(defn add-to-routes
  ([module-def]
   {:pre [(some? module-def)
          (string? (:app.routes/path module-def))]}
   (add-to-routes module-def (-> module-def
                                :app.routes/path
                                (z/of-file {:track-position? true}))))
  ([module-def zloc-routes-track-position]
   {:pre [(some? module-def)
          (some? zloc-routes-track-position)
          (or (symbol? (:module.api/libspec module-def))
              (vector? (:module.api/libspec module-def)))
          (symbol? (:module.api/routes-sym module-def))
          (some? (:position zloc-routes-track-position))
          (some? (-> zloc-routes-track-position
                     (z/find-value z/next 'app-route-data)))]}
   (-> zloc-routes-track-position
       (insert-into-ns-require (:module.api/libspec module-def))
       to-root
       (insert-into-rtr-route-data (:module.api/routes-sym module-def))
       z/root-string)))

(comment
  (println (add-to-routes {:app.routes/path "src/{{root-ns-path}}/app/routes.clj"
                           :module.api/libspec :ADDED-LIBSPEC
                           :module.api/routes-sym :ADDED-RTS})))

;; strings ns

(defn- insert-into-strs-strings
  "Returns the `zloc-strings-track-position` with the `strings-sym` added.

  Expects `zloc-strings-track-position` to contain the following form:
  `(def all-base-lang-strs
    (merge example-module/strings ,,,))`"
  [zloc-strings-track-position strings-sym]
  (let [zloc-merge-form (-> zloc-strings-track-position
                            (z/find-value z/next 'all-base-lang-strs)
                            z/rightmost)
        indent (-> zloc-merge-form z/down z/rightmost align-space-count)]
    (-> zloc-merge-form
        (z/append-child* (n/newlines 1))
        (z/append-child (n/spaces indent))
        (z/append-child strings-sym))))

(defn add-to-strings
  ([module-def]
   {:pre [(some? module-def)
          (string? (:app.strings/path module-def))]}
   (add-to-strings module-def (-> module-def
                                  :app.strings/path
                                  (z/of-file {:track-position? true}))))
  ([module-def zloc-strings-track-position]
   {:pre [(some? module-def)
          (some? zloc-strings-track-position)
          (or (symbol? (:module.api/libspec module-def))
              (vector? (:module.api/libspec module-def)))
          (symbol? (:module.api/strings-sym module-def))
          (some? (:position zloc-strings-track-position))
          (some? (-> zloc-strings-track-position
                     (z/find-value z/next 'all-base-lang-strs)))]}
   (-> zloc-strings-track-position
       (insert-into-ns-require (:module.api/libspec module-def))
       to-root
       (insert-into-strs-strings (:module.api/strings-sym module-def))
       z/root-string)))

(comment
  (println (add-to-strings {:app.strings/path "src/{{root-ns-path}}/app/strings.clj"
                            :module.api/libspec :ADDED-LIBSPEC
                            :module.api/strings-sym :ADDED-STRS})))

;; stylesheets ns

(defn- insert-into-ss-css
  "Returns the `zloc-stylesheets-track-position` with the `css-sym` added.

  Expects `zloc-stylesheets-track-position` to contain the following form:
  `(def module-css
    (concat example-module/styles ,,,))`"
  [zloc-stylesheets-track-position css-sym]
  (let [zloc-concat-form (-> zloc-stylesheets-track-position
                             (z/find-value z/next 'module-css)
                             z/rightmost)
        indent (-> zloc-concat-form z/down z/rightmost align-space-count)]
    (-> zloc-concat-form
        (z/append-child* (n/newlines 1))
        (z/append-child (n/spaces indent))
        (z/append-child css-sym))))

(defn add-to-stylesheets
  ([module-def]
   {:pre [(some? module-def)
          (string? (:app.stylesheets/path module-def))]}
   (add-to-stylesheets module-def (-> module-def
                                      :app.stylesheets/path
                                      (z/of-file {:track-position? true}))))
  ([module-def zloc-stylesheets-track-position]
   {:pre [(some? module-def)
          (some? zloc-stylesheets-track-position)
          (or (symbol? (:module.api/libspec module-def))
              (vector? (:module.api/libspec module-def)))
          (symbol? (:module.api/css-sym module-def))
          (some? (:position zloc-stylesheets-track-position))
          (some? (-> zloc-stylesheets-track-position
                     (z/find-value z/next 'module-css)))]}
   (-> zloc-stylesheets-track-position
       (insert-into-ns-require (:module.api/libspec module-def))
       to-root
       (insert-into-ss-css (:module.api/css-sym module-def))
       z/root-string)))

(comment
  (println (add-to-stylesheets {:app.stylesheets/path "src/{{root-ns-path}}/app/stylesheets.clj"
                                :module.api/libspec :ADDED-LIBSPEC
                                :module.api/css-sym :ADDED-CSS})))

;;;; module-api ns's

(defn- sorted-route-param-vecs
  #_{:clj-kondo/ignore [:shadowed-var]}
  [{:keys [get name post] :as r-params}]
  (let [top (cond-> []
                    (some? name) (conj [:name name])
                    (some? get) (conj [:get get])
                    (some? post) (conj [:post post]))]
    (if-let [bottom (-> r-params
                        (dissoc :name :get :post)
                        seq)]
      (into top bottom)
      top)))

(comment
  (sorted-route-param-vecs {:name :a :get :b})
  (sorted-route-param-vecs {:name :a :get :b :post :c})
  (sorted-route-param-vecs {:name :a, :x 0, :y 0, :z 0}))

(defn- insert-sorted-route-params
  [zloc-r-path r-params]
  (let [rps (sorted-route-param-vecs r-params)
        first-rp (first rps)
        zloc-rp-map (-> zloc-r-path
                        (z/insert-right {})
                        z/right)
        indent (-> zloc-rp-map z/position second)]
    (loop [zloc (-> zloc-rp-map
                    (z/append-child (first first-rp))
                    (z/append-child (second first-rp)))
           more (next rps)]
      (if (some? more)
        (recur (-> zloc
                   (z/append-child* (n/newlines 1))
                   (z/append-child (n/spaces indent))
                   (z/append-child (-> more first first))
                   (z/append-child (-> more first second)))
               (next more))
        zloc))))

(defn- insert-into-module-api-routes
  [zloc-routes-val indent route]
  (let [r-path (first route)
        r-params (second route)]
    (if (nil? (-> zloc-routes-val z/sexpr))
      ;; Replace nil with a parent vec containing an incomplete route vec, move
      ;; into it, insert route params map
      (-> zloc-routes-val
          (z/insert-right [[r-path]])
          z/remove
          z/right z/down z/down
          (insert-sorted-route-params r-params))
      ;; Append an incomplete route vec, move into it, insert route params map
      (-> zloc-routes-val
          (z/append-child* (n/newlines 1))
          (z/append-child (n/spaces indent))
          (z/append-child [r-path])
          z/down z/rightmost z/down
          (insert-sorted-route-params r-params)))))

(defn- insert-into-module-api
  [zloc-module-api module-api-var-sym new-data]
  (let [zloc-val (-> zloc-module-api
                     (z/find-value z/next module-api-var-sym)
                     z/rightmost)
        indent (if (nil? (-> zloc-val z/sexpr))
                 (-> zloc-val align-space-count)
                 (-> zloc-val z/down z/rightmost align-space-count))]
    (cond
      ;; Routes: handle differently than strings and css
      (= module-api-var-sym 'routes)
      (insert-into-module-api-routes zloc-val indent new-data)

      ;; Strings/css data missing: replace nil value with new-data
      (nil? (-> zloc-val z/down z/rightmost z/sexpr))
      (-> zloc-val
          z/down z/rightmost
          (z/insert-left new-data)
          z/remove)

      ;; Strings/css data present: add new-data
      :else
      (-> zloc-val
          (z/append-child* (n/newlines 1))
          (z/append-child (n/spaces indent))
          (z/append-child new-data)))))

(defn add-to-module-api
  ([unit-def]
   {:pre [(some? unit-def)
          (string? (:module.api/path unit-def))]}
   (add-to-module-api unit-def (-> unit-def
                                :module.api/path
                                (z/of-file {:track-position? true}))))
  ([unit-def zloc-module-api-track-position]
   {:pre [(some? unit-def)
          (some? zloc-module-api-track-position)
          (or (symbol? (:unit/libspec unit-def))
              (vector? (:unit/libspec unit-def)))
          (symbol? (:unit/strings-sym unit-def))
          (symbol? (:unit/css-sym unit-def))
          (or (nil? (:unit/route unit-def))
              (and (vector? (:unit/route unit-def))
                   (map? (second (:unit/route unit-def)))))
          (some? (:position zloc-module-api-track-position))
          (or (nil? (:unit/route unit-def))
              (some? (-> zloc-module-api-track-position
                         (z/find-value z/next 'routes))))
          (some? (-> zloc-module-api-track-position
                     (z/find-value z/next 'strings)))
          (some? (-> zloc-module-api-track-position
                     (z/find-value z/next 'css)))]}
   (let [zloc (-> zloc-module-api-track-position
                  (insert-into-ns-require
                    (:unit/libspec unit-def))
                  to-root
                  (insert-into-module-api 'strings (:unit/strings-sym unit-def))
                  to-root
                  (insert-into-module-api 'css (:unit/css-sym unit-def)))
         route (:unit/route unit-def)]
     (if (some? route)
       (-> zloc
           to-root
           (insert-into-module-api 'routes (:unit/route unit-def))
           z/root-string)
       (z/root-string zloc)))))

(comment
  (println (add-to-module-api
            {:module.api/path "src/{{root-ns-path}}/home/module_api.clj"
             :unit/libspec :ADDED-LIBSPEC
             :unit/route :ADDED-ROUTE
             :unit/strings-sym :ADDED-STRINGS
             :unit/css-sym :ADDED-CSS})))
