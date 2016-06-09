(ns bacta.core
  (:import [com.ocdsoft.bacta.soe.event Event]
           [com.ocdsoft.bacta.swg.server.game.script ScriptService]))

(defonce ^ScriptService script-service nil)

(defn subscribe
  "Subscribes callback function `f` to event."
  [event f]
  (when script-service
    (.subscribe script-service event f)))

(defn track-event
  [filename]
  (println :track-event filename))

(defn attach-script
  [ns object]
  (println :attach-script ns object))

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



