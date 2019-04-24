(ns irritator.daemon.core
  (:require [clojure.tools.logging :as log]
            [clojure.core :refer [future]]
            [yaml.core :as yaml]
            [irritator.daemon.api :as api]
            [irritator.daemon.player :as player]
            [irritator.daemon.processor :as processor]
            [irritator.daemon.terminator :as terminator]
            [irritator.utils.core :refer [interval]])
  (:gen-class))

(def config (yaml/from-file "config.yml"))

(defn -main [& args]

  (let [processing? (atom false)
        secret (:secret config)
        bot-url (:bot-url config)
        player-interval (:player-interval config)
        borders (:borders config)
        resources-path (:resources-path config)]

    (defn send-message-procession-result [res]
      (api/send-processed-message res))

    (defn process-next-message-response [msg]
      (when msg (processor/process-message msg send-message-procession-result)))

    (defn daemon-tick-handler []
      (when (and (terminator/terminate?) @player/current-sample)
        (player/stop))

      ; TODO: make daemon-level flag of processing
      ; (when (and (false? (terminator/terminate?)) ))

      (when (not @processing?)
        (do
          (reset! processing? true)
          (try
            (api/get-next-message process-next-message-response)
            (catch Exception ex
              (log/error ex "Irritator runtime error"))
            (finally (reset! processing? false))))))

    (println "daemon: irritator daemon API started! 🚀")

    (player/configure resources-path player-interval)
    (terminator/configure borders)
    (api/configure (:bot-url config) (:secret config))
    (interval 1000 daemon-tick-handler)))
