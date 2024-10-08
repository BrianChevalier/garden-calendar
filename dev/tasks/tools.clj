(ns tasks.tools
  (:require
   [babashka.process :as p]
   [clojure.string :as str]
   [io.aviso.ansi :as a])
  (:import
   [java.time Duration]))

(def ^:dynamic *cwd* nil)
(def ^:dynamic *opts* nil)

(defn- now [] (System/currentTimeMillis))

(defn- format-millis [ms]
  (let [duration (Duration/ofMillis ms)
        h        (mod (.toHours duration) 24)
        m        (mod (.toMinutes duration) 60)
        s        (mod (.toSeconds duration) 60)
        ms       (mod ms 1000)]
    (str
     (a/bold-blue "->")
     " "
     (a/bold-yellow
      (str
       (when (pos? h)
         (str h " hours, "))
       (when (pos? m)
         (str m " minutes, "))
       s "." ms " seconds")))))

(def ^:private in-bb? (some? (System/getProperty "babashka.version")))

(def ^:private fns
  (when in-bb? {:clojure (requiring-resolve 'babashka.deps/clojure)}))

(defn- sh* [& args]
  (binding [*out* *err*]
    (println (a/bold-blue "=>")
             (a/bold-green (name (first args)))
             (a/bold-white (str/join " " (map name (rest args))))))
  (let [opts   (merge {:dir      *cwd*
                       :shutdown p/destroy-tree}
                      (merge *opts* (when-not (:inherit *opts*)
                                      {:out *out* :err *err*})))
        start  (now)
        result @(if-let [f (get fns (first args))]
                  (or (f (map name (rest args)) opts)
                      (atom {:exit 0}))
                  (p/process (map name args) opts))
        exit   (:exit result)]
    (when-not (:inherit opts)
      (.flush *out*)
      (.flush *err*))
    (binding [*out* *err*]
      (println
       (str (format-millis (- (now) start))
            (when-not (zero? exit)
              (str " " (a/bold-red (str "(exit: " exit ")")))))))
    (when-not (zero? exit)
      (throw (ex-info (str "Non-zero exit code: "
                           (str/join " " (map name args)))
                      (assoc (select-keys result [:cmd :exit]) :opts *opts*)))))
  true)

(defn sh [& args]
  (if-let [session (:session *opts*)]
    (apply session sh* args)
    (apply sh* args)))

(def bb     (partial #'sh :bb))
(def nbb    (partial #'sh :npx :nbb))
(def clj    (partial #'sh :clojure))
(def git    (partial #'sh :git))
(def node   (partial #'sh :node))
(def npm    (partial #'sh :npm))
(def npx    (partial #'sh :npx))
(def shadow (partial #'clj "-M:cljs" "-m" "shadow.cljs.devtools.cli"))
