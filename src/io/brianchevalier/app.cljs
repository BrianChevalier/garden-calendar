(ns io.brianchevalier.app
  (:require
   ["react" :as react]
   [clojure.edn :as edn]
   [clojure.spec.alpha :as spec]
   [clojure.string :as str]
   [io.brianchevalier.schema]
   [shadow.resource :as shadow.resource]
   [uix.core :refer [defui $]]))

(defn get-data []
  (let [data (edn/read-string (shadow.resource/inline "../../../resources/data.edn"))]
    (when-not (spec/valid? :plant/db data)
      (throw (js/Error. "Schema error")))
    data))

(def months
  [:jan :feb :mar :apr :may :jun :jul :aug :sept :oct :nov :dec])

(def month->int
  {:jan  0
   :feb  1
   :mar  2
   :apr  3
   :may  4
   :jun  5
   :jul  6
   :aug  7
   :sept 8
   :oct  9
   :nov  10
   :dec  11})

(def day->offset
  {1  0
   15 1
   30 2})

(defn ->col
  [[month day]]
  (+ 2 (day->offset day)
     (* 2 (month->int month))))

(defui periods
  [{:keys [row periods classes]}]
  ($ :<>
     (map-indexed (fn [i [start end]]
                    ($ :div
                       {:key   i
                        :class (str classes " h-5 rounded-md row-start-" row " col-start-" (->col start) " col-end-" (->col end))}))
                  periods)))

(def k->classes
  {:plant/sow-indoors  "bg-blue-500"
   :plant/sow-outdoors "bg-green-500 mt-3"
   :plant/transplant   "bg-orange-500 mt-6"})

(defui table-row
  [{:keys [i plant]}]
  (let [row  (inc (inc i))
        name (:plant/name plant)]
    ($ :<>
       ($ :div {:class (str "col-start-1 row-start-" row)}
          ($ :div.flex.flex-col
             name
             ($ :i.text-stone-400
                (:plant/scientific-name plant))))
       (for [k (keys k->classes)]
         ($ periods {:key     k
                     :row     row
                     :classes (k->classes k)
                     :periods (get plant k)}))
       )))

(defui table-rows [{:keys [plants]}]
  (map-indexed (fn [i p]
                 ($ table-row {:i     i
                               :key   i
                               :plant p}))
               plants))

(defui table-header []
  ($ :<>
     ($ :div {:class "col-start-1 row-start-1"}
        "Plant")
     (map-indexed (fn [i month]
                    ($ :div.relative
                       {:key   i
                        :class (str "text-center col-span-2 row-start-1 col-start-" (inc (inc (* 2 i))))}
                       ($ :div
                          (str/capitalize (name month)))))
                  months)))

(defui search
  [{:keys [value on-change]}]
  ($ :input {:type        :text
             :placeholder "Search..."
             :class       "text-stone-500 rounded-md p-2"
             :value       value
             :on-change   (fn [e] (-> e .-target .-value on-change))}))

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

(defui app
  []
  (let [db                (get-data)
        [value on-change] (react/useState {:search ""})
        plants            (query db value)]
    ($ :div.bg-slate-500.text-stone-100.p-10.h-screen
       ($ :div.flex.flex-col.gap-10
          ($ search {:value     (:search value)
                     :on-change (fn [v] (on-change (assoc value :search v)))})
          ($ :div.grid.grid-cols-25.grid-rows-25.grid-flow-row.gap-y-5
             ($ table-header)
             ($ table-rows {:plants plants}))))))
