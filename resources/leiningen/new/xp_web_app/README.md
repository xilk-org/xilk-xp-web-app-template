# {{name}}

A web application (multi-page) that FIXME

## Overview

### Dependencies

| Role                   | Library |
| ---------------------- | ------- |
| Programming Language   | [Clojure](https://clojure.org/) |
| System Management      | [donut.system](https://github.com/donut-power/system) |
| Web Server             | [Ring](https://github.com/ring-clojure/ring) |
| Router                 | [reitit](https://github.com/metosin/reitit/) |
| HTML Rendering         | [Hiccup](https://github.com/weavejester/hiccup) |
| CSS Rendering          | [Garden](https://github.com/noprompt/garden) |
| String Localization    | [Tongue](https://github.com/tonsky/tongue) |
| **(Development Only)** |
| Hot-Reload Web Server  | [Lein-Ring](https://github.com/weavejester/lein-ring) |
| Boilerplate Automation | [rewrite-clj](https://github.com/clj-commons/rewrite-clj) |

### Structure

| Path | Description |
| ---- | ----------- |
| [`.clj-kondo/`](.clj-kondo/) | Lint config and cache files (cache files are not tracked; see [`.gitignore`](.gitignore)) |
| [`dev/`](dev/) | Development source files (compiled only with the `dev` profile) |
| [`dev/xilk/xp/web_app/repl.clj`](dev/xilk/xp/web_app/repl.clj) | Xilk Experimental Web App REPL tooling library |
| [`dev/user.clj`](dev/user.clj) | Init, symbols, and forms for development at the [REPL](#starting-and-connecting-to-the-repl) |
| [`doc/`](doc/) | Developer documentation |
| [`resources/public/`](resources/public/) | Static asset files; url `/s/` routes here |
| [`src/`](src/) | Application source files |
| [`src/{{root-ns-path}}/`](src/{{root-ns-path}}/) | Top-level namespaces and modules |
| [`src/{{root-ns-path}}/app/`](src/{{root-ns-path}}/app/) | The `app` module (shared components, themes, infrastructure) |
| [`src/{{root-ns-path}}/app/comps/`](src/{{root-ns-path}}/app/comps/) | Shared components |
| [`src/{{root-ns-path}}/app/default_theme/theme.clj`](src/{{root-ns-path}}/app/default_theme/theme.clj) | Public API for the default theme (delegates to another specified theme) |
| [`src/{{root-ns-path}}/app/xilk2022_theme/`](src/{{root-ns-path}}/app/xilk2022_theme/) | Xilk2022 theme module (includes light and dark color schemes) |
| [`src/{{root-ns-path}}/app/xilk2022_theme/theme.clj`](src/{{root-ns-path}}/app/xilk2022_theme/theme.clj) | Public API for the Xilk2022 theme |
| [`src/{{root-ns-path}}/app/routes.clj`](src/{{root-ns-path}}/app/routes.clj) | Merged module routes, app router, route handler |
| [`src/{{root-ns-path}}/app/strings.clj`](src/{{root-ns-path}}/app/strings.clj) | Merged module and theme strings, localizations |
| [`src/{{root-ns-path}}/app/stylesheets.clj`](src/{{root-ns-path}}/app/stylesheets.clj) | Merged module and theme css, handlers to serve stylesheets |
| [`src/{{root-ns-path}}/home/`](src/{{root-ns-path}}/home/) | Module containing the home screen |
| [`src/{{root-ns-path}}/home/module_api.clj`](src/{{root-ns-path}}/home/module_api.clj) | Aggregated routes, css, and strings for all screens and components in the home module |
| [`src/{{root-ns-path}}/http_error/`](src/{{root-ns-path}}/http_error/) | Module containing HTTP error screens |
| [`src/{{root-ns-path}}/app.clj`](src/{{root-ns-path}}/app.clj) | Application initialization, configuration, and entry point |
| [`src/xilk/xp/web_app/ui.clj`](src/xilk/xp/web_app/ui.clj) | Xilk Experimental Web App UI library |
| [`test/`](test/) | Test source files; only namespaces ending in `-test` (files `*_test.clj`) are compiled and sent to the test runner |

## Getting Started

### Editor/IDE

Use your preferred editor or IDE that supports Clojure/ClojureScript
development. See [Clojure tools](https://clojure.org/community/tools) for some
popular options.

### Environment

1. Install [JDK 8 or later](https://openjdk.java.net/install/) (Java Development Kit)
2. Install [Leiningen](https://leiningen.org/#install) (Clojure project task & dependency management)
3. (Optional) Install [clj-kondo](https://github.com/borkdude/clj-kondo/blob/master/doc/install.md) (linter)
4. Clone this repo and open a terminal in the `{{name}}` project root directory
5. Download project dependencies:
    ```sh
    lein deps
    ```
6. (Optional) Setup [lint cache](https://github.com/borkdude/clj-kondo#project-setup):
    ```sh
    clj-kondo --lint "$(lein classpath)" --dependencies --parallel --copy-configs
    ```
7. (Optional) Setup
[linting in your editor](https://github.com/borkdude/clj-kondo/blob/master/doc/editor-integration.md)

### Development

1. Open a terminal and change to the project directory.
2. Start a REPL (optional if you use a "jack-in" feature from your editor).
   ```sh
   lein repl
   ```
   NOTE: if your Clojure tooling requires starting the REPL with a wrapper, then
   use the appropriate command. For example, using
   [vim-iced](https://github.com/liquidz/vim-iced):
   ```sh
   iced repl
   ```
3. Open `dev/user.clj` in your editor, and use your Clojure tooling to connect
   to the REPL and require/reload all. See the `comment` block under `"Reloaded"
   Workflow` for forms to start and stop the app at the REPL.

## Development Build Tasks

### Starting and Connecting to the REPL

Build the app with the `dev` profile and start a REPL:
```sh
lein repl
```

Once the REPL starts, you may connect to it from your editor. Note that some
tools require the REPL to be started with a wrapper. For example, `vim-iced`
requires the REPL to be started via the following command:
```sh
iced repl
```

### Running the App with Hot Reload (separate from the REPL)

Hot-reload is very useful when programming and styling UI. Future work includes
implementing hot-reload when starting the app at the REPL, for a smoother
development experience.

Build the app with the `dev` profile, start a temporary local web server, and
serve the app with hot reload:
```sh
lein ring server 3000
```

This will automatically open and navigate your default browser to the
application main page.

### Running Tests

Build the app with the `dev` profile and run tests:
```sh
lein test
```

## Production Build Tasks

Build the app with the `prod` profile:

```sh
lein uberjar
```

The `target/uberjar/` directory is created, containing the compiled JAR and
standalone JAR (uberjar) archive files.

## Copyright Notice

Copyright © {{year}} FIXME
