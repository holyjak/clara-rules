(defproject com.cerner/clara-rules "0.22.0-SNAPSHOT"
  :description "Clara Rules Engine"
  :url "https://github.com/cerner/clara-rules"
  :license {:name "Apache License Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [prismatic/schema "1.1.6"]]
  :profiles {:dev {:dependencies [[org.clojure/math.combinatorics "0.1.3"]
                                  [org.clojure/data.fressian "0.2.1"]]
                   :java-source-paths ["src/test/java"]
                   :global-vars {*warn-on-reflection* true}}
             :provided {:dependencies [[org.clojure/clojurescript "1.7.170"]]}
             :recent-clj {:dependencies [^:replace [org.clojure/clojure "1.9.0"]
                                         ^:replace [org.clojure/clojurescript "1.9.946"]]}
             :java9 {:jvm-opts ["--add-modules=java.xml.bind"]}}
  :plugins [[lein-codox "0.10.3" :exclusions [org.clojure/clojure
                                              org.clojure/clojurescript]]
            [lein-javadoc "0.3.0" :exclusions [org.clojure/clojure
                                               org.clojure/clojurescript]]
            [lein-cljsbuild "1.1.7" :exclusions [org.clojure/clojure
                                                 org.clojure/clojurescript]]
            [lein-figwheel "0.5.14" :exclusions [org.clojure/clojure
                                                 org.clojure/clojurescript]]]
  :codox {:namespaces [clara.rules clara.rules.dsl clara.rules.accumulators
                       clara.rules.listener clara.rules.durability
                       clara.tools.inspect clara.tools.tracing
                       clara.tools.fact-graph]
          :metadata {:doc/format :markdown}}
  :javadoc-opts {:package-names "clara.rules"}
  :source-paths ["src/main/clojure"]
  :resource-paths []
  :test-paths ["src/test/clojure" "src/test/common"]
  :java-source-paths ["src/main/java"]
  :javac-options ["-target" "1.6" "-source" "1.6"]
  :clean-targets ^{:protect false} ["resources/public/js" "target"]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds [;; Simple mode compilation for tests.
                       {:id "figwheel"
                        :source-paths ["src/test/clojurescript" "src/test/common"]
                        :figwheel true
                        :compiler {:main "clara.test"
                                   :output-to "resources/public/js/simple.js"
                                   :output-dir "resources/public/js/out"
                                   :asset-path "js/out"
                                   :optimizations :none}}

                       {:id "simple"
                        :source-paths ["src/test/clojurescript" "src/test/common"]
                        :compiler {:output-to "target/js/simple.js"
                                   :optimizations :whitespace}}

                       ;; Advanced mode compilation for tests.
                       {:id "advanced"
                        :source-paths ["src/test/clojurescript" "src/test/common"]
                        :compiler {:output-to "target/js/advanced.js"
                                   :anon-fn-naming-policy :mapped
                                   :optimizations :advanced}}]

              :test-commands {"phantom-simple" ["phantomjs"
                                                "src/test/js/runner.js"
                                                "src/test/html/simple.html"]

                              "phantom-advanced" ["phantomjs"
                                                  "src/test/js/runner.js"
                                                  "src/test/html/advanced.html"]}}

  :repl-options {;; The large number of ClojureScript tests is causing long compilation times
                 ;; to start the REPL.
                 :timeout 180000}
  
  ;; Factoring out the duplication of this test selector function causes an error,
  ;; perhaps because Leiningen is using this as uneval'ed code.
  ;; For now just duplicate the line.
  :test-selectors {:default (complement (fn [x]
                                          (let [blacklisted-packages #{"generative" "performance"}
                                                patterns (into []
                                                           (comp
                                                             (map #(str "^clara\\." % ".*"))
                                                             (interpose "|"))
                                                           blacklisted-packages)]
                                            (some->> x :ns ns-name str (re-matches (re-pattern (apply str patterns)))))))
                   :generative (fn [x] (some->> x :ns ns-name str (re-matches #"^clara\.generative.*")))
                   :performance (fn [x] (some->> x :ns ns-name str (re-matches #"^clara\.performance.*")))}
  
  :scm {:name "git"
        :url "https://github.com/cerner/clara-rules"}
  :pom-addition [:developers [:developer
                              [:id "rbrush"]
                              [:name "Ryan Brush"]
                              [:url "http://www.clara-rules.org"]]]
  :deploy-repositories [["snapshots" {:url "https://oss.sonatype.org/content/repositories/snapshots/"
                                      :creds :gpg}]
                        ["clojars" {:url "https://repo.clojars.org"
                                    :username :env/clojars_username
                                    :password :env/clojars_password
                                    :sign-releases false}]])
