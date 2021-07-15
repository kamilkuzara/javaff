#!/bin/bash

setting=$1
domain=$2
problemNumber=$3
version="base_version"
solutionPath="./solutions/$version/$domain-$problemNumber-$setting.sol"

./run.sh $setting $domain $problemNumber $solutionPath && ./validate.sh $domain $problemNumber $solutionPath
