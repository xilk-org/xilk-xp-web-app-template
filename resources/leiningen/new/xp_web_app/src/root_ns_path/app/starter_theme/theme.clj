(ns {{root-ns}}.app.starter-theme.theme
  "The API for screens to use this theme.
  NOTE: very early stage and unstable."
  (:require
   [{{root-ns}}.app.starter-theme.screen :as screen]
   [xilk.xp.web-app.ui :as x]))

;; FIXME: add standard color vars

;;;; Screen Rendering

(defn render
  "Returns a `String` containing a screen rendered in HTML.
  Use this when the response status is other than 200 OK. Otherwise, use
  [[screen-resp]].

  **Arguments**

  * `props`: A map containing initial props defined by the screen.

    - `:screen.html.head/added-els`: A sequence of vectors containing HTML
      elements in Hiccup syntax, to be added to the screen's `<head>` element.
      Example: `[[:meta {:name \"description\" :content \"Search page\"}]]`.

    - `:screen.html.head/title`: Either a keyword specifying the localized
      `String`, or the `String` literal, to be used in the document title for
      the screen. Often an auto-resolved keyword, scoped to the screen. If not
      specified, the theme applies its default title.

    - `:screen.main/to-container-edges?`: A boolean specifying whether the
      content returned by `content-fn` is rendered fully to the edges of its
      container, without being wrapped in padding, similar to a full-bleed in
      printing. `true` for rendering to the container edges. `false` for
      wrapping in padding. Default: `false`.

    - Other props, such as retrieved or computed data, which `content-fn` may
      require.

  * `content-fn`: A function that returns an object wrapping screen content
      HTML. Use [[xilk.xp.web-app.ui/html]] in the function to produce
      this object.

  **Examples**

  ```clj
  ;; Render in an error screen handler.
  (defn handler [req]
    (-> req
        (x/create-props {:screen.html.head/title ::not-found-title})
        (theme/render html)
        (x/response 404 \"text/html\")))
  ```

  **See Also**

  [[screen-resp]]
  [[xilk.xp.web-app.ui/html]]"
  [{:keys [screen.html.head/_added-els
           screen.html.head/title
           screen.main/_to-container-edges?] :as props}
   content-fn]
  (-> props
      (screen/add-title title)
      (screen/add-main-content content-fn)
      screen/html-doc))

(defn screen-resp
  "Returns a Ring response containing a screen rendered in HTML.

  **Arguments**

  * `props`: A map containing initial props defined by the screen.

    - `:screen.html.head/added-els`: A sequence of vectors containing HTML
      elements in Hiccup syntax, to be added to the screen's `<head>` element.
      Example: `[[:meta {:name \"description\" :content \"Search page\"}]]`.

    - `:screen.html.head/title`: Either a keyword specifying the localized
      `String`, or the `String` literal, to be used in the document title for
      the screen. Often an auto-resolved keyword, scoped to the screen. If not
      specified, the theme applies its default title.

    - `:screen.main/to-container-edges?`: A boolean specifying whether the
      content returned by `content-fn` is rendered fully to the edges of its
      container, without being wrapped in padding, similar to a full-bleed in
      printing. `true` for rendering to the container edges. `false` for
      wrapping in padding. Default: `false`.

    - Other props, such as retrieved or computed data, which `content-fn` may
      require.

  * `content-fn`: A function that returns an object wrapping screen content
      HTML. Use [[xilk.xp.web-app.ui/html]] in the function to produce
      this object.

  **Examples**

  ```clj
  ;; Render and return a response in a screen handler.
  (defn get-handler [req]
    (-> req
        (x/create-props {:screen.html.head/title ::screen-title})
        ;; Query databases, call API's, and add any props here.
        (theme/screen-resp html)))
  ```

  **See Also**

  [[xilk.xp.web-app.ui/html]]"
  [{:keys [screen.html.head/_added-els
           screen.html.head/_title
           screen.main/_to-container-edges?] :as props}
   content-fn]
  (-> props
      (render content-fn)
      x/response-ok-html))
