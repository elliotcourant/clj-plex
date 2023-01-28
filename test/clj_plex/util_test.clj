(ns clj-plex.util-test
  (:require
   [clj-plex.util :as util]
   [clojure.test :refer [deftest is testing]])
  (:import [org.xml.sax SAXParseException]))

(deftest trim-left-test
  (testing "with valid prefix"
    (is (= "api" (util/trim-left "/api" "/"))))
  (testing "without valid suffix"
    (is (= "api" (util/trim-left "api" "/")))))

(deftest trim-right-test
  (testing "with valid suffix"
    (is (= "https://plex.tv" (util/trim-right "https://plex.tv/" "/"))))
  (testing "without valid suffix"
    (is (= "https://plex.tv/test" (util/trim-right "https://plex.tv/test" "/")))))

(deftest parse-xml-response-test
  (testing "parse xml"
    (let [response {:body "<MediaContainer size=\"1\"><Playlist ratingKey=\"27454\" key=\"/playlists/27454/items\" guid=\"com.plexapp.agents.none://f3b33d7b-66fd-4c6e-adfd-da32114907b7\" type=\"playlist\" title=\"Liked Songs - playlist - Spotify\" summary=\"\" smart=\"0\" playlistType=\"audio\" composite=\"/playlists/27454/composite/1674922394\" viewCount=\"6\" lastViewedAt=\"1674929776\" duration=\"31912000\" leafCount=\"131\" addedAt=\"1674883174\" updatedAt=\"1674922394\"></Playlist></MediaContainer>"}
          result (util/parse-xml-response response)]
      (is (some? result))
      (is (= :MediaContainer (-> result :tag)))
      (is (= "1" (-> result :attrs :size)))))
  (testing "fails to parse json"
    (let [response {:body "{\"foo\": \"bar\"}"}]
      (is (thrown? SAXParseException (util/parse-xml-response response))))))

