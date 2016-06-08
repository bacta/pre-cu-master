(ns bacta.core
  (:import [com.ocdsoft.bacta.soe.event Event]
           [com.ocdsoft.bacta.swg.server.game.script ScriptService]))

(defonce ^ScriptService script-service nil)

(defn subscribe
  "Subscribes callback function `f` to event."
  [^Event event f]
  (.subscribe script-service f))

