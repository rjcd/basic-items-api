(defproject items.api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.0"]]

  :main items.api
  :target-path "target/%s"
  :profiles {:uberjar {:omit-source true
                       :aot :all}

             :dev [:project/dev :profiles/dev]
             :test [:project/dev :project/test :profiles/test]

             :project/dev {:dependencies []
                           :plugins []}
             :project/test {}

             :profiles/dev {:dependencies [[integrant "0.7.0"]
                                           [ring/ring-jetty-adapter "1.7.1"]
                                           [metosin/reitit "0.3.1"]]
                            :source-paths ["modules/zx/src"
                                           "modules/zx-http/src"]}
             :profiles/test {}})
