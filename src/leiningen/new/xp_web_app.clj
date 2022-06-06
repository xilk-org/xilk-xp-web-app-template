(ns leiningen.new.xp-web-app
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [leiningen.core.main :as main]
   [leiningen.new.templates :as ts]))

(def render (ts/renderer "xp_web_app"))

(defn xp-web-app
  "Creates an experimental web app project."
  [project-name]
  (let [root-ns (ts/sanitize-ns project-name)
        date-iso (ts/date)
        template-project-clj (-> "./project.clj" slurp edn/read-string)
        data {:date-iso date-iso
              :date-iso-no-dash (str/replace date-iso "-" "")
              :name (ts/project-name project-name)
              :raw-name project-name
              :root-ns root-ns
              :root-ns-path (ts/name-to-path root-ns)
              :template-name (-> template-project-clj (nth 1) (str/replace "/lein-template." "/"))
              :template-url (nth template-project-clj (-> template-project-clj (.indexOf :url) inc))
              :template-version (nth template-project-clj 2)
              :year (ts/year)}]
    (main/info "Generating fresh 'lein new' org.xilk/xp-web-app project.")
    (ts/->files
     data
     [".clj-kondo/config.edn" (render "clj-kondo/config.edn" data)]

     ["dev/user.clj" (render "dev/user.clj" data)]

     ["dev/xilk/xp/web_app/repl.clj" (render "dev/xilk/xp/web_app/repl.clj" data)]

     ["dev/xilk/xp/web_app/repl/internal/module_api_template.clj" (render "dev/xilk/xp/web_app/repl/internal/module_api_template.clj" data)]
     ["dev/xilk/xp/web_app/repl/internal/basic_comp_template.clj" (render "dev/xilk/xp/web_app/repl/internal/basic_comp_template.clj" data)]
     ["dev/xilk/xp/web_app/repl/internal/basic_screen_template.clj" (render "dev/xilk/xp/web_app/repl/internal/basic_screen_template.clj" data)]
     ["dev/xilk/xp/web_app/repl/internal/editor.clj" (render "dev/xilk/xp/web_app/repl/internal/editor.clj" data)]
     ["dev/xilk/xp/web_app/repl/internal/scaffolder.clj" (render "dev/xilk/xp/web_app/repl/internal/scaffolder.clj" data)]
     ["dev/xilk/xp/web_app/repl/internal/state.clj" (render "dev/xilk/xp/web_app/repl/internal/state.clj" data)]
     ["dev/xilk/xp/web_app/repl/internal/validator.clj" (render "dev/xilk/xp/web_app/repl/internal/validator.clj" data)]

     "doc"

     "resources/public"

     ["src/xilk/xp/web_app/ui.clj" (render "src/xilk/xp/web_app/ui.clj" data)]
     ["src/xilk/xp/web_app/ui/internal/css.clj" (render "src/xilk/xp/web_app/ui/internal/css.clj" data)]
     ["src/xilk/xp/web_app/ui/internal/props.clj" (render "src/xilk/xp/web_app/ui/internal/props.clj" data)]
     ["src/xilk/xp/web_app/ui/internal/state.clj" (render "src/xilk/xp/web_app/ui/internal/state.clj" data)]

     ["src/{{root-ns-path}}/app.clj" (render "src/root_ns_path/app.clj" data)]

     ["src/{{root-ns-path}}/app/comps/module_api.clj" (render "src/root_ns_path/app/comps/module_api.clj" data)]
     ["src/{{root-ns-path}}/app/comps/file_info_comp.clj" (render "src/root_ns_path/app/comps/file_info_comp.clj" data)]

     ["src/{{root-ns-path}}/app/default_theme/theme.clj" (render "src/root_ns_path/app/default_theme/theme.clj" data)]

     ["src/{{root-ns-path}}/app/starter_theme/module_api.clj" (render "src/root_ns_path/app/starter_theme/module_api.clj" data)]
     ["src/{{root-ns-path}}/app/starter_theme/screen.clj" (render "src/root_ns_path/app/starter_theme/screen.clj" data)]
     ["src/{{root-ns-path}}/app/starter_theme/theme.clj" (render "src/root_ns_path/app/starter_theme/theme.clj" data)]

     ["src/{{root-ns-path}}/app/xilk2022_theme/module_api.clj" (render "src/root_ns_path/app/xilk2022_theme/module_api.clj" data)]
     ["src/{{root-ns-path}}/app/xilk2022_theme/colors.clj" (render "src/root_ns_path/app/xilk2022_theme/colors.clj" data)]
     ["src/{{root-ns-path}}/app/xilk2022_theme/css_reset.clj" (render "src/root_ns_path/app/xilk2022_theme/css_reset.clj" data)]
     ["src/{{root-ns-path}}/app/xilk2022_theme/footer_comp.clj" (render "src/root_ns_path/app/xilk2022_theme/footer_comp.clj" data)]
     ["src/{{root-ns-path}}/app/xilk2022_theme/header_comp.clj" (render "src/root_ns_path/app/xilk2022_theme/header_comp.clj" data)]
     ["src/{{root-ns-path}}/app/xilk2022_theme/main_nav_comp.clj" (render "src/root_ns_path/app/xilk2022_theme/main_nav_comp.clj" data)]
     ["src/{{root-ns-path}}/app/xilk2022_theme/screen.clj" (render "src/root_ns_path/app/xilk2022_theme/screen.clj" data)]
     ["src/{{root-ns-path}}/app/xilk2022_theme/theme.clj" (render "src/root_ns_path/app/xilk2022_theme/theme.clj" data)]

     ["src/{{root-ns-path}}/app/routes.clj" (render "src/root_ns_path/app/routes.clj" data)]
     ["src/{{root-ns-path}}/app/strings.clj" (render "src/root_ns_path/app/strings.clj" data)]
     ["src/{{root-ns-path}}/app/stylesheets.clj" (render "src/root_ns_path/app/stylesheets.clj" data)]

     ["src/{{root-ns-path}}/http_error/module_api.clj" (render "src/root_ns_path/http_error/module_api.clj" data)]
     ["src/{{root-ns-path}}/http_error/e_400_bad_request_screen.clj" (render "src/root_ns_path/http_error/e_400_bad_request_screen.clj" data)]
     ["src/{{root-ns-path}}/http_error/e_403_invalid_sec_token_screen.clj" (render "src/root_ns_path/http_error/e_403_invalid_sec_token_screen.clj" data)]
     ["src/{{root-ns-path}}/http_error/e_404_not_found_screen.clj" (render "src/root_ns_path/http_error/e_404_not_found_screen.clj" data)]
     ["src/{{root-ns-path}}/http_error/e_405_method_not_allowed_screen.clj" (render "src/root_ns_path/http_error/e_405_method_not_allowed_screen.clj" data)]
     ["src/{{root-ns-path}}/http_error/e_406_not_acceptable_screen.clj" (render "src/root_ns_path/http_error/e_406_not_acceptable_screen.clj" data)]
     ["src/{{root-ns-path}}/http_error/error_screen_template.clj" (render "src/root_ns_path/http_error/error_screen_template.clj" data)]

     ["src/{{root-ns-path}}/home/module_api.clj" (render "src/root_ns_path/home/module_api.clj" data)]
     ["src/{{root-ns-path}}/home/home_screen.clj" (render "src/root_ns_path/home/home_screen.clj" data)]

     ["test/xilk/xp/web_app/repl_test.clj" (render "test/xilk/xp/web_app/repl_test.clj" data)]
     ["test/xilk/xp/web_app/ui_test.clj" (render "test/xilk/xp/web_app/ui_test.clj" data)]

     ["test/xilk/xp/web_app/ui/internal/css_test.clj" (render "test/xilk/xp/web_app/ui/internal/css_test.clj" data)]

     ["test/xilk/xp/web_app/repl/internal/editor_test.clj" (render "test/xilk/xp/web_app/repl/internal/editor_test.clj" data)]

     ;; TODO: CHANGELOG
     ["README.md" (render "README.md" data)]
     [".gitignore" (render ".gitignore" data)]
     ["project.clj" (render "project.clj" data)])))
