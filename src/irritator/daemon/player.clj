(ns irritator.daemon.player
  (:require [clojure.java.io :as io]
            [clj-audio.core :as audio]
            [irritator.utils.core :refer [ranged-rand]])
  (:gen-class))

(def samples-dir "./resources")

(def stopped? (atom true))

(def current-sample (atom nil))

(def last-sample (atom nil))

(defn playlist []
  (->>
   (io/file samples-dir)
   (file-seq)
   (map #(str (.getFileName (.toPath %))))
   (filter #(re-find #"\.mp3$" %))))

(defn playing? [] @current-sample)

(defn read-sound [path]
  (-> path
      (audio/->stream)
      (audio/decode)))

(defn play [path cb]
  (-> (read-sound path)
      (audio/play))
  (cb))

(defn get-random-sample []
  (let [samples (playlist)
        samples-count (count samples)]
    (if (= samples-count 0)
      (first samples)
      (->> samples
           (remove #(= % @last-sample))
           (rand-nth)))))

(defn play-random-sample [cb]
  (reset! current-sample (get-random-sample))
  (reset! last-sample @current-sample)
  (play
   (str samples-dir "/" @current-sample)
   (fn []
     (reset! current-sample nil)
     (cb))))

; TODO: refactor this
(defn configure [borders]
  (let [[from to] borders]

    (defn tick []
      (when (and (not @stopped?) (not @current-sample))
        (let [sleep-time (ranged-rand from to)
              sleep-minutes (* sleep-time 60 1000)]
          (play-random-sample (fn []
                                (Thread/sleep sleep-minutes)
                                (tick)))))))

  (defn start []
    (when @stopped? (do
                      (reset! stopped? false)
                      (tick))))

  (defn stop []
    (when (not @stopped?) (do
                            (reset! stopped? true)
                            (reset! current-sample nil)
                            (audio/stop)))))
