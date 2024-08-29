(ns io.brianchevalier.components
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
             :class       "text-stone-200 rounded-md p-1 bg-slate-600"
             :value       value
             :on-change   (fn [e] (on-change (oops/oget e [:target :value])))}))