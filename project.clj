(defproject irritator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license 
    {:name "Eclipse Public License"
     :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure       "1.8.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [com.novemberain/monger    "3.1.0"]
                 [io.forward/yaml           "1.0.9"]                 
                 [ring/ring-codec           "1.1.1"]                                
                 [javax.servlet/servlet-api "2.5"]
                 [http-kit                  "2.3.0"]
                 [cheshire                  "5.8.1"]]  

  :auto-clean true               

  :profiles
    {:bot
      {:main irritator.bot.core
       :aot [irritator.bot.core]
       :source-paths ["src/irritator/bot" "src/irritator"]
       :target-path "target/bot"              
       :uberjar-name "bot.jar" 
       :dependencies [[ring/ring-json         "0.4.0"]
                      [morse                  "0.2.4"]
                      [compojure              "1.6.1"]]}

    :daemon 
      {:main irritator.daemon.core
       :aot [irritator.daemon.core]
       :uberjar-name "daemon.jar"       
       :target-path "target/daemon"
       :source-paths ["src/irritator/daemon" "src/irritator"]
       :dependencies [[org.clojars.beppu/clj-audio     "0.3.0"]
                      [com.googlecode.soundlibs/mp3spi "1.9.5.4"]
                      [clj-time                        "0.15.0"]]}})
