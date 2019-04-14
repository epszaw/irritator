(ns irritator.daemon.core  
  (:require [clojure.tools.logging :as log]
            [yaml.core :as yaml]
            [irritator.daemon.api :as api]
            [irritator.daemon.lifecycle :as lifecycle]
            [irritator.daemon.processor :as processor]
            [irritator.utils.core :refer [interval]])
  (:gen-class))

(def config (yaml/from-file "config.yml"))

(defn -main [& args]
  (let [processing? (atom false)]
    (println "daemon: irritator daemon API started! 🚀")

    (api/configure (:bot-api-url config) (:secret config))
      (interval 1000 (fn []
        (when (false? @processing?)
          (do
            (reset! processing? true)
            (try
              (api/get-next-message 
                (fn [msg]
                  (when msg
                    (processor/process-message msg (fn [res]
                      (api/send-processed-message res))))))
            (catch Exception ex
              (log/error ex "Irritator runtime error"))
            (finally (reset! processing? false)))))))))
