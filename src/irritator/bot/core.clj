(ns irritator.bot.core  
  (:require [clojure.tools.logging :as log]
            [yaml.core :as yaml]            
            [irritator.bot.bot :as bot]
            [irritator.bot.api :as api])
  (:gen-class))

(def config (yaml/from-file "config.yml"))

(defn -main [& args]
  (let [
    token (:telegram-token config nil)
    whitelist (:allowed-usernames config [])
    secret (:secret config nil)]
    (when 
      (= token nil)
        (println "Please provide token in telegram-token config.yml file!")
        (System/exit 1))
    (when 
      (= secret nil)
        (println "Please provide secret in secret config.yml file!")
        (System/exit 1))
    (try 
      (api/start secret)
      (bot/start token whitelist)
      (catch Exception ex
        (log/error ex "Irritator runtime error")))))
