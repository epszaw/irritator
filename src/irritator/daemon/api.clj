(ns irritator.daemon.api
  (:require [org.httpkit.client :as http]
            [irritator.utils :refer [get-request-body]])
  (:gen-class))

(defn configure [url secret]

  (defn get-next-message [cb]
    (http/get url 
      {:query-params {:secret secret} }
      (fn [res]
        (->
          (get-request-body res)
          (cb)))))

  (defn send-processed-message [payload]
    (http/post 
      url 
      {:query-params 
        (merge {:secret secret} payload)})))
