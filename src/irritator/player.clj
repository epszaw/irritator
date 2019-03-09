(ns irritator.player
  (:require [clj-audio.core :refer :all])
  (:gen-class))

(defn start [path]
  (future (-> (->stream path) decode play)))

(defn terminate []
  (when finished? stop))