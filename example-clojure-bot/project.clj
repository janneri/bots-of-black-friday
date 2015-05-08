(defproject friday-bot "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [metosin/compojure-api "0.17.0"]
                           [metosin/ring-http-response "0.5.2"]
                           [metosin/ring-swagger-ui "2.0.24"]
                           [clj-http "1.0.1"]
                           [ring.middleware.logger "0.5.0"]
                           [cheshire "5.4.0"]
                           [http-kit "2.1.19"]
                           ]
            :main friday-bot.handler
            :ring {:handler friday-bot.handler/app
                   :port 3002}
            :uberjar-name "server.jar"
            :profiles {:uberjar {:resource-paths ["swagger-ui"]}
                       :dev {:dependencies [[javax.servlet/servlet-api "2.5"]]
                             :plugins [[lein-ring "0.9.0"]]}})
