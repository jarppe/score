(ns score.server
  (:require [score.web]
            [score.db :as db]
            [score.poll :as poll]
            [noir.server :as server])
  (:gen-class))

(def score-mode (keyword (or (System/getenv "SCORE_MODE") (System/getProperty "score.mode") "dev")))

(defn debug-requests [handler]
  (fn [request]
    (println "DEBUG:" (:request-method request) (:uri request))
    (handler request)))

(defn -main [& args]
  (db/init)
  (poll/start-poller)
  (server/add-middleware debug-requests)
  (server/start 8082 {:mode score-mode :ns 'score.web}))
