(ns clj-postgresql.pool
  "BoneCP based connection pool"
  (:require [clojure.java.data :as data])
  (:import (com.jolbox.bonecp BoneCPDataSource BoneCPConfig ConnectionHandle)
           (com.jolbox.bonecp.hooks AbstractConnectionHook)
           (java.util.concurrent TimeUnit)))

(defn db-spec->pool-config
  "Converts a db-spec with :host :port :dbname and :user to bonecp pool config"
  [{:keys [dbtype host port dbname user password]}]
  (let [host-part (when host (if port (format "%s:%s" host port) host))
        pool-conf {:jdbcUrl (format "jdbc:%s://%s/%s" dbtype (or host-part "") dbname)
                   :username user}]
    (if password
      (assoc pool-conf :password password)
      pool-conf)))

(defn pooled-db
  [spec opts]
  (let [config (merge (db-spec->pool-config spec) opts)]
    {:datasource (data/to-java BoneCPDataSource config)}))

(defn close-pooled-db!
  [{:keys [datasource]}]
  (.close ^BoneCPDataSource datasource))
