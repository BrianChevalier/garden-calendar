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
   {:keys [search]}]
  (sort-by :plant/name
           (if (str/blank? search)
             plants
             (filter (fn [{name :plant/name}]
                       (str/includes? (str/lower-case name)
                                      (str/lower-case search)))
                     plants))))

(defui page-header
  []
  ($ :div.flex.flex-col.h-20.bg-slate-800
    ($ :div.text-gray-300.font-bold.text-xl.text-center.my-auto.mx-auto
      "Sonoran Desert Gardening")))

(defui app
  []
  (let [db                (get-data)
        [value on-change] (uix/use-state {:search ""})
        plants            (query db value)]
    ($ :div.bg-slate-700.text-stone-100.h-screen
      ($ page-header)
      ($ :div.flex.flex-col.gap-10.p-5
        ($ components/search {:value     (:search value)
                              :on-change (fn [v] (on-change (assoc value :search v)))})
        ($ calendar/calendar {:plants plants})))))
