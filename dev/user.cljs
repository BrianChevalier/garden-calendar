(ns user
  (:require
   [portal.web :as portal]))

(defn portal
  []
  (add-tap #'portal/submit)
  (portal/open))

(comment
  (portal)
  (tap> :a))