(ns bacta.player.base.base-player
  (:use bacta.core)
  (:import [com.ocdsoft.bacta.soe.event ConnectEvent]
           [com.ocdsoft.bacta.swg.server.game.object ServerObject]))

(defn on-login
  ([^ConnectEvent event]
   (on-login event nil))
  ([^ConnectEvent event ^ServerObject object]
   (println :event event
            :object object)))

(subscribe ConnectEvent on-login)
