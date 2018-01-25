(defproject dvlopt/sysrun
            "0.0.2"

  :description "Miscellaneous system utilities"
  :url         "https://github.com/dvlopt/sysrun"
  :license     {:name "Eclipse Public License"
                :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles    {:dev {:source-paths ["dev"]
                      :main         user
                      :dependencies [[org.clojure/clojure "1.9.0"]
                                     [org.clojure/test.check "0.10.0-alpha2"]
                                     [criterium              "0.4.4"]]
                      :plugins      [[venantius/ultra "0.5.2"]
                                     [lein-codox      "0.10.3"]]
                      :codox        {:output-path  "doc/auto"
                                     :source-paths ["src"]}
                      :global-vars  {*warn-on-reflection* true}}})
