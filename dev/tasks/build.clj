(ns tasks.build
  (:require
   [babashka.fs :as fs]
   [tasks.tools :refer [npm npx shadow]]))

(defn install []
  (when (seq
         (fs/modified-since
          "node_modules"
          ["package.json" "package-lock.json"]))
    (npm :ci)))

(defn css
  "Build css resources"
  []
  (npx :postcss "static/css/main.css" "-o" "public/static/css/main.css"))

(defn cljs
  "Build cljs resources"
  []
  (shadow :release :main))

(defn build
  []
  (install)
  (css)
  (cljs))