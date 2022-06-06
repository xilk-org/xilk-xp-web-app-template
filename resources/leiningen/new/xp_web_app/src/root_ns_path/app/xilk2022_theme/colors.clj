(ns {{root-ns}}.app.xilk2022-theme.colors
  (:require
   [garden.color :refer [darken hsla invert lighten transparentize
                         triad] :as gc]))

;;;; Colors

(def gray (hsla 0 0 50 1))
(def red (hsla 0 90 50 1))
(def yellow (hsla 60 90 35 1))
(def green (hsla 120 90 40 1))
(def cyan (hsla 180 90 40 1))
(def blue (hsla 240 90 50 1))
(def magenta (hsla 300 90 50 1))

(def primary
  "The primary color of the theme's color palette.

  For easy experimentation, this color is used to compute the secondary,
  tertiary, and accent colors in the theme `base` colors below. Change this
  color while the app is running to see its impact on the theme."
  cyan)

(def base
  {:primary primary
   :secondary (-> primary triad second)
   :tertiary (-> primary triad last)
   :accent (-> primary invert (darken 20))
   :neutral gray
   :info nil
   :success nil
   :warning nil
   :danger nil})

(def lt-bg
  {:body/bg (-> base :primary (lighten 100))
   :body/text (-> base :neutral (darken 40))
   :body.link/active (-> base :accent)
   :body.link/any-link (-> base :primary (darken 10))
   :body.link/hover (-> base :primary (darken 25))

   :header/bg (-> base :primary (darken 25))
   :header/fg (-> base :neutral (lighten 45))
   :header.nav-item.here/fg (-> base :neutral (lighten 100))
   :header.nav-item.link/active (-> base :accent)
   :header.nav-item.link/any-link (-> base :primary (lighten 30))
   :header.nav-item.link/hover (-> base :primary (lighten 50))

   :footer/bg (-> base :neutral (darken 30))
   :footer/fg (-> base :neutral (lighten 45))
   :footer.link/active (-> base :accent)
   :footer.link/any-link (-> base :neutral (lighten 30))
   :footer.link/hover (-> base :neutral (lighten 50))

   :neutral/border nil
   :neutral/bg (-> base :neutral (darken 100) (transparentize 0.9))
   :neutral/fg (-> base :neutral (darken 40))

   :accent/bg (-> base :accent)
   :accent/fg (-> base :neutral (lighten 100))})

(def dk-bg
  {:body/bg (-> base :primary (darken 45))
   :body/text (-> base :neutral (lighten 40))
   :body.link/active (-> base :accent)
   :body.link/any-link (-> base :primary (lighten 30))
   :body.link/hover (-> base :primary (lighten 50))

   :header/bg (-> base :primary (darken 35))
   :header/fg (-> base :neutral (lighten 45))
   :header.nav-item.here/fg (-> base :neutral (lighten 100))
   :header.nav-item.link/active (-> base :accent)
   :header.nav-item.link/any-link (-> base :primary (lighten 30))
   :header.nav-item.link/hover (-> base :primary (lighten 50))

   :footer/bg (-> base :neutral (darken 40))
   :footer/fg (-> base :neutral (lighten 45))
   :footer.link/active (-> base :accent)
   :footer.link/any-link (-> base :neutral (lighten 30))
   :footer.link/hover (-> base :neutral (lighten 50))

   :neutral/border nil
   :neutral/bg (-> base :neutral (lighten 100) (transparentize 0.9))
   :neutral/fg (-> base :neutral (lighten 40))

   :accent/bg (-> base :accent)
   :accent/fg (-> base :neutral (lighten 100))})

(comment
  "Reference color keywords for alerts."
  {:info/border nil
   :info/bg nil
   :info/fg nil

   :success/border nil
   :success/bg nil
   :success/fg nil

   :warning/border nil
   :warning/bg nil
   :warning/fg nil

   :danger/border nil
   :danger/bg nil
   :danger/fg nil})
