(ns irritator.daemon.player
  (:require [clojure.java.io :as io]
            [clj-audio.core :as audio])  
  (:gen-class))

(def current-sample (atom nil))

(def samples-dir "./resources")

(defn playing? [] @current-sample)

(defn read-sound [path]
  (-> path
    (audio/->stream)
    (audio/decode)))

; TODO: return callback as second arg
(defn play [path]
  (future 
    (->
      (read-sound path)
      (audio/play))))

; (defn playlist []
;   (->>
;     (.list (io/file samples-dir))
;     (map #(str samples-dir "%s"))))

; (defn play-random-sample [cb]
;   (reset! current-sample (rand-nth (playlist)))
;   (play @current-sample cb))

(defn stop [] (audio/stop))