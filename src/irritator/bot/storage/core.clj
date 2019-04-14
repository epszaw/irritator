(ns irritator.bot.storage.core
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:gen-class))

(defn connect [host port]
  (def connection (mg/connect {:host host :port port}))

  (def db (mg/get-db connection "irritator")))

