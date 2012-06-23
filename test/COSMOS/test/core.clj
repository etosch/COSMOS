(ns COSMOS.test.core
  (:use [COSMOS.core])
  (:use [clojure.test]))

(deftest nil-stream
  (is {} (cosmos-1 '())))

(deftest cosmos-1-1
  (is {2 {0 {:runs 2, :xbar 3/2}}} (cosmos-1 '({:ord 0 :val 1 :gen 2}{:ord 0 :val 2 :gen 2}))))

(deftest cosmos-1-2
  (is 
   {3 {0 {:runs 1, :xbar 2}}, 2 {0 {:runs 1, :xbar 1}}}
   (cosmos-1 '({:ord 0 :val 1 :gen 2}{:ord 0 :val 2 :gen 3}))))

(deftest cosmos-1-3
  (is
   {2 {125 {:runs 1, :xbar 2}, 0 {:runs 1, :xbar 1}}}
   (cosmos-1 '({:ord 0 :val 1 :gen 2}{:ord 125 :val 2 :gen 2}))))

(deftest cosmos-1-4
  (is true
      (let [{c :converged u :unconverged} (meta (cosmos-1 '({:ord 0 :val 1 :gen 2}{:ord 125 :val 2 :gen 2})))]
	(and (= 0 (count c)) (= 2 (count u))))))

(deftest cosmos-1-5
  (is
   {0 {0 {:runs 32, :xbar 0.9981875}}}
   (cosmos-1 (concat (repeat 10 {:ord 0 :val 0.999 :gen 0})
		     (repeat 10 {:ord 0 :val 0.998 :gen 0})
		     (repeat 10 {:ord 0 :val 0.997 :gen 0})
		     (repeat 10 {:ord 0 :val 1.001 :gen 0})))))

(deftest cosmos-1-6
  (is true
      (let [{c :converged u :unconverged} (meta (cosmos-1 (concat (repeat 10 {:ord 0 :val 0.999 :gen 0})
								  (repeat 10 {:ord 0 :val 0.998 :gen 0})
								  (repeat 10 {:ord 0 :val 0.997 :gen 0})
								  (repeat 10 {:ord 0 :val 1.001 :gen 0}))))]
	(and (= 1 (count c)) (= 0 (count u))))))

(deftest cosmos-1-7
  (is {0 {125 {:runs 15, :xbar 23}, 0 {:runs 32, :xbar 0.9981875}}}
      (cosmos-1 (concat (repeat 10 {:ord 0 :val 0.999 :gen 0})
			(repeat 10 {:ord 0 :val 0.998 :gen 0})
			(repeat 10 {:ord 0 :val 0.997 :gen 0})
			(repeat 10 {:ord 0 :val 1.001 :gen 0})
			(repeat 15 {:ord 125 :val 23 :gen 0}))
		true)))

(deftest cosmos-1-8
  (is true
      (let [{c :converged u :unconverged} (meta (cosmos-1 (concat (repeat 10 {:ord 0 :val 0.999 :gen 0})
								  (repeat 10 {:ord 0 :val 0.998 :gen 0})
								  (repeat 10 {:ord 0 :val 0.997 :gen 0})
								  (repeat 10 {:ord 0 :val 1.001 :gen 0})
								  (repeat 15 {:ord 125 :val 23 :gen 0}))
							  true))]
	(and (= 1 (count c)) (= 1 (count u))))))