(ns clojure-bot.core
  (:require [clojure-bot.api :as api])
  (:gen-class))

(defn -main
  [& args]
  (let [game-info (api/register "My cool Clojure bot")]
    (while true
      (Thread/sleep 1000)
      (api/move (:id game-info) (rand-nth ["LEFT" "RIGHT"])))))
