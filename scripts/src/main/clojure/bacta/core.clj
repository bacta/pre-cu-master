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
  [filename])

(defn attach-script
  [ns object])

(defn detach-script
  [ns object])

(defn trigger-script
  ([event ns])
  ([event ns object]))

(defn trigger-scripts
  ([event])
  ([event object]))



