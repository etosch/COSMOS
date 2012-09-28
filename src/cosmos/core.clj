(ns cosmos.core
  (:require [config])
  (:use [clojure.core.match :only [match]]))

(def last-computed
     "Returns a map of the last computed cosmos data."
     (atom nil))

;; design question - should ords be pre-specified in the config file or should ords be determined through input?
;; have another function called recommended-runs that takes the max over all
;; have a summary function as well? store relevant data in the metadata and write getters for this.
;; implementation of cosmos-1 allows for a single pass across the data stream. this allows the data to be read in from a database or from a file

;; data comes in a lazy seq
;; contents: {:ord 125 :val 0.123 :gen 0}

(defstruct cosmos-data :ord :val :gen)
(defstruct rec :runs :xbar)

(defn converged? [runs xbar new-xbar]
  (and (> runs config/increment)
       (< (/ new-xbar xbar) (+ 1 config/epsilon))
       (> (/ new-xbar xbar) (- 1 config/epsilon))))

(defn recompute-mean [old-mean old-runs-count new-data-point]
  (/ (+ (* old-mean old-runs-count) new-data-point) (inc old-runs-count)))

(defn- return-data [recs converged unconverged]
  (reset! last-computed (with-meta recs {:converged converged :unconverged unconverged})))

;; Note: this would be very nice to have implemented using pattern matching, but due to issues in the :seq testing of core.match, I've opted to use basic destructuring.
(defn cosmos-1
  "Takes a list of maps of keys :ord, :val, :gen as input. Returns a vector whose length is the number of generations and whose contents are maps of the required number of runs and the fitness/error. Information on converged and unconverged [gen ord] pairs contained in the metadata.

Default implementation assumes that data comes in a stream. This means that if all points seen thus far have converged, the algorithm will return. If the algorithm should actually process all data it sees before returning, please specify a second argument set to a value that evaluates to true."
  ([data-seq full-stream?]
     (loop [recs (sorted-map) data data-seq converged #{} unconverged #{}]
       (cond (not (seq data))
	     (return-data recs converged unconverged)
	     (and (not full-stream?) (seq converged) (not (seq unconverged)))
	     (return-data recs converged unconverged)
	     :else (let [[{ord :ord val :val gen :gen} & tail] data]
		     (if (contains? converged [gen ord]) 
		       (recur recs tail converged unconverged)
		       (let [{runs :runs xbar :xbar} (-> recs (get gen {}) (get ord {:runs 0 :xbar 1}))
			     new-xbar (recompute-mean xbar runs val)]
			 (recur (assoc recs gen (assoc (get recs gen) ord {:runs (inc runs) :xbar new-xbar}))
				tail
				(if (converged? runs xbar new-xbar) (conj converged [gen ord]) converged)
				(if (converged? runs xbar new-xbar) (disj unconverged [gen ord]) (conj unconverged [gen ord]))))))
	     )))
  ([data-seq] (cosmos-1 data-seq false)))

(defn- get-converged-data
  "A private helper function for returning the converged data when selecting the maximum over this set."
  [unconverged cd] ;; there should be fewer unconverged than converged
  (let [[gen ord] (first unconverged)]
    (if (empty? unconverged)
      cd
      (recur (rest unconverged) (assoc cd gen (dissoc (get cd gen) ord))))))

(defn recommended-runs
  "Returns the maximum number of recommended runs. Can specify whether we should return the max-value only for the converged runs."
  ([cosmos-data & {:keys [converged-only] :or {converged-only false}}]
     (let [data (or (and (not converged-only) cosmos-data)
		    (get-converged-data (seq (:unconverged (meta cosmos-data))) cosmos-data))]
       (apply max (map :runs (mapcat vals (vals data))))))
  ([] (recommended-runs @last-computed)))


(defn- get-unconverged-data
  "Returns the recommended runs from unconverged generation/ordinal pairs."
  ([cosmos-data]
     (for [[gen ord] (:unconverged (meta cosmos-data))]
       (let [{runs :runs xbar :xbar} (-> cosmos-data (get gen) (get ord))]
	 {:gen gen :ord ord :runs runs :xbar xbar})))
  ([] (get-unconverged-data @last-computed)))
       

(defn recommended-runs-summary
  "Returns the highest generation containing the maximum number of recommended runs. If the highest generation with the maximum number of runs has not converged, can specify that we want the highest generation with the maxmum number of runs that has converged by using keyword :converged-only."
  ([cosmos-data & {:keys [converged-only] :or {converged-only false}}]
     (let [data (or (and (not converged-only) cosmos-data) (get-converged-data (seq (:unconverged (meta cosmos-data))) cosmos-data))
	   max-runs (recommended-runs data)]
       (loop [gens (reverse (sort (keys data)))]
	 (let [candidate (get data (first gens))]
	   (if (contains? (set (map :runs (vals candidate))) max-runs)
	     {:highest-gen {(first gens) candidate}
	      :unconverged-data (get-unconverged-data data)}
	     (recur (rest gens)))))))
  ([] (recommended-runs-summary @last-computed)))