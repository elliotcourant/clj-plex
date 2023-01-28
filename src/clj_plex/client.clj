(ns clj-plex.client
  (:require
   [clj-http.client :as client]
   [clj-plex.util :as util]))

(defprotocol PlexClient
  "The protocol for interacting with a Plex server."
  (playlist [this title] "Retrieve a plex playlist matching the specified title."))

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
                   (map? params) (merge {:query-params params}))]
     (client/get url options))))

(defrecord Client [token baseurl]
  PlexClient
  (playlist [this title]
    (let [result (http-get this "/playlists" {:title title})])))

