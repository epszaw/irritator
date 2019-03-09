(ns irritator.core
  (:require [clojure.string :as str]
            [yaml.core :as yaml]
            [irritator.bot :as bot]
            [irritator.player :as player])
  (:gen-class))

(def config (yaml/from-file "config.yml"))

(defn -main
  [& args]
  (let [
    token (:telegram-token config)
    whitelist (:allowed-usernames config)]
      (when (str/blank? token)
        (println "Please provide token in telegram-token config.yml file!")
        (System/exit 1))

      (println "Starting the irritator 🚀")
      (bot/start token whitelist)))
