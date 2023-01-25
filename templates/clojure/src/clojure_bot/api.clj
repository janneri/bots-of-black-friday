(ns clojure-bot.api
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def local-url "http://localhost:8080")
(def prod-url "https://bots-of-black-friday.azurewebsites.net")

(defn url []
  (if (= (System/getenv "ENV") "development")
    local-url
    prod-url))

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

(defn move [player-id command]
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
