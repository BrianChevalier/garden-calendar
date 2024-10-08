(ns io.brianchevalier.app-test
  (:require
   ["@testing-library/react" :as react-testing-library]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [oops.core :as oops]
   [io.brianchevalier.app :as app]
   [uix.core :refer [$]]))

(use-fixtures :each {:after react-testing-library/cleanup})

(deftest app
  (testing "that we can render the app"
    (let [element (react-testing-library/render ($ app/app))]
      (is (some? (oops/ocall element :queryByText "brianchevalier"))))))
