#!/bin/bash

mkdir $1

for num in $(eval echo "{1..$2}")
do
	curl -s https://www.cs.colostate.edu/meps/aips02data/$1/Strips/pfile$num --output ./$1/pfile$num.pddl
done
