(ns clj-plex.client
  (:require
   [clj-http.client :as client]
   [clj-plex.util :as util]))

(defprotocol PlexClient
  "The protocol for interacting with a Plex server."
  (playlists       [this] "Retrieve all Plex playlists.")
  (playlist        [this title] "Retrieve a Plex playlist matching the specified title.")
  (playlist->items [this rating-key] "Retrieve the items on a playlist by the playlist's rating key."))

(defn- build-url
  "Takes the current client and builds a request URL using the baseurl and the
  provided path. It appends the path to the baseurl of the client. But if the"
  [{:keys [baseurl]} path]
  (-> baseurl
      (util/trim-right "/")
      (str "/" (util/trim-left path "/"))))

(defn- default-client-options [client]
  {:headers {"X-Plex-Token" (:token client)}})

(defn- http-get
  ([client path]
   (http-get client path nil))
  ([client path params]
   (let [url     (build-url client path)
         options (cond-> (default-client-options client)
                   (map? params) (merge {:query-params params}))
         response (client/get url options)]
     (util/parse-xml-response response))))

(defmulti response
  (fn [response-xml]
    (-> response-xml :tag keyword)))

;; If we need to parse a response based on its tag, we can do so by creating a
;; new multimethod here that will branch by tag. Otherwise everything will fall
;; into this implementation which makes all the attributes key value pairs and
;; adds a :kind field with the tag name, and a :content field if there are
;; children to the tag. Children are parsed recursively with response.
(defmethod response :default
  [element]
  (let [tag     (keyword (or (:tag element) :Unknown))
        attrs   (:attrs element)
        content (->> element :content (map response))]
    (cond->
      (assoc attrs :kind tag)
      (seq? content)
      (assoc :content content))))

(defrecord Client [token baseurl] PlexClient
  (playlists [this]
    (-> this
        (http-get "/playlists")
        (response)))
  (playlist [this title]
    (-> this
        (http-get "/playlists" {:title title})
        (response)))
  (playlist->items [this rating-key]
    (-> this
        (http-get (str "/playlists/" rating-key "/items"))
        (response))))

