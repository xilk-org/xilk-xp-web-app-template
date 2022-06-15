# Xilk Experimental Web App Template

A [Leiningen](https://github.com/technomancy/leiningen) template for R&D on
Clojure web app development patterns and tools that deliver high user-value
features, like
[progressive enhancement](https://en.wikipedia.org/wiki/Progressive_enhancement)
and
[internationalization](https://en.wikipedia.org/wiki/Internationalization_and_localization),
while also reducing friction for developers:

* **Fewer decisions**:
  Skip distractions and start programming right away.
* **Zero lock-in**:
  *Opt-in* to provided patterns (AKA **Not a Framework**).
* **Zero [SPA](https://en.wikipedia.org/wiki/Single_page_application) pains**:
  Avoid FE/BE state and code, complex auth flows, etc.
* **Minimum ramp-up**:
  Be productive with the basics
  ([Ring](https://github.com/ring-clojure/ring),
  [Hiccup](https://github.com/weavejester/hiccup),
  [Garden](https://github.com/noprompt/garden/)).
* **Less cognitive load**:
  Stay focused with code colocation, detailed docstrings, explicit dependencies,
  and a REPL-centered workflow.

Less friction is smooth, and smooth is fast.

## Status: EXPERIMENTAL, EARLY STAGE

For production use, this template is immediately ready for small, fixed-scope,
low-JavaScript, low-traffic projects. Library source code is included directly
in new projects to both isolate them from API instability during R&D, and allow
easy changes to accommodate more demanding needs.

So what's experimental about it? Before improving library code quality and test
coverage, addressing gaps in current features, and starting work on new
features, there are two assumptions that must first be tested:

* Developers find these patterns and tools valuable,
  [contributing](CONTRIBUTING.md) to and starting to form a community
  around the project.
* The current feature API's accommodate developers' needs well, without
  significant rework.

## Features

### Screen and Composable Component Pattern

Colocate HTML, CSS, strings, and controller code in single-file screens and
components (inspired by
[Vue's Single File Components](https://vuejs.org/guide/scaling-up/sfc.html)).
Locally scope CSS rule and string names using auto-resolved keywords. Apply
previous web development experience using common idioms like `props` and nested
components.

```clj
;; Example screen
(ns my-project.example.hello-world-screen
  (:require
   [my-project.app.default-theme.theme :as theme]
   [my-project.example.sign-in-form-comp :refer [sign-in-form]]
   [xilk.xp.web-app.ui :as x])))

(defn html [props]
  (x/html
   [:h1 (::hello-world props)]  ;; String
   [:p {:class ::sub-text}      ;; CSS
       (::sign-in props)]       ;; String
   (sign-in-form props)))       ;; Component

(def css
  (x/css
   [::sub-text {:font-weight :bold}]))

(def strings
  {::hello-title "Hello"
   ::hello-world "Hello World!"
   ::sign-in     "Sign in to see more..."})

(def get-handler [req]
  (-> req
      (x/create-props {:screen.html.head.title/str-kw ::hello-title})
      ;; Query databases, call API's, and add any props here.
      ;; Then let theme/screen-resp handle the routine stuff:
      ;; 1. Extract props from the request.
      ;; 2. Merge screen props, request props, and localized strings.
      ;; 3. Call the content function with the merged props.
      ;; 4. Lay out the content in an HTML document with header, footer, etc.
      ;; 5. Wrap the HTML document in a Ring response.
      (theme/screen-resp html)))
```
```clj
;; Example component
(ns my-project.example.sign-in-form-comp
  (:require
   [my-project.example.password-input-comp :refer [password-input]]
   [my-project.example.submit-button-comp :refer [submit-button]]
   [my-project.example.username-input-comp :refer [username-input]]
   [xilk.xp.web-app.ui :as x])))

(defn sign-in-form [props]
  (x/html
   [:form {:method :post}
    [:h2 (::sign-in props)    ;; String
    (username-input props)    ;; Component
    (password-input props)    ;; Component
    (submit-button props)]))  ;; Component

(def css
  nil)  ;; No additional CSS required for this component.

(def strings
  {::sign-in "Sign In"})  ;; No collision with hello-world-screen string
```

### Module Pattern

Colocate related screens, components, and other code in module directories.
Generate and update infrastructure code — idiomatic and with dependencies
explicitly defined (no magic) — automatically when
[scaffolding at the REPL](#repl-tooling-scaffolding).

```clj
;; Example module-api, generated and updated for each module
(ns my-project.example.module-api
  (:require
   [my-project.example.hello-world-screen :as hello-world-screen]
   [my-project.example.password-input-comp :as password-input-comp]
   [my-project.example.sign-in-form-comp :as sign-in-form-comp]
   [my-project.example.submit-button-comp :as submit-button-comp]
   [my-project.example.username-input-comp :as username-input-comp]))

;; Data is processed and used by app.routes, app.strings, and app.stylesheets

(def routes
  [["/" {:name :example/hello-world
         :get  hello-world-screen/get-handler}]])

(def css
  (str hello-world-screen/css
       password-input-comp/css
       sign-in-form-comp/css
       submit-button-comp/css
       username-input-comp/css))

(def strings
  (merge hello-world-screen/strings
         password-input-comp/strings
         sign-in-form-comp/strings
         submit-button-comp/strings
         username-input-comp/strings))
  ```

### Theme Pattern

Wrap screen content in layouts containing dynamically rendered sections like
headers and footers, applying global CSS including light/dark color schemes.
Share themes across projects or with other developers. **NOTE: emerging feature,
half-baked, very unstable API's.**

### REPL Tooling: Scaffolding

Add scaffolding for new modules, screens, and components directly from your
REPL-connected editor. Keep scaffolding history in source code for reference.
Use the extensible, data-driven design to compose entire features, sharing
across projects or with other developers.

```clj
;; dev/user.clj
(-> (module-def {:kw :example})
    (screen-def {:kw         :hello-world
                 :route-path "/"})
    (comp-def   {:kw :sign-in-form})
    (comp-def   {:kw :username-input})
    (comp-def   {:kw :password-input})
    (comp-def   {:kw :submit-button})
    add!)
```

## Future Work Under Consideration

* Production and test code improvement.
* Test coverage improvement.
* Performance optimization.
* Guide and reference documentation.
* REPL tooling to edit and remove modules, screens, and components.
* Hot-reload when starting the app at the REPL. Currently, hot-reload is
  available via [Lein-Ring](https://github.com/weavejester/lein-ring), but this
  is admittedly not smooth.
* Pattern for system-agnostic persistence and third-party API integration.
* JavaScript colocation. Currently, JavaScript is written either in a separate
  static file and included in a screen or component via `<script src=...>` like
  it's 1996, or inline as a string in HTML like nothing matters anymore.
* ClojureScript compilation and colocation.
* Live UI updates via [AJAX](https://en.wikipedia.org/wiki/Ajax_(programming)),
  [WebSocket](https://en.wikipedia.org/wiki/Web_socket), or maybe both.
* [PWA](https://en.wikipedia.org/wiki/Progressive_web_application)
  infrastructure.

## Getting Started

1. [Install Leiningen](https://leiningen.org/).

2. Clone the template.
   ```sh
   git clone https://github.com/xilk-org/xilk-xp-web-app-template.git
   ```

3. Create a new project and change directory.
   ```sh
   cd web-app-template
   lein new org.xilk/xp-web-app my-project-name --to-dir ../my-project-dir
   cd ../my-project-dir
   ```

4. Start a REPL (optional if you use a "jack-in" feature from your editor).
   ```sh
   lein repl
   ```
   NOTE: if your Clojure tooling requires starting the REPL with a wrapper, then
   use the appropriate command. For example, using
   [vim-iced](https://github.com/liquidz/vim-iced):
   ```sh
   iced repl
   ```

5. Open `dev/user.clj` in your editor, and use your Clojure tooling to connect
   to the REPL and require/reload all. See the comment block under `"Reloaded"
   Workflow` for forms to start and stop the app at the REPL.

6. (Optional) Run the app separately from the REPL, starting a local web server
   and launching a browser with hot-reload.
   ```sh
   lein ring server 3000
   ```
   Hot-reload is very useful when programming and styling UI. Future work
   includes implementing hot-reload when starting the app at the REPL, for a
   smoother development experience.

7. See [`README.md`](resources/leiningen/new/xp_web_app/README.md) at the root
   of the new project for more detailed information on project dependencies,
   structure, and build tasks.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

Copyright © 2022 stevejmp

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
