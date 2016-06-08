(ns bacta.core
  (:import [com.ocdsoft.bacta.soe.event Event]
           [com.ocdsoft.bacta.swg.server.game.script ScriptService]))

(defonce ^ScriptService script-service nil)

(defn subscribe
  "Subscribes callback function `f` to event."
  [event f]
  (when script-service
    (.subscribe script-service event f)))

