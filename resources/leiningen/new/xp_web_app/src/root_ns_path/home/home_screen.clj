(ns {{root-ns}}.home.home-screen
  "Home screen view and controller."
  (:require
   [{{root-ns}}.app.comps.file-info-comp :refer [file-info] :as fi]
   [{{root-ns}}.app.default-theme.theme :as theme]
   [{{root-ns}}.app.xilk2022-theme.colors :as colors]
   [garden.color :refer [darken hsl->hex lighten]]
   [garden.stylesheet :refer [at-media]]
   [garden.units :refer [em]]
   [xilk.xp.web-app.ui :as x]))

;;;; View

(defn html [props]
  (x/html
   [:div {:class ::container}

    [:div {:class ::hero}

     [:div {:class ::headline}

      [:h1 {:class ::headline__main}
       (::headline.main.smooth-is-fast props)]

      [:p {:class ::headline__sub}
       (::headline.sub.welcome-to-silk props)]]

     [:div {:class ::status}
      (::status-experimental props)]]

    (file-info (assoc props ::fi/class ::file-info
                            ::fi/str-kw ::edit-this-screen-at
                            ::fi/path (str "src/" (-> #'html meta :file))))

    (file-info (assoc props ::fi/class ::file-info
                            ::fi/str-kw ::edit-theme-colors-at
                            ::fi/path (str "src/"
                                           (-> #'colors/base meta :file))))

    (file-info (assoc props ::fi/class ::file-info
                            ::fi/str-kw ::make-new-at
                            ::fi/path (str "src/dev/user.clj")))

    (file-info (assoc props ::fi/class [::file-info ::last]
                            ::fi/str-kw ::more-info-at
                            ::fi/path (str "README.md")))]))

(def css
  (x/css
   [(at-media {:prefers-color-scheme :light}
     [::status {:color (:accent/fg theme/lt-bg-colors)
                :background-color (:accent/bg theme/lt-bg-colors)}]

     [::container
      {:background-image
       (str "radial-gradient(circle at 0 0, "
            (-> theme/base-colors :primary (lighten 35) hsl->hex)
            ", transparent)"
            ", "
            "radial-gradient(circle at 100% 0, "
            (-> theme/base-colors :secondary (lighten 35) hsl->hex)
            ", transparent)"
            ", "
            "radial-gradient(circle at 50% 100%, "
            (-> theme/base-colors :tertiary (lighten 35) hsl->hex)
            ", transparent)")}])

    (at-media {:prefers-color-scheme :dark}
     [::status {:color (:accent/fg theme/dk-bg-colors)
                :background-color (:accent/bg theme/dk-bg-colors)}]

     [::container
      {:background-image
       (str "radial-gradient(circle at 0 0, "
            (-> theme/base-colors :primary (darken 20) hsl->hex)
            ", transparent)"
            ", "
            "radial-gradient(circle at 100% 0, "
            (-> theme/base-colors :secondary (darken 20) hsl->hex)
            ", transparent)"
            ", "
            "radial-gradient(circle at 50% 100%, "
            (-> theme/base-colors :tertiary (darken 20) hsl->hex)
            ", transparent)")}])

    [::container
     {:display :grid
      :grid-template-columns [["minmax(1rem, 1fr)"
                               "minmax(0, 60rem)"
                               "minmax(1rem, 1fr)"]]}]

    [::hero {:grid-column 2
             :display :grid
             :align-items :center
             :grid-template-columns "repeat(auto-fit, minmax(12.5em, 1fr))"
             :padding (em 1)}]

    [::headline {:padding (em 1)
                 :text-align :center}]

    [::headline__main {:font-size (em 2.4)
                       :font-weight 700
                       :line-height (em 1.2)}]

    [::headline__sub {:font-size (em 1.2)
                      :font-weight 600}]

    [::status {:max-width (em 25)
               :padding (em 1)
               :text-align :center
               :font-size (em 1.25)
               :font-weight 600}]

    [::file-info {:grid-column 2}]

    [::last {:padding-bottom (em 2)}]]))

(def strings
  {::home-title "Home"
   ::headline.main.smooth-is-fast "Smooth is fast."
   ::headline.sub.welcome-to-silk "Welcome to your Xilk Web App."
   ::status-experimental "Status: EXPERIMENTAL"
   ::edit-this-screen-at "Edit this screen at {1}"
   ::edit-theme-colors-at "Edit theme colors at {1}"
   ::make-new-at "Make new modules, screens, and components at {1}"
   ::more-info-at "More info at {1}"})

;;;; Controller

(defn get-handler [req]
  (-> {:content-fn html
       :props {:screen.html.head.title/str-kw   ::home-title
               :screen.html.head/added-els      [[:meta {:name "description"
                                                         :content "FIXME"}]]
               :screen.main/to-container-edges? true}
       :req req}
      theme/screen-resp))
