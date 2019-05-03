(ns zx
  (:require [integrant.core :as ig]
            [zx.literals :as lit]
            [clojure.edn :as edn]))

(defprotocol Executor
  (-start
    [this])
  (-stop
    [this server]))

(defn start
  [executor]
  (-start executor))

(defn stop
  [executor server]
  (-stop executor server))

(defn get-system
  [config]
  (-> config
      ig/prep
      ig/init))

(defn read-config
  [path]
  (->> (slurp path)
       (ig/read-string {:eof nil :readers *data-readers*})))
