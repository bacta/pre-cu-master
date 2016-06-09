(ns bacta.player.base.base-player
  (:use bacta.core)
  (:import (com.ocdsoft.bacta.swg.server.game.event PlayerOnlineEvent)
           (com.ocdsoft.bacta.swg.server.game.object ServerObject)))

(defn on-login

  ([^PlayerOnlineEvent event]
   (println :event event
            :object nil))


  ([^PlayerOnlineEvent event ^ServerObject object]
   (println :event event
            :object object)))


(subscribe PlayerOnlineEvent on-login)
