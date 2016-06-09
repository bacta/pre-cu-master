(ns bacta.core
  (:require [clojure.string :as str] [clojure.main :as main])
  (:import (com.ocdsoft.bacta.soe.service PublisherService)
           (com.ocdsoft.bacta.swg.server.game.script ScriptService)))

(defonce ^ScriptService script-service nil)
(defonce ^PublisherService publisher-service nil)

(defn subscribe
  "Subscribes callback function `f` to event."
  [event f]
  (when publisher-service
    (.subscribe publisher-service event f)))

(defn filename-to-namespace
  [filename]
  (-> filename
      (str/replace "/" ".")
      (str/replace "_" "-")))

(defn namespace-to-filename
  [namespace]
  (-> (cond->> namespace
        (not (str/starts-with? namespace "bacta")) (str "bacta/"))
      (str/replace "." "/")
      (str/replace "-" "_")
      (str ".clj")))

(defn track-event
  [filename]
  (println :track-event filename))

;;Hacked in the bacta/ prepend - belongs in the namespace-to-filename func
;;This still doesn't resolve the script. Not sure how it's trying to resolve it. Need to investigate.
(defn attach-script
  [namespace object]
  (println :attach-script namespace object)
  (namespace-to-filename namespace))

(defn detach-script
  [ns object]
  (println :detach-script ns object))

(defn trigger-script
  ([event ns]
   (println :trigger-script event ns))
  ([event ns object]
   (println :trigger-script event ns object)))

(defn trigger-scripts
  ([event]
   (println :trigger-scripts event))
  ([event object]
   (println :trigger-scripts event object)))


