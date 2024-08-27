(ns io.brianchevalier.init
  (:require
   [io.brianchevalier.app :as app]
   [uix.core :as uix :refer [$]]
   [uix.dom]))

(defonce root
  (let [container (js/document.getElementById "brianchevalier")]
    (uix.dom/create-root container)))

(defn start
  []
  (uix.dom/render-root
   ($ uix/strict-mode
     ($ app/app))
   root))

(defn init
  []
  (when ^boolean goog.DEBUG
    (add-tap js/console.log))
  (start))
