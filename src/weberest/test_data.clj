(ns weberest.test-data
  (:use [datomic.api :only [q db] :as d]) 
  (:require [clj-time.core :as ctc]
            [weberest 
             [datomic :as bd]
             [core :as bc]
             [util :as bu]]))

;add 1030/1/0 F-MAIS data
(defn add-maize [datomic-connection]
  (let [dc-to-day (bd/create-entities :kv/dc :kv/rel-dc-day 
                                   [20 1, 30 15, 40 25, 50 50, 60 70, 70 80, 80 120, 90 150])
      dc-to-name (bd/create-entities :kv/dc :kv/name 
                                  [20 "Aussaat", 30 "Aufgang", 40 "4-Blattstadium", 50 "8-Blattstadium", 
                                   60 "Fahnenschieben", 70 "Bluete", 80 "Milchreife", 90 "Erntereife"])
      rel-day-to-cover-degree (bd/create-entities :kv/rel-dc-day :kv/cover-degree 
                                               [15 0.0, 80 1.0])
      rel-day-to-extraction-depth (bd/create-entities :kv/rel-dc-day :kv/extraction-depth 
                                                   [15 20, 80 90, 90 100]) 
      rel-day-to-transpiration-factor (bd/create-entities :kv/rel-dc-day :kv/transpiration-factor 
                                                       [60 1.0, 85 1.2, 140 1.2, 170 0.1]) 
      rel-day-to-quotient (bd/create-entities :kv/rel-dc-day :kv/quotient-aet-pet
                                           [40 0.0, 60 0.6, 90 0.9, 115 0.9, 140 0.7, 150 0.0])
      
      crop {:db/id (bd/new-entity-id) 
            ;:db/ident :crop.id/id-1030-1-0
            :crop/id "1030/1/0" 
            :crop/number 1030 
            :crop/cultivation-type 1 
            :crop/usage 0 
            :crop/name "Mais Gr+S" 
            :crop/symbol "F-MAIS"
            :crop/dc-to-rel-dc-days (bd/get-entity-ids dc-to-day) 
            :crop/dc-to-developmental-state-names (bd/get-entity-ids dc-to-name)
            :crop/rel-dc-day-to-cover-degrees (bd/get-entity-ids rel-day-to-cover-degree) 
            :crop/rel-dc-day-to-extraction-depths (bd/get-entity-ids rel-day-to-extraction-depth)
            :crop/rel-dc-day-to-transpiration-factors (bd/get-entity-ids rel-day-to-transpiration-factor)
            :crop/rel-dc-day-to-quotient-aet-pets (bd/get-entity-ids rel-day-to-quotient)
            :crop/effectivity-quotient 0.13}]
      
  (d/transact datomic-connection
              ;print
              (flatten [dc-to-day
                        dc-to-name
                        rel-day-to-cover-degree
                        rel-day-to-extraction-depth
                        rel-day-to-transpiration-factor
                        rel-day-to-quotient
                        crop]))))



;add 0703/1/0 KART3 data
(defn add-potato [datomic-connection]
  (let [dc-to-day (bd/create-entities :kv/dc :kv/rel-dc-day 
                                   [1 1, 5 35, 10 50, 15 70, 17 85, 28 115, 31 125])
        dc-to-name (bd/create-entities :kv/dc :kv/name 
                                    [1 "Pflanzung", 5 "Auflaufen", 10 "Sichtbarwerden der 1. Knospen", 15 "Blühbeginn", 
                                     17 "Blühende", 28 "Braunreife", 31 "Krautabtötung Erntebeginn"])
        rel-day-to-cover-degree (bd/create-entities :kv/rel-dc-day :kv/cover-degree 
                                                 [35 0.0, 65 1.0, 120 1.0, 135 0.0])
        rel-day-to-extraction-depth (bd/create-entities :kv/rel-dc-day :kv/extraction-depth 
                                                     [35 20, 50 50, 60 70]) 
        rel-day-to-transpiration-factor (bd/create-entities :kv/rel-dc-day :kv/transpiration-factor 
                                                         [50 1.0, 70 1.2, 115 1.2, 120 1.0]) 
        rel-day-to-quotient (bd/create-entities :kv/rel-dc-day :kv/quotient-aet-pet
                                             [35 0.0, 50 0.2, 55 0.7, 85 0.8, 105 0.8, 110 0.7, 115 0.0])
        
        crop {:db/id (bd/new-entity-id)
              ;:db/ident :crop.id/id-0703-1-0
              :crop/id "0703/1/0"
              :crop/number 703 
              :crop/cultivation-type 1 
              :crop/usage 0 
              :crop/name "Kartoffeln" 
              :crop/symbol "KART3"
              :crop/dc-to-rel-dc-days (bd/get-entity-ids dc-to-day) 
              :crop/dc-to-developmental-state-names (bd/get-entity-ids dc-to-name)
              :crop/rel-dc-day-to-cover-degrees (bd/get-entity-ids rel-day-to-cover-degree) 
              :crop/rel-dc-day-to-extraction-depths (bd/get-entity-ids rel-day-to-extraction-depth)
              :crop/rel-dc-day-to-transpiration-factors (bd/get-entity-ids rel-day-to-transpiration-factor)
              :crop/rel-dc-day-to-quotient-aet-pets (bd/get-entity-ids rel-day-to-quotient)
              :crop/effectivity-quotient 0.1}]
    
    (d/transact datomic-connection
                ;print
                (flatten [dc-to-day
                          dc-to-name
                          rel-day-to-cover-degree
                          rel-day-to-extraction-depth
                          rel-day-to-transpiration-factor
                          rel-day-to-quotient
                          crop]))))

;add 0101/1/0 WW data
(defn add-winter-wheat [datomic-connection]
  (let [dc-to-day (bd/create-entities :kv/dc :kv/rel-dc-day 
                                   [21 60, 31 128, 51 158, 61 165, 75 180, 92 215])
        dc-to-name (bd/create-entities :kv/dc :kv/name 
                                    [21 "Bestockungsbeginn", 31 "Schossbeginn", 51 "Ährenschieben", 61 "Blüte", 
                                     75 "Milchreife", 92 "Todreife"])
        rel-day-to-cover-degree (bd/create-entities :kv/dc :kv/cover-degree 
                                                 [30 0.6, 115 0.8, 125 1.0])
        rel-day-to-extraction-depth (bd/create-entities :kv/rel-dc-day :kv/extraction-depth 
                                                     [90 60, 120 80, 150 120, 180 180]) 
        rel-day-to-transpiration-factor (bd/create-entities :kv/rel-dc-day :kv/transpiration-factor 
                                                         [118 1.0, 128 1.2, 180 1.6, 210 1.0, 220 0.5]) 
        rel-day-to-quotient (bd/create-entities :kv/rel-dc-day :kv/quotient-aet-pet
                                             [75 0.0, 90 0.2, 128 0.8, 180 0.8, 200 0.6, 210 0.0])
        
        crop {:db/id (bd/new-entity-id) 
              ;:db/ident :crop.id/id-0101-1-0
              :crop/id "0101/1/0" 
              :crop/number 101 
              :crop/cultivation-type 1 
              :crop/usage 0 
              :crop/name "Winterweizen/EJ" 
              :crop/symbol "WW"
              :crop/dc-to-rel-dc-days (bd/get-entity-ids dc-to-day) 
              :crop/dc-to-developmental-state-names (bd/get-entity-ids dc-to-name)
              :crop/rel-dc-day-to-cover-degrees (bd/get-entity-ids rel-day-to-cover-degree) 
              :crop/rel-dc-day-to-extraction-depths (bd/get-entity-ids rel-day-to-extraction-depth)
              :crop/rel-dc-day-to-transpiration-factors (bd/get-entity-ids rel-day-to-transpiration-factor)
              :crop/rel-dc-day-to-quotient-aet-pets (bd/get-entity-ids rel-day-to-quotient)
              :crop/effectivity-quotient 0.17}]
    
    (d/transact datomic-connection
                ;print
                (flatten [dc-to-day
                          dc-to-name
                          rel-day-to-cover-degree
                          rel-day-to-extraction-depth
                          rel-day-to-transpiration-factor
                          rel-day-to-quotient
                          crop]))))

;add 0000/1/0 Brache data
(defn add-fallow [datomic-connection]
  (let [dc-to-day (bd/create-entities :kv/dc :kv/rel-dc-day [1 1])
        dc-to-name (bd/create-entities :kv/dc :kv/name [1 "Brache"])
        rel-day-to-cover-degree (bd/create-entities :kv/rel-dc-day :kv/cover-degree [1 0.0])
        rel-day-to-extraction-depth (bd/create-entities :kv/rel-dc-day :kv/extraction-depth [1 1]) 
        rel-day-to-transpiration-factor (bd/create-entities :kv/rel-dc-day :kv/transpiration-factor [1 1.0]) 
        rel-day-to-quotient (bd/create-entities :kv/rel-dc-day :kv/quotient-aet-pet [1 0.0])
        
        fallow {:db/id (bd/new-entity-id) 
                ;:db/ident :crop.id/id-0000-1-0
                :crop/id "0000/1/0" 
                :crop/number 0 
                :crop/cultivation-type 1 
                :crop/usage 0 
                :crop/name "Brache" 
                :crop/symbol "BRACHE"
                :crop/dc-to-rel-dc-days (bd/get-entity-ids dc-to-day) 
                :crop/dc-to-developmental-state-names (bd/get-entity-ids dc-to-name)
                :crop/rel-dc-day-to-cover-degrees (bd/get-entity-ids rel-day-to-cover-degree) 
                :crop/rel-dc-day-to-extraction-depths (bd/get-entity-ids rel-day-to-extraction-depth)
                :crop/rel-dc-day-to-transpiration-factors (bd/get-entity-ids rel-day-to-transpiration-factor)
                :crop/rel-dc-day-to-quotient-aet-pets (bd/get-entity-ids rel-day-to-quotient)
                :crop/effectivity-quotient 0.0}]
    
    (d/transact datomic-connection
                ;print
                (flatten [dc-to-day
                          dc-to-name
                          rel-day-to-cover-degree
                          rel-day-to-extraction-depth
                          rel-day-to-transpiration-factor
                          rel-day-to-quotient
                          fallow]))))

;add plot 0400
(defn add-plot [datomic-connection] 
  (let [fcs (bd/create-entities {:soil/upper-boundary-depth [30 60 150]
                              :soil/field-capacity [17.7 13.7 15.7]})
        
        pwps (bd/create-entities {:soil/upper-boundary-depth [30 60 150]
                               :soil/permanent-wilting-point [3.4 2.9 3.8]})
        
        sms (bd/create-entities {:soil/upper-boundary-depth [30 60 90 150] 
                              :soil/soil-moisture [80. 90. 100. 100.]})
        
        technology {:db/id (bd/new-entity-id)
                    :technology/cycle-days 4
                    :technology/min-donation 5
                    :technology/max-donation 30
                    :technology/opt-donation 20
                    :technology/donation-step-size 5}
        
        dc-assertion-1 {:db/id (bd/new-entity-id)
                        :assertion/in-year 2012
                        :assertion/at-abs-day (bu/date-to-doy 18 4)
                        :assertion/assert-dc 1
                        :assertion/abs-assert-dc-day (bu/date-to-doy 18 4)}
        
        dc-assertion-2 {:db/id (bd/new-entity-id)
                        :assertion/in-year 2012
                        :assertion/at-abs-day (bu/date-to-doy 28 5)
                        :assertion/assert-dc 10
                        :assertion/abs-assert-dc-day (bu/date-to-doy 28 5)}
        
        instance-703 {:db/id (bd/new-entity-id)
                      :crop-instance/template (bd/unique-query-for-db-id :crop/id "0703/1/0") 
                      :crop-instance/name "Kartoffel - 703/1/0"
                      :crop-instance/dc-assertions (bd/get-entity-ids [dc-assertion-1 dc-assertion-2])}
        
        dc-assertion-3 {:db/id (bd/new-entity-id)
                        :assertion/in-year 2012
                        :assertion/at-abs-day (bu/date-to-doy 28 8)
                        :assertion/assert-dc 20
                        :assertion/abs-assert-dc-day (bu/date-to-doy 28 8)}
        
        instance-1030 {:db/id (bd/new-entity-id)
                       :crop-instance/template (bd/unique-query-for-db-id :crop/id "1030/1/0") 
                       :crop-instance/name "Mais - 1030/1/0"
                       :crop-instance/dc-assertions [(bd/get-entity-id dc-assertion-3)]}
        
        
        plot {:db/id (bd/new-entity-id)
              ;:db/ident :plot.id/id-0400 
              :plot/number "0400" 
              :plot/crop-area 1.0 
              :plot/irrigation-area 1.0 
              :plot/stt 6212 
              :plot/slope 1
              :plot/field-capacities (bd/get-entity-ids fcs)
              :plot/fc-unit :soil-moisture-unit/volP
              :plot/permanent-wilting-points (bd/get-entity-ids pwps)
              :plot/pwp-unit :soil-moisture-unit/volP
              :plot/groundwaterlevel 300
              :plot/damage-compaction-depth 300
              :plot/damage-compaction-area 0.0
              :plot/abs-day-of-initial-soil-moisture-measurement (bu/date-to-doy 31 3)
              :plot/initial-soil-moistures (bd/get-entity-ids sms)
              :plot/initial-sm-unit :soil-moisture-unit/pFK 
              :plot/technology (bd/get-entity-id technology)
              :plot/crop-instances (bd/get-entity-ids [instance-703 instance-1030])}]
    
    (d/transact datomic-connection
                ;print
                (flatten [fcs
                          pwps
                          sms
                          technology
                          dc-assertion-1
                          dc-assertion-2
                          instance-703
                          dc-assertion-3
                          instance-1030
                          plot]))))
