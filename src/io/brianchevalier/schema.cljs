(ns io.brianchevalier.schema
  (:require
   [clojure.spec.alpha :as spec]))

(spec/def ::month #{:jan :feb :mar :apr :may :jun :jul :aug :sept :oct :nov :dec})
(spec/def ::day #{1 15 30})
(spec/def ::time (spec/tuple ::month ::day))
(spec/def ::period (spec/tuple ::time ::time))
(spec/def ::periods (spec/coll-of ::period))

(spec/def :plant/name string?)
(spec/def :plant/type #{:flower :herb :vegetable :tree})
(spec/def :plant/lifecycle #{:annual :perennial :biennial})

(spec/def :plant/sow-indoor ::periods)
(spec/def :plant/sow-outdoor ::periods)
(spec/def :plant/transplant ::periods)
(spec/def :plant/bloom ::periods)
(spec/def :plant/harvest ::periods)
(spec/def :plant/fertilize ::periods)

(spec/def :plant/plant
  (spec/keys
   :req [:plant/name]
   :opt [:plant/type
         :plant/lifecycle
         :plant/sow-indoor
         :plant/sow-outdoor
         :plant/transplant
         :plant/bloom
         :plant/harvest
         :plant/fertilize]))

(spec/def :plant/plants
  (spec/coll-of :plant/plant))

(spec/def :plant/db
  (spec/keys :req [:plant/plants]))
