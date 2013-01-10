(ns berest.datomic
  (:require clojure.set
            [clojure.string :as cstr]
            [clojure.pprint :as pp]
            [clj-time.core :as ctc])
  (:use [datomic.api :as d :only [q db]]))

#_(def berest-datomic-uri "datomic:free://localhost:4334/berest")
(def berest-datomic-uri "datomic:free://humane-spaces.cloudapp.net:4334/berest")
(defn connection [] (d/connect berest-datomic-uri)) 
(def ^:dynamic *db* (db (connection)))

(defn new-entity-ids [] (repeatedly #(d/tempid :db.part/user)))
(defn new-entity-id [] (first (new-entity-ids)))

(defn create-entities 
  ([key value kvs]
    (map (fn [id [k v]] {:db/id id, key k, value v}) (new-entity-ids) (apply array-map kvs)))
  ([ks-to-vss]
    (map #(assoc (zipmap (keys ks-to-vss) %) :db/id (new-entity-id)) 
         (apply map vector (vals ks-to-vss)))))

(defn get-entity-ids [entities] (map :db/id entities))
(defn get-entity-id [entity] (:db/id entity))

(defn get-entities [entity-ids]
  (map (partial d/entity *db*) entity-ids)
  #_(map (partial datomic.db/get-entity *db*) entity-ids))
(defn get-entity [entity-id]
  (first (get-entities [entity-id])))

(defn create-map-from-entity-ids 
  [key value entity-ids]
  (->> entity-ids 
    get-entities
    (map (juxt key value) ,,,) 
    (into (sorted-map) ,,,)))

(defn create-map-from-entities 
  [key value entities]
  (->> entities 
    (map (juxt key value) ,,,) 
    (into (sorted-map) ,,,)))

(defn query-for-db-id [relation value]
  (->> (q '[:find ?db-id 
            :in $ ?r ?v  
            :where 
            [?db-id ?r ?v]]
         *db*, relation, value)
    (map first)))

(defn unique-query-for-db-id [relation value]
  (first (query-for-db-id relation value)))

