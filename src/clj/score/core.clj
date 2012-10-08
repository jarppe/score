(ns score.core
  (:require [score.db :as db]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clj-http.client :as client]
            [net.cgrand.enlive-html :as enlive]))

(defn get-user-page [user]
  (enlive/html-resource (:body (client/get (str "https://www.4clojure.com/user/" user) {:insecure? true :as :stream}))))

(defn parse-progress [value]
  "parses \"value: 42%\" -> 42"
  (let [value (.substring value 7)]
    (Integer/parseInt (.substring value 0 (dec (count value))))))

(defn get-user-score [user]
  (let [page (get-user-page user)
        total (Integer/parseInt (first (s/split (first (:content (last (enlive/select page [:td.count-value])))) #"/")))
        progress (map #(-> % :attrs :style parse-progress) (enlive/select page [:.progress-bar]))]
    [total progress]))

(defn get-users-scores-async []
  (zipmap users (map #(future (get-user-score (:name %))) (db/get-users))))

(defn get-users-scores []
  (into {} (map (fn [[k v]] [k (deref v)]) (get-users-scores-async))))

(defn events-to-score [e]
  ; TODO
  e)

(comment
  (doseq [[user score] (get-users-scores)]
    (println user ": " score)))
