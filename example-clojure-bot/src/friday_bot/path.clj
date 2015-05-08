(ns friday-bot.path
  (:require [clojure.data.priority-map :refer :all]))

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (Math/abs ^Integer (- x2 x1)) (Math/abs ^Integer (- y2 y1))))

;; SNIP SNIP