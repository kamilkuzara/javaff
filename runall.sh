#!/bin/bash

#arguments:
# $1 - setting
# $2 - problem domain, e.g. driverlog
# $3 - first problem number
# $4 - last problem number (inclusive)

for num in $(eval echo "{$3..$4}")
do
	echo "-------------------------------------- $2 - $num --------------------------------------------"
	timeout --kill-after=10m 10m ./run-val.sh $1 $2 $num
	echo "---------------------------------------------------------------------------------------------"
	echo ""
	echo ""
	sleep 10s
done
