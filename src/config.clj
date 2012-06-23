(ns config
  (import java.lang.Math))

(def epsilon 0.01)
(def increment 30)
(def quantiles (fn [population-size]
		 [0
		  (int (Math/floor (/ population-size 4)))
		  (int (Math/floor (/ population-size 2)))
		  (int (- population-size (Math/floor (/ population-size 4))))
		  (dec population-size)]))