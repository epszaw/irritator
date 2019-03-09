(ns irritator.lifecycle
  (:require [clj-time.core :as t]
            [clj-time.local :as l])
  (:gen-class))

(defn tick [cb] (
  (when (or
    (t/before? (t/today-at 7 00) (l/local-now))
    (t/after? (t/today-at 21 45) (l/local-now)))
      (cb))))

(defn start [timeout cb]
  (while true 
    (do 
      (Thread/sleep timeout) 
      (cb))))
