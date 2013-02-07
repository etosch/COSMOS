(ns cosmos.config
  (import java.lang.Math))

(def ^:dynamic epsilon (atom 0.01))
(def ^:dynamic increment 30)
(def ^:dynamic quantiles (fn [population-size]
                           [0
                            (int (Math/floor (/ population-size 4)))
                            (int (Math/floor (/ population-size 2)))
                            (int (- population-size (Math/floor (/ population-size 4))))
                            (dec population-size)]))