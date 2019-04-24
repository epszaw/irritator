(ns irritator.bot.core
  (:require [clojure.tools.logging :as log]
            [yaml.core :as yaml]
            [irritator.bot.storage.core :as storage]
            [irritator.bot.bot :as bot]
            [irritator.bot.api :as api])
  (:gen-class))

(def config (yaml/from-file "config.yml"))

(defn -main [& args]
  (let [whitelist (:allowed-usernames config [])
        token (:telegram-token config nil)
        secret (:secret config nil)
        db-host (:db-host config "127.0.0.1")
        db-port (:db-port config 27017)]

    (when
     (= token nil)
      (println "Please provide token in telegram-token config.yml file!")
      (System/exit 1))

    (when
     (= secret nil)
      (println "Please provide secret in secret config.yml file!")
      (System/exit 1))

    (try
      (storage/connect db-host db-port)
      (api/start secret)
      (bot/start token whitelist)
      (catch Exception ex
        (log/error ex "Irritator runtime error")))))
