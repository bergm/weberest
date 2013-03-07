;; make in memory database
(use '[datomic.api :only [q db] :as d])
(require '[clj-time.core :as ctc]
         '[weberest.datomic :as bd]
         '[weberest.util :as bu])

;get connection
(def con (bd/datomic-connection "berest"))

;get db value
(def db (bd/current-db "berest"))


#_(alter-var-root (var *db*) (constantly (db datomic-connection)))

;create db with schema
(bd/create-db "berest")

;delete db
(bd/delete-db "berest") 





(comment 
  ;; find all matches corresponding to read-1
(q '[:find ?match ?doc
     :where
     [?read :db/doc "read-1"]
     [?read :read/match ?match]
     [?match :db/doc ?doc]]
   (db datomic-connection))

;; find all reads corresponding to match-b
(q '[:find ?read ?doc
     :where
     [?read :db/doc ?doc]
     [?read :read/match ?match]
     [?match :db/doc "match-b"]]
   (db datomic-connection))
  
  ) 

