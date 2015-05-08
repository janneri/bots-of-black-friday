(ns friday-bot.handler
  (:gen-class)
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [org.httpkit.server :as hs]
            [clj-http.client :as client]
            [ring.middleware.params :refer (wrap-params)]
            [ring.middleware.keyword-params :refer (wrap-keyword-params)]
            [ring.middleware.logger :as logger]
            [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [friday-bot.path :refer [search manhattan-distance]]
            [friday-bot.schema :as sc]))

(def uid (atom nil))

(onelog.core/set-debug!)

(def target-item (atom nil))

(def player-state (atom nil))
(def game-map (atom nil))



(defn set-player-state [player]
  (reset! player-state player))

(defn set-map [map]
  (reset! game-map map))

(defn distance-to-item [item]
  (let [start (:position @player-state)
        end   (:position item)]
    (assoc item :distance (manhattan-distance [(:x start) (:y start)] [(:x end) (:y end)])))
  )

(defn can-afford? [item]
  (>= (:money @player-state) (* (/ (- 100 (:discountPercent item)) 100) (:price item))))

(defn find-closest [items]
  (let [items-distances (map distance-to-item items)]
    (first (filter can-afford? (sort-by :distance items-distances))))
  )

(defn set-target [items]
  (let [new-target (find-closest items)]
    (reset! target-item new-target)
    )
  )

(defn pick []
  (println "PICK")
  (reset! target-item nil)
  "PICK"
  )

(defn target-item-or-exit []
  (if @target-item
    (do
      (println "Going for target")
      (:position @target-item))
    (do
      (println "Can't find affordable")
      (:exit @game-map)))
  )

(defn make-move []
  (let [target-pos (target-item-or-exit)
        player-pos (:position @player-state)
        route      (time (search (vec (map vec (:tiles @game-map))) [(:x player-pos) (:y player-pos)] [(:x target-pos) (:y target-pos)]))
        ]
    (if (= player-pos target-pos)
      (do
        (pick))
      (let [moves {[(:x player-pos) (dec (:y player-pos))] "UP"
                   [(:x player-pos) (inc (:y player-pos))] "DOWN"
                   [(inc (:x player-pos)) (:y player-pos)] "RIGHT"
                   [(dec (:x player-pos)) (:y player-pos)] "LEFT"}
            next  (second route)
            move  (moves next)]
        (println move)
        move)
      )
    ))

(defn handle-round [state]
  (set-player-state (get-in state [:playerState]))
  (set-map (get-in state [:gameState :map]))

  (set-target (get-in state [:gameState :items]))
  (make-move)
  )

(defapi approutes
        (swagger-ui)
        (swagger-docs
          :title "Friday-bot")
        (swaggered "api"
                   :description "hello world"
                   (POST* "/round" []
                          :return    s/Str
                          :body      [gs sc/Game-State-Changed]
                          :summary   ""
                          (ok (handle-round gs)))
                   ))

#_(def app (logger/wrap-with-body-logger (->
                                  approutes
                                  wrap-params
                                  wrap-keyword-params
                                  logger/wrap-with-logger) {:info println})
  )

(def app (->
           approutes
           wrap-params
           wrap-keyword-params
           )
  )

(defn register [port] (reset! uid (:id (:body (client/post "http://192.168.2.86:8080/register" #_"http://localhost:8080/register"
                                                           {:form-params  {:playerName (str "ED-" port) :url (str #_"http://localhost:" "http://192.168.2.200:" port "/round")}
                                                            :content-type :json
                                                            :as           :json})))))

(defn -main [& [port]]
  (let [port-nbr (read-string port)]
    (hs/run-server app {:port port-nbr})
    (register port-nbr))
  )

