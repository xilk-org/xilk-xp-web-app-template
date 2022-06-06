(ns xilk.xp.web-app.ui
  "Library for implementing the Xilk Web App UI design pattern."
  (:require
   [garden.core :as garden]
   [hiccup2.core :as h2]
   [reitit.core :as r]
   [ring.util.response :as rr]
   [tongue.core :as tongue]
   [xilk.xp.web-app.ui.internal.css :as css]
   [xilk.xp.web-app.ui.internal.props :as props]
   [xilk.xp.web-app.ui.internal.state :as state]))

;;;; Initialization

(defn init!
  "Initializes required state for this library. Must be called on app start.
  Works around circular dependencies.

  **Arguments**

  * `router`: A [[reitit.core/Router]] defining all routes in the app.

  * `all-base-lang-strs`: A map defining all base language string data in the
    app.

  * `app-dicts`: Dictionaries in [Tongue](https://github.com/tonsky/tongue)
    format, used for rendering all strings in all languages in the app UI.
    Usually aggregated from themes and modules and provided by the `strings` ns
    in the app module.

  **Examples**

  ```clj
  ;; Automatically initialize on app start (in app entry point ns).
  (init! strs/app-dicts)
  ```

  **See Also**

  [Tongue](https://github.com/tonsky/tongue)"
  [router all-base-lang-strs app-dicts]
  (alter-var-root #'state/router
                  (constantly router))
  (alter-var-root #'state/all-base-lang-strs
                  (constantly all-base-lang-strs))
  (alter-var-root #'state/app-dicts
                  (constantly app-dicts))
  (alter-var-root #'state/loc-str-fn
                  (constantly (tongue/build-translate app-dicts)))
  nil)

;;;; CSS Rendering

(defn css
  "Returns a `String` of CSS rendered from Clojure data structures (Garden).
  Side effects: may write to the file system if the `:output-to` option is set
  in `flags`. Supports qualified class selector keywords in Garden syntax,
  which allows for selectors to be easily scoped to a screen or component using
  auto-resolved keywords. Use as a drop-in replacement for `garden.core/css`.

  **Arguments**

  * `rules`: One, multiple, or a sequence of Garden data structures. Also
    supports qualified class selector keywords.

  * `flags`: An options map (optional, only as the first argument).

    - `:auto-prefix`: A set of properties to automatically prefix with
      `:vendors`. Default: `#{}`.

    - `:media-expressions`: A map specifying `@media` and `@supports` query
      configuration. Contains only one key.
      Default: `{:nesting-behavior :default}`.

      - `:nesting-behavior`: A keyword specifying how nested query expressions
      will be output. `:merge` for merging them with their parent. `:default`
      for leaving them nested. Default: `:default`.

    - `:output-to`: A `String` specifying the path to save a stylesheet after
      rendering. Default: `nil`.

    - `:preamble`: A sequence of `String`s specifying paths to files that
      contain text to prepend to the output. Default: `[]`.

    - `:pretty-print?` or `:pretty-print`: A `Boolean` specifying the output
      format. `true` for pretty-printed CSS, equivalent to setting
      `{:output-style => :expanded}` in Sass. `false` for compressed/minified
      CSS. Default: `true`.

    - `:vendors`: A sequence of `String`s specifying vendor prefixes to prepend
      to at-rules like `@keyframes`, properties within declarations containing
      the `^prefix` metadata, and properties defined in `:auto-prefix`.
      Default: `[]`.

  **Examples**

  ```clj
  ;; Render multiple rules.
  (css [::container {:border (px 1)}]
       [::button {:width (percent 100)}]])

  ;; Render a vector of rules.
  (css [[::container {:border (px 1)}]
        [::button {:width (percent 100)}]])

  ;; Render a vector of rules with a minified stylesheet written as a file.
  (css {:output-to \"resources/public/s/styles.css\"
        :pretty-print false}
       [[::container {:border (px 1)}]
        [::button {:width (percent 100)}]])
  ```

  **See Also**

  [[garden.core/css]]"
  {:arglists '([& rules] [flags & rules])
   :see-also ["garden.core/css"]}
  [& rules]
  (->> rules
       css/seq-of-rules
       css/transform-qualified-sel-kws-for-garden-css-compilation
       (apply garden/css)))

;;;; HTML Rendering

(defn html
  "Returns an object with HTML rendered from Clojure data structures (Hiccup).
  Supports qualified keywords as values of `:class` attributes in Hiccup syntax,
  which allows for auto-resolved class selector keywords (defined in the same
  screen or component's style rules) to be directly reused in Hiccup data. Use
  as a drop-in replacement for `hiccup2.core/html`.

  The returned object may be included in other Hiccup data and passed again to
  this function without being re-rendered. Use `clojure.core/str` to produce an
  HTML `String` for use in browsers.

  **Arguments**

  * `elements`: One or more Hiccup data structures. Also supports qualified
    class selector keywords as values of `:class` attributes.

  * `options`: An option map (optional first argument, literal or variable).

    - `:escape-strings?`: A `Boolean` specifying basic escaping (HTML
      sanitization) of `String`s contained in `elements`. `true` for replacing
      special characters (`&` `<` `>` `\"`) with their corresponding character
      entities (`&amp;` `&lt;` `&gt;` `&quot;`). To insert a `String` without
      its being escaped, use [[dangerous-unescapable-string]]. `false` for
      directly outputting the unescaped `String`s.  Default: `true`.

    - `:mode`: A keyword specifying how tags are rendered. Valid values are
      `:html`, `:xhtml`, `:xml` or `:sgml`. Default: `:html` (differs from the
      Hiccup default of `:xhtml`)

  **Examples**

  ```clj
  ;; Render a single element with a nested child.
  (html [:content
         [:h1 \"Heading\"]])

  ;; Render multiple elements with nested children.
  (html [:content
         [:h1 \"Heading\"]]
        [:footer
         [:p \"Copyright © 2022\"]])

  ;; Render an element with one class value.
  (html [:div {:class ::container}
         [:p \"Contained\"]])

  ;; Render an element with multiple class values.
  (html [:div {:class [::container :.text-center \"blue\"]}
         [:p \"Contained, entered, and blue\"]])

  ;; Render an element with a nested child containing an unescapable string.
  (html [:div
         [:p \"This will be escaped: <script>safe</script>\"]
         [:p \"This will NOT be escaped:\"
          [:code
           (dangerous-unescapable-string \"<script>DANGER</script>\")]]])

  ;; Render an element with all nested children strings unescaped.
  (html {:escape-strings? false}
        [:div
         [:p \"This will NOT be escaped: <script>DANGER</script>\"]
         [:p \"Neither will this:\" [:code \"<script>DANGER</script>\")]]])
  ```

  **See Also**

  [[dangerous-unescapable-string]]
  [[hiccup2.core/html]]"
  {:arglists '([& elements] [options & elements])
   :see-also ["dangerous-unescapable-string" "hiccup2.core/html"]}
  [options & hiccup]
  ;; TODO: optimize to allow hiccup to precompile where it can
  (let [xf-hiccup
        (if (sequential? hiccup)
          (-> hiccup
              css/transform-qualified-sel-kws-for-hiccup-html-compilation
              seq)
          hiccup)]
    (if (map? options)
      (let [mode (:mode options :html)
            escape-strings? (:escape-strings? options true)]
        ;; h2/html checks 1st arg type at compile time, must pass a map literal
        (h2/html {:mode mode :escape-strings? escape-strings?} xf-hiccup))
      (let [xf-options
            (if (sequential? options)
              (css/transform-qualified-sel-kws-for-hiccup-html-compilation
               options)
              options)]
        (h2/html {:mode :html, :escape-strings? true} xf-options xf-hiccup)))))

(def ^{:arglists '([& strings])
       :see-also ["html" "hiccup2.core/raw"]}
  dangerous-unescapable-string
  "Returns an object wrapping `strings` which will not be escaped by [[html]].
  Use as a drop-in replacement for `hiccup2.core/raw`. CAUTION: use only with
  trusted data, never with user input or data from third parties, as this can
  expose users to Cross-Site Scripting (XSS) attacks.

  **Arguments**

  * `strings`: One or more `String`s to be wrapped and prevented from escaping
    by [[html]].

  **Examples**

  ```clj
  ;; Wrap a string inside an `html` call.
  (html [:div
         [:p \"This will be escaped: <script>safe</script>\"]
         [:p \"This will NOT be escaped:\"
          [:code
           (dangerous-unescapable-string \"<script>DANGER</script>\")]]])
  ```

  **See Also**

  [[html]]
  [[hiccup2.core/raw]]"
  h2/raw)

;;;; Route Lookup

(defn route-names
  "Returns a collection of route names defined in the `router` set by [[init!]].
  Impure function (return value variation): depends on global state. Useful
  at the REPL when using [[route-path]].

  **See Also**

  [[route-path]]"
  {:see-also ["route-path"]}
  []
  (-> state/router r/route-names sort))

(defn route-path
  "Returns a path `String` defined in the `router` set by [[init!]].
  Impure function (return value variation): depends on global state.

  Returns `nil` if no match is found.

  **Arguments**

  * `route-name`: A name (often a keyword) specifying the route. Use
    [[route-names]] at the REPL to see available names.

  * `path-params` (optional): A map of parameters and values to be included in
    the path.

  **Examples**

  ```clj
  ;; Get a path.
  (route-path :about/team)

  ;; Get a path that uses parameters.
  (route-path :user/profile {:id 8})
  ```

  **See Also**
  [[route-names]]"
  {:see-also ["route-names"]}
  ([route-name]
   (route-path route-name nil))
  ([route-name path-params]
   (-> state/router
       (r/match-by-name route-name path-params)
       r/match->path)))

;;;; String Localization

(def ^{:arglists '([lang key] [lang key arg] [lang key & args])
       :see-also ["init!"]}
  loc-str
  "Returns a localized `String` defined in the `app-dicts` set by [[init!]].
  Impure function (return value variation): depends on global state. NOTE: use
  this function only for `String`s which are not automatically localized by
  app infrastructure.

  May return a `String` localized in different language if no match is found for
  `lang`. For example, the matching order for :zh-Hans-CN is as follows:
  1. zh-Hans-CN
  2. zh-Hans
  3. zh
  4. the fallback language (defined in the `app-dicts` set by [[init]])

  Returns a \"Missing key\" error string if no match is found at all.

  **Arguments**

  * `lang`: A keyword specifying the IETF language tag for localization.

  * `key`: A keyword specifying the entry in `app-dicts` to localize. Often an
    auto-resolved keyword, scoped to a screen or component.

  * `arg` (optional): Either a `String` for positional substitution, or a map
    for named substitution, in template strings or localization functions.

  * `args` (optional): `String`s for positional substitution in template strings
    or localization functions.

  **Examples**

  ```clj
  ;; Localize a simple string.
  (loc-str :en ::hello-world)

  ;; Localize using a string template that takes one positional argument.
  (loc-str :es ::welcome-username \"Alicia\")

  ;; Localize using a localization function that takes one positional argument.
  (loc-str :ja ::points-count \"4\")

  ;; Localize using a string template that takes two named arguments.
  (loc-str :ko ::player-remarked-quote {:name \"김연경\" :quote \"씩방\"})
  ```

  **See Also**

  [[init!]]
  [Tongue](https://github.com/tonsky/tongue)"
  #'state/loc-str-fn)

;;;; Props Construction

(defn create-props
  "Returns a map defining props for rendering views.
  Impure function (return value variation): depends on global state set by
  [[init!]].

  **Arguments**

  * `req`: A map defining a Ring request.

  * `overriding-props` (optional): A map defining props to include in the
    created props, overriding props with the same key, if any.

  **Returned Props**

  * `:app/lang`: A keyword specifying the IETF language tag for app
    localization. This is the language in `app-dicts` (set by [[init!]]) that
    most closely matches the preferred language for response.

  * `:req/lang`: A keyword specifying the IETF language tag of the preferred
    language for response. May be set to `nil`.

  * `:req/uri`: A `String` specifying the requested URI.

  * All simple `String`s defined in `all-base-lang-strs` (set by [[init!]]).
    Those designated as `:do-not-localize` are included directly; all others are
    localized. Keys should be qualified keywords, usually auto-resolved where
    they are defined. Example: `::my-app.accounts.sign-in-screen/cancel`.

  * All props in `overriding-props` (optional), which will override any props
    from other sources with the same key.

  **Examples**

  ```clj
  ;; Create props in a render function.
  (defn render
    [req content-fn screen-props]
    (-> req
        (create-props screen-props))
        (#(assoc % ::main-content (content-fn %)))
        html-doc))
  ```"
  ([req]
   (create-props req {}))
  ([req overriding-props]
   (-> req
       props/props-from-req
       ;; FIXME: generate all string props by lang at compile time
       props/assoc-str-props
       (merge overriding-props))))

;;;; Response Construction

(defn response-ok-css
  "Returns a Ring response with status 200, Content-Type `text-css`, and body.

  **Arguments**

  * `body`: a `String` specifying the CSS body of the response.

  **Examples**

  ```clj
  ;; Wrap a CSS string in an OK response.
  (response-ok-css \"body { background-color: black; }\")

  ;; Return an OK response from a stylesheet handler, with CSS in Garden syntax.
  (defn get-handler [_req]
    (-> [:body [:background-color :black]]
        css
        response-ok-css))"
  [body]
  (-> body
      rr/response
      (rr/content-type "text/css")))

(defn response-ok-html
  "Returns a Ring response with status 200, Content-Type `text-html`, and body.

  **Arguments**

  * `body`: a `String` specifying the HTML body of the response.

  **Examples**

  ```clj
  ;; Wrap an HTML string in an OK response.
  (response-ok-html \"<html><body><p>Welcome</p></body></html>\")

  ;; Return an OK response from a screen handler, with HTML in hiccup syntax.
  (defn get-handler [req]
    (-> req
        (theme/render [:p \"Welcome\"])
        response-ok-html))"
  [body]
  (-> body
      rr/response
      (rr/content-type "text/html")))
