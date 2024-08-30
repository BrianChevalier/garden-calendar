(ns io.brianchevalier.time
  "Utilities for dealing with times/dates")

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

(defn current-date
  "Gets the current 'date' rounded down"
  []
  (let [now (js/Date.)
        month (get months (.getMonth now))
        day ({0 1
              1 15
              2 30}
             (quot (.getDate now) 15))]
    [month day]))

(def all-periods
  (for [month months
        day   [1 15 30]]
    [month day]))

(def period->int
  (reduce (fn [m [i period]]
            (assoc m period i))
          {}
          (map vector (range) all-periods)))

(defn in-period?
  [period {:keys [start end]}]
  (<= (period->int start)
      (period->int period)
      (period->int end)))

(defn in-any-periods?
  [period periods]
  (some (partial in-period? period) periods))
