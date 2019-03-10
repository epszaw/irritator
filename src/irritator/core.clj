(ns irritator.core  
  (:require [clojure.core :refer [future]]
            [yaml.core :as yaml]
            [clj-time.core :as t]
            [clj-time.local :as l]
            [irritator.lifecycle :as lifecycle]
            [irritator.bot :as bot]
            [irritator.player :as player])
  (:gen-class))

(def config (yaml/from-file "config.yml"))

(defn -main
  [& args]
  (let [
    token (:telegram-token config nil)
    whitelist (:allowed-usernames config [])
    timeout (:lifecycle-timeout config 1000)]
      (when (= token nil)
        (println "Please provide token in telegram-token config.yml file!")
        (System/exit 1))

      (println "Starting the irritator! 🚀")

      (bot/start token whitelist timeout)))
