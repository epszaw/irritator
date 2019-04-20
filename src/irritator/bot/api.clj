(ns irritator.bot.api
  (:require [clojure.core :refer [future]]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response]]
            [org.httpkit.server :as http]
            [irritator.bot.chat :as chat]
            [irritator.utils.core :refer [qs-to-hash get-request-body]]
            [irritator.bot.storage.queue :refer [get-first-message remove-message]]
            [irritator.bot.storage.users :refer [save-user remove-user]])
  (:gen-class))

(defn create-response [status body]
  {:status status
   :headers {"Content-Type" "application/edn"}
   :body body})

(defn start [secret]
  (println "api: Starting the irritator API! 🚀")

  (defn get-message-handler [{qs :query-string}]
    ; TODO: move that to func
    (if (= secret (:secret (qs-to-hash qs)))
      (create-response
       200
       {:ok true
        :payload (get-first-message true)})
      (create-response
       403
       {:ok false
        :error "Secret is invalid!"})))

  (defn process-message-handler [{qs :query-string}]
    (let [req (qs-to-hash qs)]
      ; TODO: move that to func
      (if (= secret (:secret req))
        (do
          (chat/send-message (select-keys req [:_id :message]))
          (create-response
           200
           {:ok true}))
        (create-response
         403
         {:ok false
          :error "Secret is invalid!"}))))

  (compojure/defroutes router
    (compojure/POST "/" [] process-message-handler)
    (compojure/GET "/" [] get-message-handler)
    (route/not-found (create-response 404 {:error "Not found!"})))

  (future
    (http/run-server (wrap-json-response router) {:port 8080})))
