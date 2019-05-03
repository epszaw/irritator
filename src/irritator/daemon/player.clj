(ns irritator.daemon.player
  (:require [clojure.java.io :as io]
            [clj-audio.core :as audio]
            [irritator.daemon.state :as s :refer [state]]
            [irritator.utils.core :refer [ranged-rand]])
  (:gen-class))

(defn configure [samples-dir borders]
  (let [[from to] borders]

    (defn playlist []
      (->>
       (io/file samples-dir)
       (file-seq)
       (map #(str (.getFileName (.toPath %))))
       (filter #(re-find #"\.mp3$" %))))

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
               (remove #(= % (:last-sample @state)))
               (rand-nth)))))

    (defn play-random-sample [cb]
      (let [random-sample (get-random-sample)]
        (s/mutate-state :current-sample random-sample)
        (s/mutate-state :last-sample random-sample)
        (play
         (str samples-dir "/" (:current-sample @state))
         (fn []
           (s/mutate-state :current-sample nil)
           (cb)))))

    (defn tick []
      (let [playing? (:playing? @state)
            current-sample (:current-sample @state)
            allowed? (and playing? (nil? current-sample))
            sleep-time (ranged-rand from to)
            sleep-minutes (* sleep-time 60 1000)]

        (when allowed?
          (play-random-sample
           (fn []
             (Thread/sleep sleep-minutes)
             (tick)))))))

  (defn start []
    (do
      (s/mutate-state :playing? true)
      (tick)))

  (defn stop []
    (do
      (s/mutate-state :playing? false)
      (s/mutate-state :current-sample nil)
      (audio/stop))))
