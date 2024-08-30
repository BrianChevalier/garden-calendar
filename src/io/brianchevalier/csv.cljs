(ns io.brianchevalier.csv
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [io.brianchevalier.schema]
   [shadow.resource :as shadow.resource]))

(defn ->map [header row]
  (zipmap header row))

(defn ->maps [data]
  (let [header (first data)
        rows (rest data)]
    (map #(->map header %) rows)))

(defn ->entry
  [{:strs [Type Name Latin] :as m}]
  (assoc m
         :plant/name Name
         :plant/scientific-name Latin
         :plant/type ({} Type)))

(def months
  [:jan :feb :mar :apr :may :jun :jul :aug :sept :oct :nov :dec])

(def days
  [[1 "01"] [15 "15"]])

(defn pad-end [[month day]]
  [month ({1 15, 15 30} day)])

(def period-types
  {:plant/sow-indoors "Indoors"
   :plant/sow-outdoors "Outdoors"
   :plant/transplant "Transplant"})

(def plant-types
  {"Vegetable" :vegetable
   "Flower"    :flower
   "Herb"      :herb})

(defn collect-periods
  [[k s] row]
  (->> (when (str/includes? (get row k) s)
         [month ind-day])
       (let [k (str (str/capitalize (name month)) day)])
       (for [month months
             [ind-day day]   days])
       (partition-by nil?)
       (remove (fn [v] (nil? (first v))))
       (map (fn [dates]
              {:start (first dates)
               :end   (pad-end (last dates))}))
       (assoc row k)))

(defn add-all-periods
  [row]
  (reduce (fn [m [k s]]
            (collect-periods [k s] m))
          row
          period-types))


(defn format-plants
  [plants]
  (map
   (fn [{name       :plant/name
         scientific :plant/scientific-name
         indoors    :plant/sow-indoors
         outdoors   :plant/sow-outdoors
         transplant :plant/transplant
         :strs [Type]}]
     (cond-> {}
       name          (assoc :plant/name name)
       (not (str/blank? scientific))
       (assoc :plant/scientific-name scientific)

       (plant-types Type)
       (assoc :plant/type (plant-types Type))
       
       (seq indoors)    (assoc :plant/sow-indoors indoors)
       (seq outdoors)   (assoc :plant/sow-outdoors outdoors)
       (seq transplant) (assoc :plant/transplant transplant)))

   plants))

(comment
  (->> (shadow.resource/inline "../../../resources/plants.edn")
       edn/read-string
       ->maps
       (map ->entry)
       (map add-all-periods)
       format-plants
       (sort-by :plant/name)
       tap>))