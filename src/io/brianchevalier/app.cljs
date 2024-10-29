(ns io.brianchevalier.app
  (:require
   [clojure.edn :as edn]
   [clojure.set :as set]
   [clojure.spec.alpha :as spec]
   [clojure.string :as str]
   [io.brianchevalier.calendar :as calendar]
   [io.brianchevalier.components :as components]
   [io.brianchevalier.plant-detail :as plant-detail]
   [io.brianchevalier.schema]
   [io.brianchevalier.time :as time]
   [shadow.resource :as shadow.resource]
   [uix.core :as uix :refer [defui $]]))

(defn get-data []
  (let [base (edn/read-string (shadow.resource/inline "../../../resources/data.edn"))
        flowers (edn/read-string (shadow.resource/inline "../../../resources/flowers.edn"))
        vegetables (shadow.resource/inline "../../../resources/vegetables.edn")
        data (merge base {:plant/plants (concat flowers vegetables)})]
    (when-not (spec/valid? :plant/db data)
      (throw (ex-info "Schema error" {:data (spec/explain-data :plant/db data)})))
    data))

(defn current-plant?
  [current-period plant]
  (->> plant 
       ((juxt :plant/sow-indoors
              :plant/sow-outdoors
              :plant/transplant))
       (filter identity)
       (apply concat)
       (time/in-any-periods? current-period)))

(defn filter-by-date
  [plants]
  (let [current-period (time/current-date)]
    (filter (partial current-plant? current-period) plants)))

(defn query
  [{plants :plant/plants}
   {:keys [search plant-type times selected-plant]}]
  (cond->> plants
    selected-plant
    (filter (fn [plant] (= plant selected-plant)))

    (not (str/blank? search))
    (filter (fn [{name :plant/name}]
              (str/includes? (str/lower-case name)
                             (str/lower-case search))))

    (seq plant-type)
    (filter (fn [{type :plant/type}]
              (contains? plant-type type)))

    (contains? times :current)
    (filter-by-date)

    :always (sort-by :plant/name)))

(defui page-header
  []
  ($ :div.flex.flex-col.h-20.bg-slate-800
    ($ :div.text-gray-300.font-bold.text-xl.text-center.my-auto.mx-auto
      "Sonoran Desert Gardening")))

(defui resources []
  ($ :div.flex.flex-row.gap-5.mx-auto.justify-center
    ($ :a {:href   "https://extension.arizona.edu/sites/default/files/2024-08/az1005-2018.pdf"
           :target "_blank"}
      "Vegetable Calendar")
    ($ :a {:href   "https://extension.arizona.edu/sites/default/files/2024-08/az1100a.pdf"
           :target "_blank"}
      "Flower Calendar")
    ($ :a {:href   "https://extension.arizona.edu/sites/default/files/2024-08/az1269.pdf"
           :target "_blank"}
      "Tree Calendar")))

(defui navbar 
  [{:keys [value on-change]}]
  ($ :div.flex.flex-row.text-gray-300.font-bold.text-sm.text-center.mx-auto.p-3.justify-center
    ($ components/toggle-button-group {:value value
                                       :on-change (fn [v] (on-change (set/difference v value)))
                                       :options [:calendar :resources]})))

(defui app
  []
  (let [db                (get-data)
        [value on-change] (uix/use-state {:search         ""
                                          :plant-type     #{}
                                          :times          #{:current}
                                          :selected-plant nil})
        [page on-navigate] (uix/use-state #{:calendar})
        plants            (query db value)]
    ($ :div.bg-slate-700.text-stone-100.h-screen
      ($ page-header)
      ($ navbar {:value page :on-change on-navigate})
      (cond 
        (contains? page :resources)
        ($ resources)

        :else
        ($ :div.flex.flex-col.gap-10.p-5
          (contains? page :calendar)
          ($ :div.flex.flex-col.md:flex-row.gap-5
            ($ components/search {:value     (:search value)
                                  :on-change (fn [v] (on-change (assoc value :search v)))})
            ($ components/plant-type {:value     (:plant-type value)
                                      :on-change (fn [v] (on-change (assoc value :plant-type v)))})
            ($ components/time-span {:value     (:times value)
                                     :on-change (fn [v] (on-change (assoc value :times v)))}))
          ($ calendar/calendar {:on-select (fn [plant]
                                             (on-change (assoc value :selected-plant (when (not= plant (:selected-plant value)) plant))))
                                :plants plants
                                :genuses (:genuses db)})
          ($ plant-detail/Detail {:plant (:selected-plant value)}))
        ))))
