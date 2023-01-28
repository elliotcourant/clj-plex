(ns clj-plex.client
  (:require
   [clj-http.client :as client]
   [clj-plex.util :as util]))

(defprotocol PlexClient
  "The protocol for interacting with a Plex server."
  (playlists [this] "Retrieve all Plex playlists.")
  (playlist [this title] "Retrieve a Plex playlist matching the specified title."))

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

(defmethod response :MediaContainer
  [element]
  (let [attrs (:attrs element)
        content (->> element :content (map response))]
    (assoc attrs :content content
                 :kind :MediaContainer)))

(defmethod response :Playlist
  [element]
  (-> element
      :attrs
      (select-keys [:ratingKey
                    :key
                    :guid
                    :type
                    :title
                    :titleSort
                    :summary
                    :smart
                    :playlistType
                    :icon
                    :viewCount
                    :lastViewedAt
                    :leafCount
                    :addedAt
                    :updatedAt])
      (assoc :kind :Playlist)))

(defrecord Client
  [token baseurl]
  PlexClient
  (playlists [this]
    (-> this
        (http-get "/playlists")
        (response)))
  (playlist [this title]
    (-> this
        (http-get "/playlists" {:title title})
        (response))))

