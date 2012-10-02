(ns score.db
  (:require [clojure.java.jdbc :as sql])
  (:import [com.jolbox.bonecp BoneCPConfig BoneCPDataSource]))

;;
;; Init:
;;

(Class/forName "org.h2.Driver")

(def db (atom nil))

(defn make-pool []
  (BoneCPDataSource.
    (doto (BoneCPConfig.)
      (.setJdbcUrl "jdbc:h2:./score")
      (.setUsername "sa")
      (.setPassword "")
      (.setMaxConnectionsPerPartition 10)
      (.setMinConnectionsPerPartition 2))))

(defn init []
  (swap! db (constantly {:datasource (make-pool) :subprotocol "h2"}))
  (try
  (sql/with-connection @db
    (sql/create-table :events
      [:id       "identity" "not null" "primary key"]
      [:created  "timestamp" "not null" "default current_timestamp"]
      [:data     "varchar(512)" "not null"])
    (sql/do-commands "create index events_ndx on events(created)")
    (sql/create-table :users
      [:name     "varchar(32)" "not null" "primary key"]
      [:gravatar "varchar(128)"]
      [:created  "timestamp" "not null" "default current_timestamp"])
    (println "Database successfully initialized"))
  (catch java.sql.SQLException e
    (if (not (.contains (.getMessage e) "Table \"EVENTS\" already exists")) (throw e))
    (println "Database already initialized"))))

;;
;; Score events:
;;

(defn push [event]
  (sql/with-connection @db
    (sql/transaction
      (sql/insert-records :events {:data (pr-str event)}))))

(defn load-events [since]
  (sql/with-connection @db
    (sql/with-query-results rows ["select id, created, data from events where created > ? order by created asc" since]
      (doall (map (fn [row] (assoc row :data (read-string (:data row)))) rows)))))

(defn load-last []
  (sql/with-connection @db
    (sql/with-query-results rows ["select id, created, data from events order by created desc limit 1"]
      (first (doall (map (fn [row] (assoc row :data (read-string (:data row)))) rows))))))

;;
;; Users:
;;

(defn add-user [name]
  (try
    (sql/with-connection @db
      (sql/transaction
        (sql/insert-records :users {:name name})))
    (catch java.sql.SQLException e
      (if (not= (.getErrorCode e) 23505)
        (do
          (println "Unexpected SQL error")
          (.printStrackTrace e)
          (throw e))
        nil))))

(defn delete-user [name]
  (sql/with-connection @db
    (sql/delete-rows :users ["name=?" name])))

(defn set-user-gravatar [name url]
  (sql/with-connection @db
    (sql/transaction
      (sql/update-values :users ["name=?" name] {:gravatar url}))))

(defn get-users []
  (sql/with-connection @db
    (sql/with-query-results rows ["select name, created, gravatar from users"]
      (doall rows))))

(defn get-user [name]
  (sql/with-connection @db
    (sql/with-query-results rows ["select name, created, gravatar from users where name = ?" name]
      (first (doall rows)))))

;;
;;
;;

(comment
  (sql/with-connection @db (sql/drop-table :events))
  (push {:type "fzz" :user "bzz"})
  (load-events
    (java.util.Date. (- (System/currentTimeMillis) (* 6 60 60 1000)))
    (fn [es] (doseq [e es] (prn e)))))
