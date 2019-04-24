(ns irritator.daemon.terminator
  (:require [clj-time.core :as t]
            [clj-time.local :as l]
            [irritator.utils.core :refer [interval]])
  (:gen-class))

(defn configure [{from :from to :to}]

  (defn terminate? []
    (let [local-time (t/plus (l/local-now) (t/hours 3))
          local-from (apply t/today-at from)
          local-to (apply t/today-at to)]
      (or
       (t/after? local-time local-to)
       (t/before? local-time local-from)))))
