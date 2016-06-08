(ns bacta.core
  (:import [com.ocdsoft.bacta.soe.event Event]
           [com.ocdsoft.bacta.swg.server.game.script ScriptService]
           [com.ocdsoft.bacta.soe.service PublisherService]))

(defonce ^ScriptService script-service nil)

(defonce ^PublisherService publisher-service nil)

(defn subscribe
  "Subscribes callback function `f` to event."
  [^Event event f]
  (.subscribe publisher-service event f))

