(ns user
  "Initialization, symbols, and forms for use during development."
  #_{:clj-kondo/ignore [:unused-referred-var :unused-namespace]}
  (:require
   [{{root-ns}}.app :as app]
   [clojure.java.io :as io]
   [clojure.pprint :refer (pprint)]
   [clojure.repl :refer [apropos demunge dir dir-fn doc find-doc pst root-cause
                         set-break-handler! source source-fn stack-element-str
                         thread-stopper]]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.test :as test]
   [donut.system :as ds]
   [donut.system.repl :as dsr]
   [donut.system.repl.state :as dsrs]
   [xilk.xp.web-app.repl :refer [add! comp-def module-def screen-def] :as xr]))

;;;; Initialization

;;; Xilk Web App REPL Tooling
(xr/init! "{{root-ns}}")

;;; Dev System (donut.system.repl helps manage systems named ::ds/repl)
(defmethod ds/named-system ::ds/repl
  [_]
  (ds/system :prod {[:http :port] 3000}))

;;;; "Reloaded" Workflow (enhances interactive development)

(defn start
  "Starts a new dev system instance.

  Browse to http://localhost:3000/ after starting."
  []
  (dsr/start))

(defn stop
  "Shuts down and destroys the running dev system instance."
  []
  (dsr/stop))

(defn reset
  "Stops, refreshes changed source files, and starts a new dev system instance.

  Many Clojure development tools include this as a feature that can be easily
  invoked from your editor. Search your Clojure tools' docs for \"refresh\"."
  []
  (dsr/restart))

(comment
  "Dev system management forms for evaluation in a REPL-connected editor."
  (start)
  (reset)
  (stop)
  (pprint (select-keys dsrs/system [::ds/defs ::ds/instances])))

;;;; Xilk Web App Scaffolding

(comment
  "Xilk scaffolding forms previously used for this app (scaffold history)."
  (-> (module-def {:kw         :home})
      (screen-def {:kw         :home
                   :route-path "/"})
      add!)

  (add! (comp-def {:kw :file-info})))

(comment
  "Xilk scaffolding form examples.

  Evaluating `add!` modifies project source code, so committing changes to
  source control first will help make it easier to undo. Evaluating `*-def`
  returns definition data with no side effects."

  ;; Add a module with base screens and components.
  (-> (module-def {:kw         :delete-me})
      (screen-def {:kw         :example
                   :route-path "/delete-me/example"})
      (comp-def   {:kw         :sub-example})
      add!)

  ;; Add a screen, using a non-default theme, to an existing module.
  (add! (screen-def {:kw               :example-too
                     :route-path       "/delete-me/example-too"
                     :parent-module-kw :delete-me
                     :theme-kw         :starter}))

  ;; Add a component to an existing module.
  (add! (comp-def {:kw               :sub-example-too
                   :parent-module-kw :delete-me}))

  ;; Add a shared component to `app/comps/`
  (add! (comp-def {:kw :delete-me})))
