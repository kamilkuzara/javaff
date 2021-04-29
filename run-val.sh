#!/bin/bash

setting=$1
domain=$2
problemNumber=$3
version=$4  # as in "base_version", "parallel_EHC", "parallel_BFS"
runNumber=$5 # there may be more than one solution file for a problem so this is needed
solutionPath="./solutions/$version/$domain-$problemNumber-$runNumber.sol"

./run.sh $setting $domain $problemNumber $solutionPath && ./validate.sh $domain $problemNumber $solutionPath
