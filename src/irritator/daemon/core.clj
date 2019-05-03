(ns irritator.daemon.core
  (:require [clojure.tools.logging :as log]
            [clojure.core :refer [future]]
            [clojure.core.async :refer [go]]
            [yaml.core :as yaml]
            [irritator.daemon.state :as s :refer [state]]
            [irritator.daemon.api :as api]
            [irritator.daemon.player :as player]
            [irritator.daemon.processor :as processor]
            [irritator.daemon.terminator :as terminator :refer [terminate?]]
            [irritator.utils.core :refer [interval]])
  (:gen-class))

(def config (yaml/from-file "config.yml"))

(defn -main [& args]

  (let [secret (:secret config)
        bot-url (:bot-url config)
        player-interval (:player-interval config)
        borders (:borders config)
        resources-path (:resources-path config)]

    (defn send-respose [res]
      (api/send-processed-message res))

    (defn process-next-message []
      (s/mutate-state :processing? true)
      (api/get-next-message
       (fn [msg]
         (cond
           (nil? msg) (do)
           (terminate?) (processor/process-ignored-message msg send-respose)
           (not (terminate?)) (processor/process-message msg send-respose))
         (s/mutate-state :processing? false))))

    (defn daemon-tick-handler []
      (let [current-sample (:current-sample @state)
            suspended? (:suspended? @state)
            playing? (:playing? @state)
            startup-tick? (and (not (terminate?)) (not suspended?) (not playing?))]

        (cond
          (terminate?) (player/stop)
          startup-tick? (go (player/start)))

        (try
          (when (not (:processing? @state)) (process-next-message))
          (catch Exception ex
            (log/error ex "Irritator runtime error")))))

    (println "daemon: irritator daemon API started! 🚀")
    (player/configure resources-path player-interval)
    (terminator/configure borders)
    (api/configure (:bot-url config) (:secret config))
    (interval 1000 daemon-tick-handler)))
