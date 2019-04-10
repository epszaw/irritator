(defproject irritator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure             "1.8.0"]
                 [org.clojure/tools.logging       "0.4.1"]
                 [org.clojars.beppu/clj-audio     "0.3.0"]
                 [com.googlecode.soundlibs/mp3spi "1.9.5.4"]
                 [com.novemberain/monger          "3.1.0"]
                 [io.forward/yaml                 "1.0.9"]
                 [ring/ring-json                  "0.4.0"]
                 [ring/ring-codec                 "1.1.1"]
                 [morse                           "0.2.4"]
                 [clj-time                        "0.15.0"]
                 [compojure                       "1.6.1"]
                 [javax.servlet/servlet-api       "2.5"]
                 [http-kit                        "2.3.0"]
                 [cheshire                        "5.8.1"]]

  :target-path "target/%s"

  :profiles 
    {:bot 
      {:main irritator.bot.core 
       :uberjar-name "bot.jar"}
     :daemon 
      {:main irritator.daemon.core 
       :uberjar-name "daemon.jar"}})
