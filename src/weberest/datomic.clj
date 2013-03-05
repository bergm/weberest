(ns weberest.datomic
  (:require clojure.set
            [weberest.util :as bu]
            [clojure.string :as cstr]
            [clojure.pprint :as pp]
            [clj-time.core :as ctc])
  (:use [datomic.api :as d :only [q db]]
        [clojure.core.incubator :only [-?> -?>>]]))

#_(def datomic-base-uri "datomic:free://localhost:4334/")
(def datomic-base-uri "datomic:free://humane-spaces.cloudapp.net:4334/")
#_(def berest-datomic-uri (str datomic-base-uri "berest"))
#_(defn connection [] (d/connect berest-datomic-uri)) 
#_(def ^:dynamic *db* (db (connection)))

(defn current-db [db-id]
  (-?>> db-id
        (str datomic-base-uri ,,,)
        d/connect
        d/db))

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

(defn get-entities [db entity-ids]
  (map (partial d/entity db) entity-ids)
  #_(map (partial datomic.db/get-entity db) entity-ids))
(defn get-entity [db entity-id]
  (first (get-entities db [entity-id])))

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

(defn query-for-db-id [db relation value]
  (->> (q '[:find ?db-id 
            :in $ ?r ?v  
            :where 
            [?db-id ?r ?v]]
         db, relation, value)
    (map first)))

(defn unique-query-for-db-id [db relation value]
  (first (query-for-db-id db relation value)))

(defn create-dc-assertion* 
  "Create a dc assertion for given year 'in-year' to define that at abs-dc-day
  the dc-state was 'dc'. Optionally a at-abs-day can be given when the 
  dc state had been told the system, else abs-dc-day will be assumed."
  [in-year abs-dc-day dc & [at-abs-day]]
  {:db/id (new-entity-id)
   :assertion/in-year in-year
   :assertion/at-abs-day (or at-abs-day abs-dc-day)
   :assertion/assert-dc dc
   :assertion/abs-assert-dc-day abs-dc-day})

(defn create-dc-assertion 
  "Create a dc assertion for given year 'in-year' to define that at '[day month]'
  the dc-state was 'dc'. Optionally a '[at-day at-month]' can be given when the 
  dc state had been told the system, else '[day month]' will be assumed"
  [in-year [day month] dc & [[at-day at-month :as at]]]
  (let [abs-dc-day (bu/date-to-doy day month in-year)
        at-abs-day (if (not-any? nil? at)
                       (bu/date-to-doy at-day at-month in-year)
                       abs-dc-day)]
       (create-dc-assertion* in-year abs-dc-day dc at-abs-day)))
  
