(ns irritator.daemon.state
  (:gen-class))

(def state (atom {:suspended? true
                  :playing? false
                  :processing? false
                  :current-sample nil
                  :last-sample nil}))

(defn mutate-state 
  "Mutates state atom by given key with given value"
  [key value]
  (reset! state (assoc @state key value)))
