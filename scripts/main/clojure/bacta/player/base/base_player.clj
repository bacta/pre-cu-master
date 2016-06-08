(ns bacta.player.base.base-player
  (:use bacta.core))

(defn on-login
  [event])

(subscribe :event/login on-login)
