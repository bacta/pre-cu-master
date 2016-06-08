(ns bacta.player.base.base-player
  (:use bacta.core)
  (:import [com.ocdsoft.bacta.soe.event ConnectEvent]
           [com.ocdsoft.bacta.swg.server.game.object ServerObject]))

(defn on-login
  [^ConnectEvent event ^ServerObject object]
  (let [connection (.-connection event)]
    (println :event event
             :object object
             :connection connection)))

(subscribe ConnectEvent on-login)
