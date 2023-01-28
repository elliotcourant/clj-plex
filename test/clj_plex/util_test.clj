(ns clj-plex.util-test
  (:require
   [clj-plex.util :as util]
   [clojure.test :refer [deftest is testing]]))

(deftest trim-left-test
  (testing "with valid prefix"
    (is (= "api" (util/trim-left "/api" "/"))))
  (testing "without valid suffix"
    (is (= "api" (util/trim-left "api" "/")) )))

(deftest trim-right-test
  (testing "with valid suffix"
    (is (= "https://plex.tv" (util/trim-right "https://plex.tv/" "/"))))
  (testing "without valid suffix"
    (is (= "https://plex.tv/test" (util/trim-right "https://plex.tv/test" "/")))))
