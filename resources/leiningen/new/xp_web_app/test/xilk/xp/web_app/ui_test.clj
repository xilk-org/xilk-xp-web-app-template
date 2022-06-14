(ns xilk.xp.web-app.ui-test
  (:require
   [clojure.test :refer [deftest is]]
   [xilk.xp.web-app.ui :as sut]))

;;;; css

;;; css Arity 1

(deftest css__with-one-simple-form__returns-css
  (is (= "a {\n  color: red;\n}"
         (sut/css [:a {:color :red}]))))

(deftest css__with-vector-of-one-nested-simple-form__returns-css
  (is (= "a {\n  color: red;\n}"
         (sut/css [[:a {:color :red}]]))))

(deftest css__with-vector-of-multiple-nested-simple-forms__returns-css
  (is (= "a {\n  color: red;\n}\n\nb {\n  border: 0;\n}\n\nc {\n  width: 5%;\n}"
         (sut/css [[:a {:color :red}]
                   [:b {:border 0}]
                   [:c {:width "5%"}]]))))

;;; css Arity n

(deftest css__with-multiple-simple-forms__returns-css
  (is (= "a {\n  color: red;\n}\n\nb {\n  border: 0;\n}\n\nc {\n  width: 5%;\n}"
         (sut/css [:a {:color :red}]
                  [:b {:border 0}]
                  [:c {:width "5%"}]))))

;;;; html

;;; html Arity 1

(deftest html__with-void-element-with-attribute__returns-unterminated-html5
  (is (= "<meta charset=\"utf-8\">"
         (str (sut/html [:meta {:charset "utf-8"}])))))

(deftest html__with-void-element-without-attributes__returns-unterminated-html5
  (is (= "<br>"
         (str (sut/html [:br])))))

(deftest html__with-string-content__returns-html-with-string-escaped
  (is (= "<p>&lt;script&gt;alert(&apos;no injection&apos;)&lt;/script&gt;</p>"
         (str (sut/html [:p "<script>alert('no injection')</script>"])))))

(deftest html__with-unescapable-string-content__returns-html-with-string-unescaped
  (is (= "<p><script>alert('SCRIPT INJECTION')</script></p>"
         (str (sut/html [:p (sut/dangerous-unescapable-string
                             "<script>alert('SCRIPT INJECTION')</script>")])))))

;;; html Arity n

(deftest html__with-multiple-unnested-forms__returns-html
  (is (= "<h1>a</h1><h2>b</h2><h3>c</h3>"
         (str (sut/html [:h1 "a"]
                        [:h2 "b"]
                        [:h3 "c"])))))

(deftest html__with-multiple-unnested-forms-including-for-seq__returns-html
  (is (= "<h1>a</h1><p>1</p><p>2</p><p>3</p>"
         (str (sut/html [:h1 "a"]
                        (for [x (range 1 4)]
                          [:p x]))))))

;;;; dangerous-unescapable-string

(deftest dangerous-unescapable-string__with-string__returns-raw-string-object
  (is (= "<script></script>"
         (str (sut/dangerous-unescapable-string "<script></script>")))))
