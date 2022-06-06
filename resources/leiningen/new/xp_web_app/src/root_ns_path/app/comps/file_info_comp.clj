(ns {{root-ns}}.app.comps.file-info-comp
  "File info component."
  (:require
   [xilk.xp.web-app.ui :as x]))

(defn file-info
  #_{:clj-kondo/ignore [:shadowed-var]}
  [{::keys [class str-kw path] :as props}]
  (x/html
    [:p {:class class}
     (x/dangerous-unescapable-string (x/loc-str (:app/lang props)
                                                str-kw
                                                (x/html [:code path])))]))

(def css
  nil)

(def strings
  nil)
