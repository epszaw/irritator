(ns irritator.storage.queue
  (:require [monger.collection :as mc]
            [irritator.utils :refer [normalize-doc]]
            [irritator.storage.core :refer [db]])
  (:import org.bson.types.ObjectId)
  (:gen-class))

(defn push-message [command payload]
  (mc/insert-and-return 
    db 
    "queue" 
    (merge {:command command} payload)))

(defn get-first-message [simplify?]
  (let [doc (mc/find-one db "queue" {})]
    (if (and simplify? (some? doc))
      (normalize-doc doc) doc)))

(defn remove-message [id]
  (mc/remove-by-id db "queue" (ObjectId. id)))

(defn get-message [id]
  (mc/find-map-by-id db "queue" (ObjectId. id)))

(ifn? true)
