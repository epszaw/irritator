(ns irritator.bot.bot
  (:require [clojure.core.async :refer [<!!]]
            [clojure.core :refer [rand-int]]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [morse.handlers :as h]
            [morse.polling :as p]
            [irritator.bot.chat :as chat]
            [irritator.bot.storage.queue :refer [push-message]])
  (:gen-class))

(defn start [token whitelist]
  (chat/configure token whitelist)

  ; Bot handler stuff
  (h/defhandler handler

    (h/command "help" {chat :chat}
               (chat/preprocess-command "help" chat))

    (h/command "start" {chat :chat}
               (chat/preprocess-command "start" chat))

    (h/command "play" {chat :chat}
               (chat/preprocess-command "play" chat))

    (h/command "stop" {chat :chat}
               (chat/preprocess-command "stop" chat))

    (h/command "subscribe" {chat :chat}
               (chat/preprocess-command "subscribe" chat))

    (h/command "unsubscribe" {chat :chat}
               (chat/preprocess-command "stop" chat))

    ; TODO: implement this
    ; (h/command-fn "kill"
    ;   (fn [{{id :id :as chat} :chat}]
    ;     (t/send-text token id (str "Bye-bye! 😢"))
    ;     (System/exit 1)))

    (h/message {chat :chat}
               (if (chat/is-permitted-chat? chat)
        ; TODO: !!!
                 (chat/send-message (:id chat) "This bot is not support any messages. Please, use /help command to see all commands. ☝️")
                 (chat/permission-stub chat))))

  (println "bot: Starting the irritator bot! 🚀")

  (<!! (p/start token handler)))
