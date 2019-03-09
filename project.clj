(defproject irritator "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure             "1.8.0"]
                 [morse                           "0.2.4"]
                 [org.craigandera/dynne           "0.4.1"]
                 [io.forward/yaml                 "1.0.9"]
                 [org.clojars.beppu/clj-audio     "0.3.0"]
                 [com.googlecode.soundlibs/mp3spi "1.9.5.4"]
                 [clj-time                        "0.15.0"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot irritator.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
