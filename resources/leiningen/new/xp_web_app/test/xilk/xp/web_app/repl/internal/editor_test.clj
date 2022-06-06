(ns xilk.xp.web-app.repl.internal.editor-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [rewrite-clj.zip :as z]
   [xilk.xp.web-app.repl.internal.editor :as sut]))

;;;; editor/add-to-routes

(deftest add-to-routes-test

  ;;; With Valid Arg Data

  (testing (str "With a zloc defining app-route-data with one entry, returns "
                "the expected routes ns source code")
    (is (= (str "(ns valid\n"
                "  (:require a\n"
                "            [valid-l]))\n\n"
                "(def app-route-data\n"
                "  (concat a/routes\n"
                "          valid-r))\n")
           (sut/add-to-routes {:module.api/libspec ['valid-l]
                               :module.api/routes-sym 'valid-r}
                              (z/of-string (str "(ns valid\n"
                                                "  (:require a))\n\n"
                                                "(def app-route-data\n"
                                                "  (concat a/routes))\n")
                                           {:track-position? true})))))

  (testing (str "With a zloc defining app-route-data with both a docstring and "
                "one entry, returns the expected routes ns source code")
    (is (= (str "(ns valid\n"
                "  \" app-route-data below.\"\n"
                "  (:require a\n"
                "            [valid-l]))\n\n"
                "(def app-route-data\n"
                "  \" docstring. \"\n"
                "  (concat a/routes\n"
                "          valid-r))\n")
           (sut/add-to-routes {:module.api/libspec ['valid-l]
                               :module.api/routes-sym 'valid-r}
                              (z/of-string (str "(ns valid\n"
                                                "  \" app-route-data below.\"\n"
                                                "  (:require a))\n\n"
                                                "(def app-route-data\n"
                                                "  \" docstring. \"\n"
                                                "  (concat a/routes))\n")
                                           {:track-position? true})))))

  (testing (str "With a zloc defining app-route-data entries on their own "
                "lines, returns the expected routes ns source code string with "
                "proper indentation")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   a\n"
                "   valid-l))\n\n"
                "(def app-route-data\n"
                "  (concat\n"
                "   a/routes\n"
                "   valid-r))\n")
          (sut/add-to-routes {:module.api/libspec 'valid-l
                              :module.api/routes-sym 'valid-r}
                             (z/of-string (str "(ns valid\n"
                                               "  (:require\n"
                                               "   a))\n\n"
                                               "(def app-route-data\n"
                                               "  (concat\n"
                                               "   a/routes))\n")
                                          {:track-position? true})))))

  ;;; With Invalid Arg Data

  (testing "With all empty or nil args, throws an error"
    (is (thrown? AssertionError (sut/add-to-routes nil)))
    (is (thrown? AssertionError (sut/add-to-routes {})))
    (is (thrown? AssertionError (sut/add-to-routes nil nil)))
    (is (thrown? AssertionError (sut/add-to-routes {} nil)))

   (testing "With a nil libspec in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec nil
                              :module.api/routes-sym 'valid-r}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def app-route-data\n"
                                               "  (concat a/routes))\n")
                                          {:track-position? true})))))

   (testing "With an invalid libspec in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec :INVALID-L
                              :module.api/routes-sym 'valid-r}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def app-route-data\n"
                                               "  (concat a/routes))\n")
                                          {:track-position? true})))))

   (testing "With a nil routes symbol in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec 'valid-l
                              :module.api/routes-sym nil}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def app-route-data\n"
                                               "  (concat a/routes))\n")
                                          {:track-position? true})))))

   (testing "With an invalid routes symbol in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec 'valid-l
                              :module.api/routes-sym :INVALID-R}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def app-route-data\n"
                                               "  (concat a/routes))\n")
                                          {:track-position? true})))))

   (testing (str "With a missing app-route-data def in the routes source code, "
                 "throws an error")
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec 'valid-l
                              :module.api/routes-sym 'valid-r}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def INVALID-VAR\n"
                                               "  (concat a/routes))\n")
                                          {:track-position? true})))))

   (testing "With a nil zloc, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec 'valid-l
                              :module.api/routes-sym 'valid-r}
                             nil)))))

  (testing "With a missing :track-position? option in the zloc, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec 'valid-l
                              :module.api/routes-sym 'valid-r}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def app-route-data\n"
                                               "  (concat a/routes))\n")
                                          {:INVALID "no :track-position?"}))))

   (testing (str "With a :track-position? of false in the zloc options, throws "
                 "an error")
    (is (thrown? AssertionError
          (sut/add-to-routes {:module.api/libspec 'valid-l
                              :module.api/routes-sym 'valid-r}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def app-route-data\n"
                                               "  (concat a/routes))\n")
                                          {:track-position? false})))))))

;;;; editor/add-to-strings

(deftest add-to-strings-test

  ;;; With Valid Arg Data

  (testing (str "With a zloc defining all-base-lang-strs with one entry, "
                "returns the expected strings ns source code")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   a\n"
                "   [valid-l]))\n\n"
                "(def all-base-lang-strs\n"
                "  (merge a/strings\n"
                "         valid-str))\n")
           (sut/add-to-strings
            {:module.api/libspec ['valid-l]
             :module.api/strings-sym 'valid-str}
            (z/of-string (str "(ns valid\n"
                              "  (:require\n"
                              "   a))\n\n"
                              "(def all-base-lang-strs\n"
                              "  (merge a/strings))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining all-base-lang-strs with both a docstring "
                "and one entry, returns the expected strings ns source code")
    (is (= (str "(ns valid\n"
                "  \" all-base-lang-strs below.\"\n"
                "  (:require a\n"
                "            [valid-l]))\n\n"
                "(def all-base-lang-strs\n"
                "  \"docstring.\"\n"
                "  (merge a/strings\n"
                "         valid-str))\n")
           (sut/add-to-strings
            {:module.api/libspec ['valid-l]
             :module.api/strings-sym 'valid-str}
            (z/of-string (str "(ns valid\n"
                              "  \" all-base-lang-strs below.\"\n"
                              "  (:require a))\n\n"
                              "(def all-base-lang-strs\n"
                              "  \"docstring.\"\n"
                              "  (merge a/strings))\n")
                         {:track-position? true})))))


  (testing (str "With a zloc defining all-base-lang-strs entries on their own "
                "lines, returns the expected strings ns source code string "
                "with proper indentation")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def all-base-lang-strs\n"
                "  (merge\n"
                "   a/strings\n"
                "   b/strings\n"
                "   valid-str))\n")
          (sut/add-to-strings
            {:module.api/libspec 'valid-l
             :module.api/strings-sym 'valid-str}
            (z/of-string (str "(ns valid)\n\n"
                              "(def all-base-lang-strs\n"
                              "  (merge\n"
                              "   a/strings\n"
                              "   b/strings))\n")
                         {:track-position? true})))))

  ;;; With Invalid Arg Data

  (testing "With all empty or nil args, throws an error"
    (is (thrown? AssertionError (sut/add-to-strings nil)))
    (is (thrown? AssertionError (sut/add-to-strings {})))
    (is (thrown? AssertionError (sut/add-to-strings nil nil)))
    (is (thrown? AssertionError (sut/add-to-strings {} nil))))

  (testing "With a nil libspec in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec nil
                               :module.api/strings-sym 'valid-str}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def all-base-lang-strs\n"
                                               "  (merge a/strings))\n")
                                          {:track-position? true})))))

  (testing "With an invalid libspec in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec :INVALID-L
                               :module.api/strings-sym 'valid-str}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def all-base-lang-strs\n"
                                               "  (merge a/strings))\n")
                                          {:track-position? true})))))

  (testing "With a nil strings symbol in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec 'valid-l
                               :module.api/strings-sym nil}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def all-base-lang-strs\n"
                                               "  (merge a/strings))\n")
                                          {:track-position? true})))))

  (testing "With an invalid strings symbol in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec 'valid-l
                               :module.api/strings-sym :INVALID-STR}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def all-base-lang-strs\n"
                                               "  (merge a/strings))\n")
                                          {:track-position? true})))))

  (testing (str "With a missing all-base-lang-strs def in the strings source "
                "code, throws an error")
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec 'valid-l
                               :module.api/strings-sym 'valid-str}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def INVALID-VAR\n"
                                               "  (merge a/strings))\n")
                                          {:track-position? true})))))

  (testing "With a nil zloc, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec 'valid-l
                               :module.api/strings-sym 'valid-str}
                             nil))))

  (testing "With a missing :track-position? option in the zloc, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec 'valid-l
                               :module.api/strings-sym 'valid-str}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def all-base-lang-strs\n"
                                               "  (merge a/strings))\n")
                                          {:INVALID "no :track-position?"})))))

  (testing (str "With a :track-position? of false in the zloc options, throws "
                 "an error")
    (is (thrown? AssertionError
          (sut/add-to-strings {:module.api/libspec 'valid-l
                               :module.api/strings-sym 'valid-str}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def all-base-lang-strs\n"
                                               "  (merge a/strings))\n")
                                          {:track-position? false}))))))

;;;; editor/add-to-stylesheets

(deftest add-to-stylesheets-test

  ;; With Valid Arg Data

  (testing (str "With a zloc defining module-css with one entry, returns "
                "the expected stylesheets ns source code")
    (is (= (str "(ns valid\n"
                "  (:require a\n"
                "            [valid-l]))\n\n"
                "(def module-css\n"
                "  (concat a/css\n"
                "          valid-r))\n")
           (sut/add-to-stylesheets
            {:module.api/libspec ['valid-l]
             :module.api/css-sym 'valid-r}
            (z/of-string (str "(ns valid\n"
                              "  (:require a))\n\n"
                              "(def module-css\n"
                              "  (concat a/css))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining module-css with both a docstring and "
                "one entry, returns the expected stylesheets ns source code")
    (is (= (str "(ns valid\n"
                "  \" module-css below.\"\n"
                "  (:require a\n"
                "            [valid-l]))\n\n"
                "(def module-css\n"
                "  \" docstring. \"\n"
                "  (concat a/css\n"
                "          valid-r))\n")
           (sut/add-to-stylesheets
            {:module.api/libspec ['valid-l]
             :module.api/css-sym 'valid-r}
            (z/of-string (str "(ns valid\n"
                              "  \" module-css below.\"\n"
                              "  (:require a))\n\n"
                              "(def module-css\n"
                              "  \" docstring. \"\n"
                              "  (concat a/css))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining module-css entries on their own "
                "lines, returns the expected stylesheets ns source code string "
                "with proper indentation")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def module-css\n"
                "  (concat\n"
                "   a/css\n"
                "   valid-css))\n")
          (sut/add-to-stylesheets
           {:module.api/libspec 'valid-l
            :module.api/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                             "(def module-css\n"
                             "  (concat\n"
                             "   a/css))\n")
                        {:track-position? true})))))

  ;;; With Invalid Arg Data

  (testing "With all empty or nil args, throws an error"
    (is (thrown? AssertionError (sut/add-to-stylesheets nil)))
    (is (thrown? AssertionError (sut/add-to-stylesheets {})))
    (is (thrown? AssertionError (sut/add-to-stylesheets nil nil)))
    (is (thrown? AssertionError (sut/add-to-stylesheets {} nil))))

  (testing "With a nil libspec in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-stylesheets
           {:module.api/libspec nil
            :module.api/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                             "(def module-css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With an invalid libspec in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-stylesheets {:module.api/libspec :INVALID-L
                                   :module.api/css-sym 'valid-css}
                             (z/of-string (str "(ns valid)\n\n"
                                               "(def module-css\n"
                                               "  (concat a/css))\n")
                                          {:track-position? true})))))

  (testing "With a nil css symbol in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-stylesheets
           {:module.api/libspec 'valid-l
            :module.api/css-sym nil}
           (z/of-string (str "(ns valid)\n\n"
                             "(def module-css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With an invalid css symbol in the module-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-stylesheets
           {:module.api/libspec 'valid-l
            :module.api/css-sym :INVALID-STY}
           (z/of-string (str "(ns valid)\n\n"
                             "(def module-css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing (str "With a missing module-css def in the stylesheets source "
                "code, throws an error")
    (is (thrown? AssertionError
          (sut/add-to-stylesheets
           {:module.api/libspec 'valid-l
            :module.api/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                             "(def INVALID-VAR\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With a nil zloc, throws and error"
    (is (thrown? AssertionError
          (sut/add-to-stylesheets
           {:module.api/libspec 'valid-l
            :module.api/css-sym 'valid-css}
           nil))))

  (testing "With a missing :track-position? option in the zloc, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-stylesheets
           {:module.api/libspec 'valid-l
            :module.api/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                             "(def module-css\n"
                             "  (concat a/css))\n")
                        {:INVALID "no :track-position?"})))))

  (testing (str "With a :track-position? of false in the zloc options, throws "
                "an error")
    (is (thrown? AssertionError
          (sut/add-to-stylesheets
           {:module.api/libspec 'valid-l
            :module.api/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                             "(def module-css\n"
                             "  (concat a/css))\n")
                        {:track-position? false}))))))

;;;; editor/add-to-module-api

(deftest add-to-module-api-test

  ;;; With Valid Arg Data: All (Both Required and Optional)

  (testing (str "With a zloc defining routes, strings, and css data with "
                "nils, replaces the nils and returns the expected module-api "
                "ns source code")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def routes\n"
                "  [[\"/valid-path\" {:get valid-handler}]])\n\n"
                "(def strings\n"
                "  (merge\n"
                "   valid-str))\n\n"
                "(def css\n"
                "  (concat\n"
                "   valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/route ["/valid-path" {:get 'valid-handler}]
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  nil)\n\n"
                              "(def strings\n"
                              "  (merge\n"
                              "   nil))\n\n"
                              "(def css\n"
                              "  (concat\n"
                              "   nil))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining routes, strings, and css data each "
                "with one entry, inserts at the correct points and returns the "
                "expected module-api ns source code")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def routes\n"
                "  [[\"/\" a/handler]\n"
                "   [\"/valid-path\" {:get valid-handler}]])\n\n"
                "(def strings\n"
                "  (merge\n"
                "   a/strings\n"
                "   valid-str))\n\n"
                "(def css\n"
                "  (concat\n"
                "   a/css\n"
                "   valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/route ["/valid-path" {:get 'valid-handler}]
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                              "(def strings\n"
                              "  (merge\n"
                              "   a/strings))\n\n"
                              "(def css\n"
                              "  (concat\n"
                              "   a/css))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining routes, strings, and css data each "
                "with both docstrings and one entry, inserts at the correct "
                "points and returns the expected module-api ns source code")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def routes\n"
                "  \" docstring. \"\n"
                "  [[\"/\" a/handler]\n"
                "   [\"/valid-path\" {:get valid-handler}]])\n\n"
                "(def strings\n"
                "  \" docstring. \"\n"
                "  (merge\n"
                "   a/strings\n"
                "   valid-str))\n\n"
                "(def css\n"
                "  \" docstring. \"\n"
                "  (concat\n"
                "   a/css\n"
                "   valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/route ["/valid-path" {:get 'valid-handler}]
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  \" docstring. \"\n"
                              "  [[\"/\" a/handler]])\n\n"
                              "(def strings\n"
                              "  \" docstring. \"\n"
                              "  (merge\n"
                              "   a/strings))\n\n"
                              "(def css\n"
                              "  \" docstring. \"\n"
                              "  (concat\n"
                              "   a/css))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining routes, strings, and css data each "
                "with their first entry on the same line as the fn symbol, "
                "returns the properly indented module-api ns source code")
    (is (= (str "(ns valid\n"
                "  (:require [a :as a]\n"
                "            valid-l))\n\n"
                "(def routes [[\"/\" a/handler]\n"
                "             [\"/valid-path\" {:get valid-handler}]])\n\n"
                "(def strings\n"
                "  (merge a/strings\n"
                "         valid-str))\n\n"
                "(def css\n"
                "  (concat a/css\n"
                "          valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/route ["/valid-path" {:get 'valid-handler}]
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid\n"
                              "  (:require [a :as a]))\n\n"
                              "(def routes [[\"/\" a/handler]])\n\n"
                              "(def strings\n"
                              "  (merge a/strings))\n\n"
                              "(def css\n"
                              "  (concat a/css))\n")
                         {:track-position? true})))))

  ;;; With Valid Arg Data: Required Only (Without Optional)

  (testing (str "With a zloc defining strings and css data with nils, "
                "replaces the nils and returns the expected module-api ns "
                "source code")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def routes\n"
                "  nil)\n\n"
                "(def strings\n"
                "  (merge\n"
                "   valid-str))\n\n"
                "(def css\n"
                "  (concat\n"
                "   valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  nil)\n\n"
                              "(def strings\n"
                              "  (merge\n"
                              "   nil))\n\n"
                              "(def css\n"
                              "  (concat\n"
                              "   nil))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining strings and css data each with one "
                "entry , inserts at the correct points and returns the "
                "expected module-api ns source code")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def routes\n"
                "  [[\"/\" a/handler]])\n\n"
                "(def strings\n"
                "  (merge\n"
                "   a/strings\n"
                "   valid-str))\n\n"
                "(def css\n"
                "  (concat\n"
                "   a/css\n"
                "   valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                              "(def strings\n"
                              "  (merge\n"
                              "   a/strings))\n\n"
                              "(def css\n"
                              "  (concat\n"
                              "   a/css))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining strings and css data each with both "
                "docstrings and one entry, inserts at the correct points and "
                "returns the expected module-api ns source code")
    (is (= (str "(ns valid\n"
                "  (:require\n"
                "   valid-l))\n\n"
                "(def routes\n"
                "  [[\"/\" a/handler]])\n\n"
                "(def strings\n"
                "  \" docstring. \"\n"
                "  (merge\n"
                "   a/strings\n"
                "   valid-str))\n\n"
                "(def css\n"
                "  \" docstring. \"\n"
                "  (concat\n"
                "   a/css\n"
                "   valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                              "(def strings\n"
                              "  \" docstring. \"\n"
                              "  (merge\n"
                              "   a/strings))\n\n"
                              "(def css\n"
                              "  \" docstring. \"\n"
                              "  (concat\n"
                              "   a/css))\n")
                         {:track-position? true})))))

  (testing (str "With a zloc defining strings and css data each with their "
                "first entry on the same line as the fn symbol, returns the"
                "expected module-api ns source code string with proper "
                "indentation")
    (is (= (str "(ns valid\n"
                "  (:require [a :as a]\n"
                "            valid-l))\n\n"
                "(def routes [[\"/\" a/handler]])\n\n"
                "(def strings\n"
                "  (merge a/strings\n"
                "         valid-str))\n\n"
                "(def css\n"
                "  (concat a/css\n"
                "          valid-css))\n")
           (sut/add-to-module-api
            {:unit/libspec 'valid-l
             :unit/strings-sym 'valid-str
             :unit/css-sym 'valid-css}
            (z/of-string (str "(ns valid\n"
                              "  (:require [a :as a]))\n\n"
                              "(def routes [[\"/\" a/handler]])\n\n"
                              "(def strings\n"
                              "  (merge a/strings))\n\n"
                              "(def css\n"
                              "  (concat a/css))\n")
                         {:track-position? true})))))

  ;;; With Invalid Arg Data

  (testing "With all empty or nil args, throws an error"
    (is (thrown? AssertionError (sut/add-to-module-api nil)))
    (is (thrown? AssertionError (sut/add-to-module-api {})))
    (is (thrown? AssertionError (sut/add-to-module-api nil nil)))
    (is (thrown? AssertionError (sut/add-to-module-api {} nil))))

  (testing "With a nil libspec in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec nil
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With an invalid libspec in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec :INVALID-L
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With an invalid route in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/route :INVALID-R
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With a route having invalid params in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/route ["/valid-path" :INVALID-PARAMS]
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With a nil strings-sym in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym nil
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With an invalid strings-sym in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym :INVALID-STR
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With a nil css-sym in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym 'valid-str
            :unit/css-sym nil}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With an invalid css-sym in unit-def, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym 'valid-str
            :unit/css-sym :INVALID-STY}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing "With a missing :track-position? option in the zloc, throws an error"
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:INVALID "no :track-position?"})))))

  (testing (str "With a :track-position? of false in the zloc options, throws "
                "an error")
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns valid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? false})))))

  (testing (str "With a unit route and a missing routes def in the module-api "
                "source code, throws an error")
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/route []
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns invalid)\n\n"
                              "(def INVALID-VAR\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing (str "With a missing strings def in the module-api source code, "
                "throws an error")
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns invalid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def INVALID-VAR\n"
                             "  (merge a/strings))\n\n"
                             "(def css\n"
                             "  (concat a/css))\n")
                        {:track-position? true})))))

  (testing (str "With a missing css def in the module-api source code, "
                "throws an error")
    (is (thrown? AssertionError
          (sut/add-to-module-api
           {:unit/libspec 'valid-l
            :unit/strings-sym 'valid-str
            :unit/css-sym 'valid-css}
           (z/of-string (str "(ns invalid)\n\n"
                              "(def routes\n"
                              "  [[\"/\" a/handler]])\n\n"
                             "(def strings\n"
                             "  (merge a/strings))\n\n"
                             "(def INVALID-VAR\n"
                             "  (concat a/css))\n")
                        {:track-position? true}))))))
