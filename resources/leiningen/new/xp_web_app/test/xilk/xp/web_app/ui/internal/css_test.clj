(ns xilk.xp.web-app.ui.internal.css-test
  (:require
   [clojure.test :refer [are deftest is testing]]
   [xilk.xp.web-app.ui.internal.css :as sut]))

(deftest css-class-str-test
  (testing "With a qualified keyword, returns a matching CSS class string."
    (is (= "simple-ns__class-sel"
           (sut/css-class-str :simple-ns/class-sel)))

    (is (= "domain_project_my-feature__class-sel"
           (sut/css-class-str :domain.project.my-feature/class-sel))))

  (testing "With an unqualified keyword,"
    (testing (str "that IS a Garden class selector keyword, returns a matching "
                  "class string.")
      (is (= "class-sel"
             (sut/css-class-str :.class-sel))))

    (testing "that IS NOT a Garden class selector keyword, throws an error."
      (is (thrown? AssertionError
                   (sut/css-class-str :unqualified-non-class-sel-kw))))))

(deftest garden-sel-kw-test
  (testing (str "With a qualified keyword, returns a matching Garden class "
                "selector keyword.")
    (is (= :.simple-ns__class-sel
           (sut/garden-sel-kw :simple-ns/class-sel)))

    (is (= :.domain_project_my-feature__class-sel
           (sut/garden-sel-kw :domain.project.my-feature/class-sel))))

  (testing "With an unqualified keyword, throws an error."
    (is (thrown? AssertionError (sut/garden-sel-kw :type-sel)))
    (is (thrown? AssertionError (sut/garden-sel-kw :.class-sel)))
    (is (thrown? AssertionError (sut/garden-sel-kw :#id-sel)))))

(deftest transform-qualified-sel-kws-for-garden-css-compilation-test
  (testing "With rules including"
    (testing (str "only vectors, returns rules with qualified keywords"
                  "transformed.")
      (is (= [[:type-sel {:a :b}]
              [:.class-sel {:c :d}]
              [:.domain_project_my-feature__class-sel {:e :f}]]
             (sut/transform-qualified-sel-kws-for-garden-css-compilation
              [[:type-sel {:a :b}]
               [:.class-sel {:c :d}]
               [:domain.project.my-feature/class-sel {:e :f}]]))))

    (testing (str "mixed elements, returns rules with qualified keywords"
                  "transformed.")
      (is (= [{:a :b}
              [:.domain_project_my-feature__class-sel {:c :d}]
              [:#id-sel {:e :f}]]
             (sut/transform-qualified-sel-kws-for-garden-css-compilation
              [{:a :b}
               [:domain.project.my-feature/class-sel {:c :d}]
               [:#id-sel {:e :f}]]))))))

(deftest transform-qualified-sel-kws-for-hiccup-html-compilation-test
  (testing "With a single-element form having no class attrs, returns the form"
    (are [result hiccup]
         (= result
            (sut/transform-qualified-sel-kws-for-hiccup-html-compilation hiccup))
         [:p "text"]
         [:p "text"]

         [:div {:id "div"} "text"]
         [:div {:id "div"} "text"]))
  (testing "With a nested-element form having no class attrs, returns the form"
    (are [result hiccup]
         (= result
            (sut/transform-qualified-sel-kws-for-hiccup-html-compilation hiccup))
         [:div [:p "text"]]
         [:div [:p "text"]]

         [:div {:id "div1"} [:div {:id "div2"} [:p "text"]]]
         [:div {:id "div1"} [:div {:id "div2"} [:p "text"]]]))
  (testing "With a vector of forms having no class attrs, returns the vector"
    (are [result hiccup]
         (= result
            (sut/transform-qualified-sel-kws-for-hiccup-html-compilation hiccup))
         [[:h1 "heading"] [:div [:p "text"]]]
         [[:h1 "heading"] [:div [:p "text"]]]

         [[:h1 {:id "h1"} "h"] [:div {:id "div1"} [:div {:id "div2"} [:p "t"]]]]
         [[:h1 {:id "h1"} "h"] [:div {:id "div1"} [:div {:id "div2"} [:p "t"]]]]))
  (testing "With forms having string class attrs, returns the forms"
    (are [result hiccup]
         (= result
            (sut/transform-qualified-sel-kws-for-hiccup-html-compilation hiccup))
         [:h1 {:class "h1"} "heading"]
         [:h1 {:class "h1"} "heading"]

         [:div {:class "div1"} [:div {:class "div2"} [:p {:class "p"} "text"]]]
         [:div {:class "div1"} [:div {:class "div2"} [:p {:class "p"} "text"]]]

         [[:h1 {:class "h1"} "heading"] [:div [:p {:class "p"} "text"]]]
         [[:h1 {:class "h1"} "heading"] [:div [:p {:class "p"} "text"]]]))
  (testing "With forms having valid kw class attrs, returns the updated forms"
    (are [result hiccup]
         (= result
            (sut/transform-qualified-sel-kws-for-hiccup-html-compilation hiccup))
         [:h1 {:class "sel"} "heading"]
         [:h1 {:class :.sel} "heading"]

         [:h1 {:class "my_ns__sel"} "heading"]
         [:h1 {:class :my.ns/sel} "heading"]))
  (testing "With forms having invalid kw class attrs, throws an error"
    (is (thrown? AssertionError
            (sut/transform-qualified-sel-kws-for-hiccup-html-compilation
             [:div {:class :UNQUALIFIED-NON-CLASS-SEL-KW}]))))
  (testing "With forms having vector class attrs, returns the updated forms"
    (are [result hiccup]
         (= result
            (sut/transform-qualified-sel-kws-for-hiccup-html-compilation hiccup))
         [:h1 {:class ["h1"]} "heading"]
         [:h1 {:class ["h1"]} "heading"]

         [:h1 {:class ["sel" "h1"]} "heading"]
         [:h1 {:class [:.sel "h1"]} "heading"]

         [:h1 {:class ["my_ns__sel" "h1"]} "heading"]
         [:h1 {:class [:my.ns/sel "h1"]} "heading"]

         [:h1 {:class ["my_ns__sel" "h1" "sel"]} "heading"]
         [:h1 {:class [:my.ns/sel "h1" :.sel]} "heading"]

         [:div {:class ["my_ns__d1"]} [:div {:class ["d2"]} [:p "text"]]]
         [:div {:class [:my.ns/d1]} [:div {:class [:.d2]} [:p "text"]]]

         [[:h1 {:class ["sel" "h1"]} "h"] [:p {:class ["my_ns__sel"]} "text"]]
         [[:h1 {:class [:.sel "h1"]} "h"] [:p {:class [:my.ns/sel]} "text"]])))
