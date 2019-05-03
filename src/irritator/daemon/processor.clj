(ns irritator.daemon.processor
  (:require [clojure.core.async :refer [go]]
            [irritator.daemon.player :as player]
            [irritator.daemon.terminator :as terminator]
            [irritator.daemon.state :as s :refer [state]])
  (:gen-class))

(defn create-process-payload [id text broadcast?]
  {:_id id
   :message text
   :broadcast? (true? broadcast?)})

(defn process-help-message [msg-id cb]
  (cb
   (create-process-payload
    msg-id
    "Welcome to irritator! Type /start command to start show and /stop to finish. You can also subscribe on broadcasting with /subscribe command and unsubscribe with /unsubscribe."
    false)))

(defn process-start-message [msg-id broadcasting? cb]
  (s/mutate-state :suspended? false)
  (go (player/start))
  (cb
   (create-process-payload
    msg-id
    "Show is started, ladies and gentlemens! 🍿"
    broadcasting?)))

(defn process-stop-message [msg-id broadcasting? cb]
  (s/mutate-state :suspended? true)
  (player/stop)
  (cb
   (create-process-payload
    msg-id
    "Show is over, ladies and gentlemens! 🌒"
    broadcasting?)))

(defn process-info-message [msg-id broadcasting? cb]
  (let [current-sample (:current-sample @state)
        cb-msg
        (if current-sample
          (str "Now playing: " current-sample)
          "Nothing is playing now. 🤷‍♀️")]
    (cb
     (create-process-payload 
      msg-id 
      cb-msg 
      broadcasting?))))

(defn process-subscribe-message [msg-id broadcasting? cb]
  (cb
   (create-process-payload
    msg-id
    "You are successfully subscribed on irritator broadcasting! 🤝"
    broadcasting?)))

(defn process-unsubscribe-message [msg-id broadcasting? cb]
  (cb
   (create-process-payload
    msg-id
    "You are successfully unsubscribed from irritator broadcasting, I will not send anything to you. 🙊"
    broadcasting?)))

(defn process-kill-message [msg-id broadcasting? cb]
  (cb
   (create-process-payload
    msg-id
    "Bot was terminated. If you want to launch it again – restart daemon and bot. Good luck! ✨"
    broadcasting?))
  (System/exit 1))

(defn process-ignored-message [msg cb]
  (let [{id :_id} msg]
    (cb
      (create-process-payload
       id
       "It is not good time to play something. 🤫"
       false))))

(defn process-message [msg cb]
  (let [{id :_id command :command} msg]
    (if (and (terminator/terminate?) (not (= command "kill")))
        (process-ignored-message msg cb)
        (case command
              "help" (process-help-message id false cb)
              "start" (process-start-message id true cb)
              "stop" (process-stop-message id true cb)
              "info" (process-info-message id false cb)
              "subscribe" (process-subscribe-message id false cb)
              "unsubscribe" (process-unsubscribe-message id false cb)
              "kill" (process-kill-message id true cb)))))
