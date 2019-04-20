(ns irritator.daemon.processor
  (:require [irritator.daemon.player :as player]
            ; TODO: move this to the core
            [irritator.daemon.terminator :as terminator])
  (:gen-class))

(defn create-process-payload [id text]
  {:_id id
   :message text})

(defn process-help-message [msg-id cb]
  (cb
   (create-process-payload
    msg-id
    "Welcome to irritator! Type /start command to start show and /stop to finish. You can also subscribe on broadcasting with /subscribe command and unsubscribe with /unsubscribe.")))

(defn process-start-message [msg-id cb]
  (player/start)
  (cb
   (create-process-payload
    msg-id
    "Show is started, ladies and gentlemens! 🍿")))

(defn process-stop-message [msg-id cb]
  (player/stop)
  (cb
   (create-process-payload
    msg-id
    "Show is over, ladies and gentlemens! 🌒")))

(defn process-info-message [msg-id cb]
  (let [current-sample @player/current-sample
        cb-msg
        (if current-sample
          (str "Now playing: " current-sample)
          "Nothing is playing now. 🤷‍♀️")]
    (cb
     (create-process-payload msg-id cb-msg))))

(defn process-subscribe-message [msg-id cb]
  (cb
   (create-process-payload
    msg-id
    "You are successfully subscribed on irritator broadcasting! 🤝")))

(defn process-unsubscribe-message [msg-id cb]
  (cb
   (create-process-payload
    msg-id
    "You are successfully unsubscribed from irritator broadcasting, I will not send anything to you. 🙊")))

(defn process-kill-message [msg-id cb]
  (cb
   (create-process-payload
    msg-id
    "Bot was terminated. If you want to launch it again – restart daemon and bot. Good luck! ✨"))
  (System/exit 1))

(defn process-termination [msg-id cb]
  (cb
    (create-process-payload
    msg-id
    "It is not good time to play something. 🤫")))

(defn process-message [msg cb]
  (let [{id :_id command :command} msg]
    (if (and (terminator/terminate?) (not (= command "kill")))
        (process-termination id cb)
        (case command
          "help" (process-help-message id cb)
          "start" (process-start-message id cb)
          "stop" (process-stop-message id cb)
          "info" (process-info-message id cb)
          "subscribe" (process-subscribe-message id cb)
          "unsubscribe" (process-unsubscribe-message id cb)
          "kill" (process-kill-message id cb)))))
