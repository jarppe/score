(defproject score "0.1.0-SNAPSHOT"
  :description "Show 4clojure scoring"
  :source-path "src/clj"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta10" :exclusions [org.clojure/clojure]]
                 [enlive "1.0.1"]
                 [clj-http "0.5.5"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [com.h2database/h2 "1.3.168"]
                 [com.jolbox/bonecp "0.7.1.RELEASE"]
                 [org.slf4j/slf4j-simple "1.5.10"]]
  :profiles {:dev {:dependencies [[midje "1.4.0" :exclusions [org.clojure/clojure]]]
                   :plugins [[lein-midje "2.0.0-SNAPSHOT"]]}}
  :cljsbuild {:builds {:main {:source-path "src/cljs"
                              :compiler {:output-to "resources/public/score.js"
                                         :optimizations :whitespace
                                         :pretty-print true
                                         :print-input-delimiter true}
                              :notify-command ["growlnotify" "-m"]}}}
  :main score.server
  :repl-options {:init-ns score.server}
  :min-lein-version "2.0.0")
