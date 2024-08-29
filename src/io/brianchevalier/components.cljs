(ns io.brianchevalier.components
  (:refer-clojure :exclude [type])
  (:require
   [clojure.string :as str]
   [oops.core :as oops]
   [uix.core :as uix :refer [$ defui]]))

(defui div
  [{:keys [classes children] :as props}]
  (let [class (->> classes
                   (filter identity)
                   (map name)
                   (str/join " "))]
    ($ :div
      (dissoc (assoc props :class class) :classes)
      children)))

(defui search
  [{:keys [value on-change]}]
  ($ :input {:type        :text
             :placeholder "Search..."
             :class       "text-stone-200 rounded-md p-2 bg-slate-600"
             :value       value
             :on-change   (fn [e] (on-change (oops/oget e [:target :value])))}))

(defui toggle-button
  [{:keys [id checked? on-change]}]
  ($ div
    (if checked?
      {:classes [:text-stone-200
                 :bg-slate-800
                 :text-gray-300
                 :bg-slate-600
                 :p-3
                 :grow-1]}
      {:classes [:p-3
                 :grow-1]})
    ($ :label.cursor-pointer
      {:for id}
      ($ :input.hidden
        {:type      :checkbox
         :checked   checked?
         :on-change (fn [_e] (on-change (not checked?)))
         :name      id
         :id        id})
      ($ :span
        (str/capitalize (name id))))))

(defui plant-type
  [{:keys [value on-change]}]
  ($ div
    {:classes [:flex 
               :overflow-hidden
               :flex-row
               :rounded-md
               :bg-slate-600
               :justify-around
               :items-center
               ]}
    (for [type [:flower :herb :vegetable :tree]]
      ($ toggle-button
        {:id type
         :key type
         :checked? (contains? value type)
         :on-change (fn [v]
                      (on-change (if (true? v)
                                   (conj value type)
                                   (disj value type))))}))))
