(ns clj-plex.playlist)

(defn- item-to-playlist [playlist-xml]
  (-> playlist-xml
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
                    :updatedAt])))

(defn response [response-xml]
  (->> response-xml
       :contents
       (map item-to-playlist)))
