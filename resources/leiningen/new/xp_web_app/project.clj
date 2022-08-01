(defproject {{name}} "{{date-iso-no-dash}}-SNAPSHOT"
  :description "FIXME"
  ; :url "http://example.com/FIXME"
  :license {:name "None (proprietary; all rights reserved)"}
  :min-lein-version "2.9.8"
  :dependencies [[club.donutpower/system "0.0.165"] ;; System
                 [garden "1.3.10"] ;; CSS
                 [hiccup "2.0.0-alpha2"] ;; HTML
                 [metosin/reitit-core "0.5.18"] ;; Router
                 [metosin/reitit-ring "0.5.18"] ;; Router
                 [org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.9.5"] ;; Web server abstraction
                 [ring/ring-defaults "0.3.3"] ;; Web server middleware
                 [ring/ring-jetty-adapter "1.9.5"] ;; Web server
                 [tongue "0.4.4"]] ;; String Localization
  :plugins [[lein-ring "0.12.6"]]
  :main ^:skip-aot {{root-ns}}
  :target-path "target/%s"
  :repl-options {:init-ns user}
  :xilk.template/name "{{template-name}}"
  :xilk.template/url "{{template-url}}"
  :xilk.template/version "{{template-version}}"

  :profiles {:dev {:dependencies [[rewrite-clj "1.0.767-alpha"]]
                   :source-paths ["dev"]}
             :uberjar {:aot :all}}

  ;; Plugin config

  :ring
  {:handler {{root-ns}}.app/handler
   :auto-refresh? true})
