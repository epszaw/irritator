(ns irritator.storage.core
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:gen-class))

(def connection (mg/connect))

(def db (mg/get-db connection "irritator"))