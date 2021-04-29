#!/bin/bash

domain=$1
problemNumber=$2
version="concurrent_heuristics_BFS"
runNumber=$3 # there may be more than one solution file for a problem so this is needed
solutionPath="./solutions/$version/$domain-$problemNumber-$runNumber.sol"

./run.sh $domain $problemNumber $solutionPath && ./validate.sh $domain $problemNumber $solutionPath
