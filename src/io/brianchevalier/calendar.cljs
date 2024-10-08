(ns io.brianchevalier.calendar
  (:require
   [clojure.string :as str]
   [io.brianchevalier.components :as components]
   [io.brianchevalier.time :as time]
   [uix.core :as uix :refer [defui $]]))

(defn ->col
  [[month day]]
  (+ 2 (time/day->offset day)
     (* 2 (time/month->int month))))

(defui periods
  [{:keys [row periods classes]}]
  ($ :<>
    (map-indexed (fn [i {:keys [start end]}]
                   ($ components/div
                     {:key   i
                      :classes (concat classes
                                       [:opacity-70
                                        :h-5
                                        :rounded-md
                                        (str "row-start-" row)
                                        (str "col-start-" (->col start))
                                        (str "col-end-" (->col end))])}))
                 periods)))

(def k->classes
  {:plant/sow-indoors  [:bg-blue-500 ]
   :plant/sow-outdoors [:bg-green-500 :mt-3 ]
   :plant/transplant   [:bg-orange-500 :mt-6 ]})

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
                    :periods (get plant k)})))))

(defui table-rows [{:keys [plants]}]
  (map-indexed (fn [i p]
                 ($ table-row {:i     i
                               :key   i
                               :plant p}))
               plants))

(defui table-header []
  ($ :<>
    (map-indexed (fn [i month]
                   ($ components/div
                     {:key   i
                      :classes [:relative
                                :text-center
                                :col-span-2
                                :row-start-1
                                (str "col-start-" (inc (inc (* 2 i))))]}
                     ($ :div
                       (str/capitalize (name month)))))
                 time/months)))

(defui vline
  "Draw a vertical line on the calendar"
  [{:keys [color date width n-rows]
    :or {color :bg-slate-600
         width :w-0.5 }}]
  ($ components/div
    {:classes [color
               :rounded
               width
               :row-start-2
               (str "row-end-" (inc (inc n-rows)))
               (str "col-start-" (->col date))]}))

(defui gridlines
  "Draw gridlines on the calendar"
  [{:keys [n-rows]}]
  ($ :<>
    (for [month time/months
          day   [0 15]]
      ($ vline {:key   [month day]
                :n-rows n-rows
                :date  [month day]
                :color :bg-slate-600 }))))

(defui current-day
  "Draw 'today' on the calendar"
  [{:keys [n-rows]}]
  (let [[month day] (time/current-date)]
    ($ vline
      {:n-rows n-rows
       :color :bg-red-900
       :width :w-2
       :date [month day]})))

(defui calendar
  [{:keys [plants]}]
  (let [n-rows (count plants)]
    ($ :div.overflow-scroll
     ($ :div.grid.grid-cols-25.grid-rows-25.grid-flow-row.gap-y-5.min-w-160
       ($ gridlines {:n-rows n-rows})
       ($ current-day {:n-rows n-rows})
       ($ table-header)
       ($ table-rows {:plants plants})))))