(ns {{root-ns}}.app.default-theme.theme
  "The default theme API for screens to use. Delegates to another theme.
  NOTE: very early stage and unstable."
  (:require
   [{{root-ns}}.app.xilk2022-theme.theme :as delegated-theme]))

;;;; Style Data

(def min-touch-target-dim
  delegated-theme/min-touch-target-dim)

(def base-colors
  delegated-theme/base-colors)

(def dk-bg-colors
  delegated-theme/dk-bg-colors)

(def lt-bg-colors
  delegated-theme/lt-bg-colors)

;;;; Screen Rendering

(defn render
  "Returns a `String` containing a screen rendered in HTML.
  Use this when the response status is other than 200 OK. Otherwise, use
  [[screen-resp]].

  **Arguments**

  * `params`: A map containing data for rendering.

    - `:req`: A map containing a Ring request.

    - `:content-fn`: A function that returns an object wrapping screen content
      HTML. Use [[xilk.xp.web-app.ui/html]] in the function to produce
      this object.

    - `:props` (optional): A map containing initial props defined by the screen.

      - `:screen.html.head/added-els`: A sequence of vectors containing HTML
        elements in Hiccup syntax, to be added to the screen's `<head>` element.
        Example: `[[:meta {:name \"description\" :content \"Search page\"}]]`.

      - `:screen.html.head.title/str-kw`: A keyword specifying the `String` to
        be used in the document title for the screen. Often an auto-resolved
        keyword, scoped to the screen. If not specified, the theme applies its
        default title.

      - `:screen.main/to-container-edges?`: A boolean specifying whether the
        content returned by `content-fn` is rendered fully to the edges of its
        container, without being wrapped in padding, similar to a full-bleed in
        printing. `true` for rendering to the container edges. `false` for
        wrapping in padding. Default: `false`.

      - Other props, such as retrieved or computed data, which `content-fn` may
        require.

  **Examples**

  ```clj
  ;; Render in an error screen handler.
  (defn handler [req]
    {:status 404
     :headers {\"Content-Type\" \"text/html\"}
     :body (render {:content-fn content
                    :props      {:screen.html.head.title/str-kw
                                 ::not-found-title}
                    :req        req})})
  ```

  **See Also**

  [[screen-resp]]
  [[xilk.xp.web-app.ui/html]]"
  [{:keys [_req _content-fn _props]
    {:keys [screen.html.head/_added-els
            screen.html.head.title/_str-kw
            screen.main/_to-container-edges?]} :props
     :as params}]
  (delegated-theme/render params))

(defn screen-resp
  "Returns a Ring response containing a screen rendered in HTML.

  **Arguments**

  * `params`: A map containing data for rendering.

    - `:req`: A map containing a Ring request.

    - `:content-fn`: A function that returns an object wrapping screen content
      HTML. Use [[xilk.xp.web-app.ui/html]] in the function to produce
      this object.

    - `:props` (optional): A map containing initial props defined by the screen.

      - `:screen.html.head/added-els`: A sequence of vectors containing HTML
        elements in Hiccup syntax, to be added to the screen's `<head>` element.
        Example: `[[:meta {:name \"description\" :content \"Search page\"}]]`.

      - `:screen.html.head.title/str-kw`: A keyword specifying the `String` to
        be used in the document title for the screen. Often an auto-resolved
        keyword, scoped to the screen. If not specified, the theme applies its
        default title.

      - `:screen.main/to-container-edges?`: A boolean specifying whether the
        content returned by `content-fn` is rendered fully to the edges of its
        container, without being wrapped in padding, similar to a full-bleed in
        printing. `true` for rendering to the container edges. `false` for
        wrapping in padding. Default: `false`.

      - Other props, such as retrieved or computed data, which `content-fn` may
        require.

  **Examples**

  ```clj
  ;; Render and return a response in a screen handler.
  (defn get-handler [req]
    (-> {:content-fn content
         :props {:screen.html.head.title/str-kw ::screen-title}
         :req req}
        ;; Query databases, call API's, and add any props here.
        theme/screen-resp))
  ```

  **See Also**

  [[xilk.xp.web-app.ui/html]]"
  [{:keys [_req _content-fn _props]
    {:keys [screen.html.head/_added-els
            screen.html.head.title/_str-kw
            screen.main/_to-container-edges?]} :props
     :as params}]
  (delegated-theme/screen-resp params))
