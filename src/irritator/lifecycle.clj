(ns irritator.lifecycle
  (:require [clj-time.core :as t]
            [clj-time.local :as l])
  (:gen-class))

; TODO move to utils, or replace with non-crutch
; because now it works only in GMT +3 timezone 🤷‍♀️
(defn local-time [] 
  (t/plus (l/local-now) (t/hours 3)))

(defn terminate? [] 
  (or
    (t/before? (local-time) (t/today-at 7 00))
    (t/after? (local-time) (t/today-at 21 45))))

(defn start [timeout cb]
  (while true 
    (do 
      (Thread/sleep timeout) 
      (cb))))
