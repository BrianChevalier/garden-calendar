(ns io.brianchevalier.calendar
  (:require
   [clojure.string :as str]
   [io.brianchevalier.components :as components]
   [uix.core :as uix :refer [defui $]]))

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
  {0  0
   1  0
   15 1
   30 2})

(defn ->col
  [[month day]]
  (+ 2 (day->offset day)
     (* 2 (month->int month))))

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
  {:plant/sow-indoors  [:bg-blue-500]
   :plant/sow-outdoors [:bg-green-500 :mt-3]
   :plant/transplant   [:bg-orange-500 :mt-6]})

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
                 months)))

(defui vline
  "Draw a vertical line on the calendar"
  [{:keys [color date width]
    :or {color :bg-slate-600
         width :w-0.5}}]
  ($ components/div
    {:classes [color
               :rounded
               width
               :row-start-2
               :row-end-26
               (str "col-start-" (->col date))]}))

(defui gridlines
  "Draw gridlines on the calendar"
  []
  ($ :<>
    (for [month months
          day   [0 15]]
      ($ vline {:key   [month day]
                :date  [month day]
                :color :bg-slate-600}))))

(defui current-day
  "Draw 'today' on the calendar"
  []
  (let [now (js/Date.)
        month (get months (.getMonth now))
        day ({0 1
              1 15
              2 30}
             (quot (.getDate now) 15))]
    ($ vline
      {:color :bg-red-900
       :width :w-2
       :date [month day]})))

(defui calendar
  [{:keys [plants]}]
  ($ :div.overflow-scroll
    ($ :div.grid.grid-cols-25.grid-rows-25.grid-flow-row.gap-y-5.min-w-160
      ($ gridlines)
      ($ current-day)
      ($ table-header)
      ($ table-rows {:plants plants}))))