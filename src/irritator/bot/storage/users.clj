(ns irritator.bot.storage.users
  (:require [monger.collection :as mc]
            [irritator.bot.storage.core :refer [db]])
  (:gen-class))

(defn get-user [id]
  (mc/find-one db "users" {:chat_id id}))

(defn save-user [payload]
  (when (= (get-user (:id payload)) nil)
    (mc/insert-and-return 
      db 
      "users" 
      {:chat_id (:id payload)
       :first_name (:first_name payload)
       :username (:username payload)})))

(defn remove-user [id]
  (mc/remove db "users" {:_id id}))
