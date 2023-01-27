(ns clojure-bot.api
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def local-url "http://localhost:8080")
(def prod-url "")

(defn url []
  (cond
    (= (System/getenv "ENV") "development") local-url
    (= prod-url "") (do
                      (println "Please set prod-url to point at the server of your Code Camp location instance:")
                      (println "Oulu: https://bots-of-black-friday-oulu.azurewebsites.net")
                      (println "Tampere: https://bots-of-black-friday-tampere.azurewebsites.net")
                      (println "Turku: https://bots-of-black-friday-turku.azurewebsites.net")
                      (println "Helsinki: https://bots-of-black-friday-helsinki.azurewebsites.net")
                      (System/exit 1))
    :else prod-url))

(defn register [player-name]
  (:body (client/post (str (url) "/register")
                      {:content-type :json
                       :accept       :json
                       :as           :json
                       :body         (json/generate-string {:playerName player-name})})))

(defn game-state []
  (:body (client/get (str (url) "/gamestate")
                     {:content-type :json
                      :accept       :json
                      :as           :json})))

(defn move
  "Allowed moves: UP, DOWN, RIGHT, LEFT, PICK, USE"
  [player-id command]
  (client/put (str (url) "/" player-id "/move")
              {:content-type :json
               :accept       :json
               :as           :json
               :body         (json/generate-string command)}))

(defn say [player-id message]
  (client/post (str (url) "/" player-id "/say")
               {:content-type :json
                :accept       :json
                :as           :json
                :body         (json/generate-string message)}))

(defn game-map []
  (:body (client/get (str (url) "/map")
                     {:as :json})))
