(ns irritator.utils.core
  (:require [clojure.core :refer [future]]
            [clojure.string :as str]
            [ring.util.codec :refer [url-decode]]
            [monger.conversion :refer [from-db-object]]
            [cheshire.core :as cheshire])
  (:gen-class))

(defn normalize-doc [doc]
  (let [converted-doc (from-db-object doc true)]
    (assoc converted-doc :_id (str (:_id converted-doc)))))

(defn get-request-body [req]
  (-> req
      (:body)
      (slurp)
      (cheshire/parse-string keyword)
      (:payload)))

(defn interval [timeout cb]
  (while true (do
                (cb)
                (Thread/sleep timeout))))

(defn qs-to-hash [qs]
  (->> (str/split qs #"&")
       (map #(str/split % #"="))
       (reduce (fn [acc [key value]] (assoc acc (keyword key) value)) {})))

(defn decode-escaped-string [str]
  (-> str
      (url-decode)
      (str/replace #"\+" " ")))

(defn ranged-rand [start end]
  (->>
   (- end start)
   (rand)
   (long)
   (+ start)))
