(ns clojure-bot.core
  (:require [clojure-bot.api :as api])
  (:gen-class))

(defn -main
  [& args]
  ;; game-info contains the game map
  (let [game-info (api/register "My cool Clojure bot")]
    (while true
      (Thread/sleep 1000)
      ;; You probably want to get the current game-state from the server before you do your move
      (api/move (:id game-info) (rand-nth ["LEFT" "RIGHT"])))))
