#!/bin/bash

for num in {1..20}
do
	curl -s https://www.cs.colostate.edu/meps/aips02data/zenotravel/Strips/pfile$num --output pfile$num.pddl
done
