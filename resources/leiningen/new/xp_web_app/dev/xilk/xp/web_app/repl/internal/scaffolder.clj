(ns ^:no-doc xilk.xp.web-app.repl.internal.scaffolder
  "INTERNAL: new module, screen, comp scaffolding generator and integrator."
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [xilk.xp.web-app.repl.internal.editor :as editor]
   [xilk.xp.web-app.repl.internal.module-api-template :as module-api]
   [xilk.xp.web-app.repl.internal.state :as state]))

;;;; Path Helpers

(defn pathify [s]
  (-> s
      (str/replace "." "/")
      (str/replace "-" "_")))

(defn path-str [kw]
  (-> kw name pathify))

(defn module-path
  [module-kw]
  (str "src/" (pathify state/module-ns-prefix) "/" (path-str module-kw)))

(defn screen-path
  [module-kw screen-kw]
  (str (module-path module-kw) "/" (path-str screen-kw) "_screen.clj"))

(defn comp-path
  [module-kw comp-kw]
  (str (module-path module-kw) "/" (path-str comp-kw) "_comp.clj"))

;;;; Scaffold Data Definition

(defn comp? [d]
  (= :comp (:def/type d)))

(defn module? [d]
  (= :module (:def/type d)))

(defn screen? [d]
  (= :screen (:def/type d)))

(defn comp-or-screen? [d]
  (or (comp? d)
      (screen? d)))

(defn complete-comp-def
  #_{:clj-kondo/ignore [:unused-binding]}
  [{:keys [module/kw unit/kw unit/template-fn] :as wip-unit-def}]
  (let [m-kw (:module/kw wip-unit-def)
        u-kw (:unit/kw wip-unit-def)
        module-root-ns (str state/module-ns-prefix "." (name m-kw))
        m-path (module-path m-kw)
        c-kw-name (name u-kw)
        c-ns (str module-root-ns "." c-kw-name "-comp")
        c-alias (str c-kw-name "-comp")
        t-data (template-fn {:comp-kw-name c-kw-name
                             :comp-ns c-ns
                             :comp-title (-> c-kw-name
                                             (str/replace "-" " ")
                                             str/capitalize)})
        {:keys [src strings-sym-str css-sym-str]} t-data]
    (assoc wip-unit-def
           :module/kw m-kw
           :module/path m-path
           :module.api/path (str m-path "/module_api.clj")
           :unit/kw u-kw
           :unit/libspec [(symbol c-ns) :as (symbol c-alias)]
           :unit/path (comp-path m-kw u-kw)
           :unit/src src
           :unit/strings-sym (symbol c-alias strings-sym-str)
           :unit/css-sym (symbol c-alias css-sym-str))))

(defn complete-screen-def
  [{:keys [unit/template-fn unit.route/path] :as wip-unit-def}]
  (let [a-m-kw (:app.module/kw wip-unit-def)
        m-kw (:module/kw wip-unit-def)
        m-path (module-path m-kw)
        th-kw (:theme/kw wip-unit-def)
        th-ns (str state/module-ns-prefix "."
                   (name a-m-kw) "."
                   (name th-kw) "-theme.theme")
        u-kw (:unit/kw wip-unit-def)
        u-r-path path
        u-r-methods (-> wip-unit-def :unit.route/methods sort)
        m-root-ns (str state/module-ns-prefix "." (name m-kw))
        u-kw-name (name u-kw)
        u-ns (str m-root-ns "." u-kw-name "-screen")
        u-alias (str u-kw-name "-screen")
        handler-sym-strs (reduce (fn [m method]
                                   (assoc m method (-> method
                                                       name
                                                       (str "-handler"))))
                                 {}
                                 u-r-methods)
        route [u-r-path (-> handler-sym-strs
                            (update-vals #(symbol u-alias %))
                            (assoc :name (keyword (name m-kw) (name u-kw))))]
        t-data (template-fn {:handler-syms (->> handler-sym-strs
                                                vals
                                                (map symbol)
                                                sort)
                             :screen-kw-name u-kw-name
                             :screen-ns u-ns
                             :screen-title (-> u-kw-name
                                               (str/replace "-" " ")
                                               str/capitalize)
                             :theme-ns th-ns})
        {:keys [src strings-sym-str css-sym-str]} t-data]
    (assoc wip-unit-def
           :module/path m-path
           :module.api/path (str m-path "/module_api.clj")
           :theme/path (str "src/" (pathify th-ns) ".clj")
           :unit/libspec [(symbol u-ns) :as (symbol u-alias)]
           :unit/path (screen-path m-kw u-kw)
           :unit/route route
           :unit/src src
           :unit/strings-sym (symbol u-alias strings-sym-str)
           :unit/css-sym (symbol u-alias css-sym-str))))

(defn complete-module-def
  [{a-kw :app.module/kw
    m-kw :module/kw
    m-path :module/path
    moduleless-u-defs :module/unit-defs
    :as wip-def}]
  (let [a-path (module-path a-kw)
        m-kw-name (name m-kw)
        u-defs (mapv #(assoc % :module/kw m-kw) moduleless-u-defs)
        module-api-ns (str state/module-ns-prefix "." m-kw-name "." "module-api")
        module-api-d (module-api/create-data {:module-api-ns module-api-ns})
        {:keys [routes-sym-str strings-sym-str css-sym-str]} module-api-d]
    (assoc (dissoc wip-def :wip?)
           :app.routes/path (str a-path "/routes.clj")
           :app.strings/path (str a-path "/strings.clj")
           :app.stylesheets/path (str a-path "/stylesheets.clj")
           :module/unit-defs u-defs
           :module.api/libspec [(symbol module-api-ns) :as (symbol m-kw)]
           :module.api/routes-sym (symbol m-kw-name routes-sym-str)
           :module.api/strings-sym (symbol m-kw-name strings-sym-str)
           :module.api/css-sym (symbol m-kw-name css-sym-str)
           :module.api/path (str m-path "/module_api.clj")
           :module.api/src (:src module-api-d))))

;;;; Scaffold Generation

(defn add-module! [module-def]
  (.mkdir (-> module-def :module/path io/file))
  (spit (:module.api/path module-def) (:module.api/src module-def))
  (spit (:app.routes/path module-def) (editor/add-to-routes module-def))
  (spit (:app.strings/path module-def) (editor/add-to-strings module-def))
  (spit (:app.stylesheets/path module-def)
        (editor/add-to-stylesheets module-def)))

(defn add-unit! [unit-def]
  (spit (:unit/path unit-def) (:unit/src unit-def))
  (spit (:module.api/path unit-def)
        (editor/add-to-module-api unit-def)))
