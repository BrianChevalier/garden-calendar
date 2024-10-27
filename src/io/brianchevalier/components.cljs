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
                 :grow-1 ]}
      {:classes [:p-3
                 :grow-1 ]})
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

(defui toggle-button-group
  [{:keys [options value on-change]}]
  ($ div
    {:classes [:flex
               :overflow-hidden
               :flex-row
               :rounded-md
               :bg-slate-600
               :justify-around
               :items-center 
               :w-fit ]}
    (for [option options]
      ($ toggle-button
        {:id        option
         :key       option
         :checked?  (contains? value option)
         :on-change (fn [v]
                      (on-change (if (true? v)
                                   (conj value option)
                                   (disj value option))))}))))

(defui plant-type
  [{:keys [value on-change]}]
  ($ toggle-button-group
    {:value     value
     :on-change on-change
     :options   [:flower :herb :vegetable :tree]}))

(defui time-span
  [{:keys [value on-change]}]
  ($ toggle-button-group
    {:value     value
     :on-change on-change
     :options   [:current]}))
