(ns irritator.bot
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [morse.handlers :as h]
            [morse.polling :as p]            
            [morse.api :as t]
            [irritator.player :as player])
  (:gen-class))

(defn start [token whitelist]
  (defn process-message [id msg] 
    (let [      
      username (:username (:from msg))]
        (println msg)
        (println "Whitelisted user sent message: " msg)
        (t/send-text token id (str "Hello " username " you are permitted to user this bot!"))))

  (defn permission-stub [id msg]
    (let [username (:username (:from msg))]
      (println msg)
      (println (str username " is not whitelisted username, add it to config.yml file!"))
      (t/send-text token id "You are not in the bot whitelist, sowwy 😢")))

  (defn help-command [id]
    (t/send-text token id "Help here!"))

  (defn process-input [cb id msg]
    (let [username (:username (:from msg))]
      (if (some #(= username %) whitelist)
        (cb id msg)
        (permission-stub id msg))))

  (h/defhandler handler

    (h/command-fn "start"
      (fn [{{id :id :as chat} :chat}]
        ; (println "Bot joined new chat: " chat)
        (t/send-text token id "Welcome to irritator! Type /play command to play sounds and /stop to mute everything.")))

    (h/command-fn "help"
      (fn [{{id :id :as chat} :chat}]
        (println "Help was requested in " chat)
        (t/send-text token id "Help is on the way")))

    (h/command-fn "play"
      (fn [{{id :id :as chat} :chat}]
        (player/start "resources/dog_01.mp3")
        (t/send-text token id "Playing sound, bark-bark! 🐶")))

    (h/command-fn "stop"
      (fn [{{id :id :as chat} :chat}]
        (player/terminate)
        (t/send-text token id "Stopped... 🙊"))))

    ; Messages handling
    ; (h/message-fn
    ;   (fn [{{id :id} :chat :as message}]
    ;     (process-input process-message id message)))

  (<!! (p/start token handler)))