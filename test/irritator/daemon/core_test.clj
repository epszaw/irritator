(ns irritator.daemon.core-test
  (:require [clojure.test :refer :all]
            [spy.core :as spy] 
            [irritator.daemon.core :as core]))

(deftest process-next-message-response
  (testing "Should process message if it passed"
    (with-redefs [core/])
    (is (= 0 0)))

  (testing "Should not do anything without message"
    (is (= 0 0))))
