#!/bin/bash

setting=$1
domain=$2
problemNumber=$3
version="base_version"
runNumber=$4 # there may be more than one solution file for a problem so this is needed
solutionPath="./solutions/$version/$domain-$problemNumber-$setting-$runNumber.sol"

./run.sh $setting $domain $problemNumber $solutionPath && ./validate.sh $domain $problemNumber $solutionPath
