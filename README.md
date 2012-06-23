# COSMOS
## First pass general use implementation at the initial algorithm presented in [(Tosch, Spector 2012)](http://people.cs.umass.edu/~etosch/papers/cosmos_paper.pdf).

The main functions of interested are 
* ```cosmos-1```, which takes a list of entries of cosmos-data structs
* ```recommended-runs```, which takes the output of ```cosmos-1``` and returns the maximum number of runs recommended. There is the option to compute this only over the converged generation/ordinal pairs
* ```recommended-runs-summary```, which takes the output of ```cosmos-1``` and returns summary data for the generation that produced the maximum number of recommended runs, as well as informtion about the unconverged generations and their ordinals.

To use in a steady-state model, set all generations equal to the same number.

For conveneince, ```cosmos-1``` sets a global variable called ```last-computed``` to hold the last comuted cosmos data.
