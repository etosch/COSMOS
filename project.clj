(defproject cosmos "1.0.0-SNAPSHOT"
  :description "A Utility for checking whether a set of experiments meets the COSMOS criterion"
  :dependencies [[org.clojure/clojure "1.3.0"]
		 [matchure "0.10.1"]
		 [org.clojure/core.match "0.2.0-alpha9"]
		 ]
  :plugins [[lein-swank "1.4.4"]]
  :main cosmos.core)