(ns bacta.player.base.base-player
  (:use bacta.core)
  (:import [com.ocdsoft.bacta.soe.event ConnectEvent]))

(defn on-login
  [^ConnectEvent event]
  (let [connection (.-connection event)]
    ))

(subscribe ConnectEvent on-login)
