(ns score.poll
  (:require [score.db :as db]
            [score.core :as core]))

(defn poll [interval f]
  (future
    (try
      (while true
        (f)
        (Thread/sleep interval))
      (catch Exception e
        (.printStackTrace e)))))

(defn update-score []
  (db/push (core/get-users-scores)))

(defn start-poller []
  (poll (* 5 60 1000) update-score))