#!/bin/bash

domain=$1
problemNumber=$2
version="concurrent_heuristics_BFS"
threadsNo=$3
solutionPath="./solutions/$version/$domain-$problemNumber-$(eval echo $threadsNo)_threads.sol"

./run.sh $domain $problemNumber $solutionPath && ./validate.sh $domain $problemNumber $solutionPath
