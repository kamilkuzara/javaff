#!/bin/bash

# Arguments:
# $1 - problem domain
# $2 - first problem number
# $3 - last problem number (inclusive)
# $4 - number of threads

for num in $(eval echo "{$2..$3}")
do
	echo "-------------------------------------- $1 - $num --------------------------------------------"
	timeout --kill-after=10m 10m ./run-val.sh $1 $num $4
	echo "---------------------------------------------------------------------------------------------"
	echo ""
	echo ""
	sleep 10s
done
