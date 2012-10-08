(ns score.web
  (:use [noir.core :only [defpage]])
  (:require [score.core :as score]
            [score.db :as db]
            [noir.request :as request]
            [noir.response :as resp]
            [cheshire.core :as json]))

(defn to-date [since]
  (java.util.Date. (Long/parseLong (or since "0"))))

(def score-context (or (System/getenv "SCORE_CTX") (System/getProperty "score.ctx") "/"))

(defpage "/" []
  (resp/redirect (str score-context "score.html")))

(defpage "/rest/scores" {since :since}
  (resp/json (score/events-to-score (db/load-events (to-date since)))))

(defpage "/rest/score" []
  (resp/json (db/load-last)))

(defpage "/rest/users" []
  (resp/json (db/get-users)))

(defpage [:get "/rest/user/:name"] {name :name}
  (resp/json (db/get-user name)))

(defpage [:post "/rest/user/:name"] {name :name}
  (when (db/add-user name)
    (resp/json {:ok true})))

(defpage [:delete "/rest/user/:name"] {name :name}
  (db/delete-user name)
  (resp/json {:ok true}))
