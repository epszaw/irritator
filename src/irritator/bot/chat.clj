(ns irritator.bot.chat
  (:require [clojure.core :refer [future]]
            [clojure.tools.logging :as log]
            [morse.api :as t]
            [irritator.utils.core :refer [decode-escaped-string]]
            [irritator.bot.storage.users :refer [save-user remove-user get-all-users get-user]]
            [irritator.bot.storage.queue :refer [push-message remove-message get-message]])
  (:gen-class))

(defn configure [token whitelist]

  (defn is-permitted-chat? [{username :username}]
    (some #(= username %) whitelist))

  (defn send-direct-message [id text]
    (t/send-text token id text))

  (defn send-broadcast-message [id text]
    (let [users (get-all-users)]
      (when (not (empty? users))
        (->> users
             (filter #(not (= id (:chat_id %))))
             (map #(send-direct-message (:chat_id %) text))
             (doall)))
      (send-direct-message id text)))

  (defn send-message [message]
    (let [id (:_id message)
          msg (get-message id)
          broadcast? (:broadcast? message)]

      (if broadcast?
        (send-direct-message (:id msg) (decode-escaped-string (:message message)))
        (send-broadcast-message (:id msg) (decode-escaped-string (:message message))))
      (remove-message id)))

  (defn add-subscribtion [chat]
    (let [id (:id chat)
          user (get-user id)]

      (if user
        (send-direct-message id "You are already subscribed on irritator broadcasting. Use /unsubscribe command to unsubscribe from it.")
        (do
          (save-user chat)
          (send-direct-message id "You successfully subscribe on irritator broadcasting! 🐵")))))

  (defn remove-subscribtion [chat]
    (let [id (:id chat)
          user (get-user id)]
      (if (nil? user)
        (send-direct-message id "You are not subscribed on irritator broadcasting. Use /subscribe command to subscribe on it.")
        (do
          (remove-user (:_id user))
          (send-direct-message id "You successfully unsubscribe from irritator broadcasting! 🙉")))))

  (defn preprocess-not-permitted-command [{id :id :as chat}]
    (log/info (str (:username chat) " is not whitelisted username, add it to config.yml file!"))
    (t/send-text token id "You are not in the bot whitelist, sowwy 😢"))

  (defn preprocess-permitted-command [command chat]
    (case command
      "subscribe" (add-subscribtion chat)
      "unsubscribe" (remove-subscribtion chat)
      (push-message command chat)))

  (defn preprocess-command [command chat]
    (if (is-permitted-chat? chat)
      (preprocess-permitted-command command chat)
      (preprocess-not-permitted-command chat))))
