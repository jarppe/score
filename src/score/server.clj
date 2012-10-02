(ns score.server
  (:require [score.web]
            [score.db :as db]
            [score.poll :as poll]
            [noir.server :as server])
  (:gen-class))

(def score-mode (keyword (or (System/getenv "SCORE_MODE") (System/getProperty "score.mode") "dev")))

(defn -main [& args]
  (db/init)
  (poll/start-poller)
  (server/start 8082 {:mode score-mode :ns 'score.web}))
