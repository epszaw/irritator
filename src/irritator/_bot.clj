(ns irritator.bot
  (:require [clojure.core.async :refer [<!!]]
            [clojure.core :refer [rand-int]]
            [clojure.string :as str]
            [morse.handlers :as h]
            [morse.polling :as p]            
            [morse.api :as t]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [irritator.lifecycle :as lifecycle]
            [irritator.player :as player])
  (:gen-class))

(defn start [token whitelist timeout]
  (let [
    startup-time (l/local-now)
    playing? (atom false)]

    (defn play-sound [path]
      (when (= playing? false)
        ; TODO: complete this
        (do (
          (reset! playing? true)
          (player/start 
            (player/read-sound path) 
            #((reset! playing? false)))))))

    (defn process-message [id msg] 
      (let [      
        username (:username (:from msg))]
          (println msg)
          (println "Whitelisted user sent message: " msg)
          (t/send-text token id (str "Hello " username " you are permitted to user this bot!"))))

    (defn permission-stub [id msg]
      (let [username (:username (:from msg))]
        (println (str username " is not whitelisted username, add it to config.yml file!"))
        (t/send-text token id "You are not in the bot whitelist, sowwy 😢")))

    (defn process-input [cb id msg]
      (let [username (:username (:from msg))]
        (if (some #(= username %) whitelist)
          (cb id msg)
          (permission-stub id msg))))

    (h/defhandler handler

      (h/command-fn "start"
        (fn [{{id :id :as chat} :chat}]
          (t/send-text token id "Welcome to irritator! Type /play command to play sounds and /stop to mute everything.")))

      (h/command-fn "play"
        (fn [{{id :id :as chat} :chat}]
          (if (= playing? true)
            (t/send-text token id "Irritaion in process. If you want to terminate it use /stop command. 🙏")
            (do (
              (play-sound "resources/dog_01.mp3")
              (t/send-text token id "Playing sound, bark-bark! 🐶"))))))

      (h/command-fn "stop"
        (fn [{{id :id :as chat} :chat}]
          (player/terminate)
          (t/send-text token id "Stopped... 🙊")
          ; TODO: check, may be this auto calling after termination
          (reset! playing? false)))

      (h/command-fn "ping"
        (fn [{{id :id :as chat} :chat}]
          (t/send-text token id 
            (str "Bot started at: " startup-time ". 👌"))))

      (h/command-fn "kill"
        (fn [{{id :id :as chat} :chat}]
          (t/send-text token id (str "Bye-bye! 😢"))
          (System/exit 1))))

      ; TODO: Messages handling
      ; (h/message-fn
      ;   (fn [{{id :id} :chat :as message}]
      ;     (process-input process-message id message)))


    ; Starting emergency termination cycle
    (lifecycle/start-cycle #(timeout) (fn [] 
      (when 
        (lifecycle/terminate?) 
          (player/terminate))))

    ; Stating randomizer cycle
    (lifecycle/start-cycle #(* 1000 60 (rand-int 30)) (fn []
      (when (not (playing?))
        (reset! playing? true)
          (player/start 
            (player/read-sound "resources/dog_01.mp3") 
            #((reset! playing? false))))))

    (<!! (p/start token handler))))