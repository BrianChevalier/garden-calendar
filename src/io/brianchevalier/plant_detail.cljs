(ns io.brianchevalier.plant-detail
  (:refer-clojure :exclude [min max range])
  (:require
   [clojure.string :as str]
   [uix.core :as uix :refer [defui $]]))

(defn range
  [{:keys [min max]}]
  (->> [min max]
       (filter identity)
       (str/join "-")))

(defui Detail
  [{:keys [plant]}]
  (when plant
    ($ :div.flex.flex-row.gap-10
      (map (fn [{:keys [url]}]
             ($ :img {:key   url
                      :src   url
                      :width "150"}))
           (:plant/photos plant))
      (let [{:keys [depth spacing temp duration]} (:plant/germination plant)]
        ($ :ul
          ($ :li "Depth: " (range depth))
          ($ :li "Spacing: " (range spacing))
          ($ :li "Temp: " (range temp))
          ($ :li "Duration: " (range duration)))))))
