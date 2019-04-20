(ns irritator.bot.chat
  (:require [clojure.core :refer [future]]
            [clojure.tools.logging :as log]
            [morse.api :as t]
            [irritator.utils.core :refer [decode-escaped-string]]
            [irritator.bot.storage.users :refer []]
            [irritator.bot.storage.queue :refer [push-message remove-message get-message]])
  (:gen-class))

(defn configure [token whitelist]

  ; Permissions stuff
  (defn is-permitted-chat? [{username :username}]
    (some #(= username %) whitelist))

  (defn permission-stub [{id :id :as chat}]
    (log/info (str (:username chat) " is not whitelisted username, add it to config.yml file!"))
    (t/send-text token id "You are not in the bot whitelist, sowwy 😢"))

  (defn preprocess-command [command chat]
    (if (is-permitted-chat? chat)
      (push-message command chat)
      (permission-stub chat)))

  ; TODO: implement this
  (defn broadcast-message [text]
    (println "broadcast msg: " text))

  (defn send-direct-message [id text]
    (t/send-text token id text))

  (defn send-message [message]
    (let [id (:_id message) 
          msg (get-message id)]
      (send-direct-message (:id msg) (decode-escaped-string (:message message)))
      (remove-message id))))
