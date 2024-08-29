(ns io.brianchevalier.app
  (:require
   [clojure.edn :as edn]
   [clojure.spec.alpha :as spec]
   [clojure.string :as str]
   [io.brianchevalier.schema]
   [shadow.resource :as shadow.resource]
   [io.brianchevalier.components :as components]
   [io.brianchevalier.calendar :as calendar]
   [uix.core :as uix :refer [defui $]]))

(defn get-data []
  (let [data (edn/read-string (shadow.resource/inline "../../../resources/data.edn"))]
    (when-not (spec/valid? :plant/db data)
      (throw (ex-info "Schema error" {:data data})))
    data))

(defn query
  [{plants :plant/plants}
   {:keys [search plant-type]}]
  (cond->> plants

    (not (str/blank? search))
    (filter (fn [{name :plant/name}]
              (str/includes? (str/lower-case name)
                             (str/lower-case search))))

    (seq plant-type)
    (filter (fn [{type :plant/type}]
              (contains? plant-type type)))

    :always (sort-by :plant/name)))

(defui page-header
  []
  ($ :div.flex.flex-col.h-20.bg-slate-800
    ($ :div.text-gray-300.font-bold.text-xl.text-center.my-auto.mx-auto
      "Sonoran Desert Gardening")))

(defui app
  []
  (let [db                (get-data)
        [value on-change] (uix/use-state {:search ""
                                          :plant-type #{}})
        plants            (query db value)]
    ($ :div.bg-slate-700.text-stone-100.h-screen
      ($ page-header)
      ($ :div.flex.flex-col.gap-10.p-5
        ($ :div.flex.flex-col.md:flex-row.gap-5
          ($ components/search {:value     (:search value)
                               :on-change (fn [v] (on-change (assoc value :search v)))})
        ($ components/plant-type {:value (:plant-type value)
                                  :on-change (fn [v] (on-change (assoc value :plant-type v)))}))
        ($ calendar/calendar {:plants plants})))))
