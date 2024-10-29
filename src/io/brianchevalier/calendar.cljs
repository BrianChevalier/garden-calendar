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

(defn scientific-name [plant]
  (or (:plant/scientific-name plant)
      (str/join " " 
                [(some-> plant :plant/genus name)
                 (some-> plant :plant/species name)])))

(defui table-row
  [{:keys [i plant genuses on-select]}]
  (let [row  (inc (inc i))
        name (:plant/name plant)]
    ($ :<>
      ($ :div 
        {:class (str "col-start-1 row-start-" row)
         :on-click (fn [_e] (on-select plant))}
        (when (contains? genuses (:plant/genus plant))
          "***")
        ($ :div.flex.flex-col
          name
          ($ :i.text-stone-400
            (scientific-name plant))))
      (for [k (keys k->classes)]
        ($ periods {:key     k
                    :row     row
                    :classes (k->classes k)
                    :periods (get plant k)})))))

(defui table-rows [{:keys [plants genuses on-select]}]
  (map-indexed (fn [i p]
                 ($ table-row {:i         i
                               :key       i
                               :plant     p
                               :on-select on-select
                               :genuses   genuses}))
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

(defui frost-lines
  [{:keys [n-rows]}]
  ($ :<>
    ($ vline {:color :bg-blue-950
              :width :w-2
              :date [:feb 30]
              :n-rows n-rows})
    ($ vline {:color :bg-blue-950
              :width :w-2
              :date [:nov 30]
              :n-rows n-rows})))

(defui current-cell-div
  [{:keys [current-cell plants]}]
  #_(when current-cell
    (let [[row col] current-cell
          [lower _upper] (:plant/harvest-days (nth plants (dec (dec row))))]
      (when lower
        (let [n-cells (quot lower 14)
              s (drop-while #(< % col) (cycle (range 2 26)))]
          ($ :<>
            (for [col (take n-cells s)]
              ($ components/div
                {:key col
                 :classes [(str "row-start-" row)
                           (str "col-start-" col)
                           :opacity-45
                           :z-10
                           :bg-slate-400 ]}))))))))

(defui dummy-div
  [{:keys [row col on-cell-change]}]
  ($ components/div
    {:classes        [:z-20
                      (str "row-start-" row)
                      (str "col-start-" col)]
     :onPointerEnter (fn [_e] (on-cell-change [row  col]))}))

(defui cells
  [{:keys [n-rows on-cell-change]}]
  ($ :<>
    (for [row (range 2 (inc (inc n-rows)))
          col (range 2 26)]
      ($ dummy-div {:key (str [row col])
                    :on-cell-change on-cell-change
                    :row            row
                    :col            col}))))

(defui contextual-data
  [{:keys [n-rows plants]}]
  (let [[cell on-cell-change] (uix/use-state nil)]
    ($ :<>
      ($ current-cell-div {:current-cell cell
                           :plants plants})
      ($ cells {:n-rows         n-rows
                :on-cell-change on-cell-change}))))

(defui calendar
  [{:keys [plants genuses on-select]}]
  (let [n-rows (count plants)]
    ($ :div.overflow-scroll
      ($ :div.grid.grid-cols-25.grid-rows-25.grid-flow-row.gap-y-5.min-w-160
        ($ frost-lines {:n-rows n-rows})
        ($ contextual-data {:n-rows n-rows
                            :plants plants})
        ($ gridlines {:n-rows n-rows})
        ($ current-day {:n-rows n-rows})
        ($ table-header)
        ($ table-rows {:on-select on-select
                       :plants    plants
                       :genuses   genuses})))))
