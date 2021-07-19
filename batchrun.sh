#!/bin/bash

# Arguments:
# $1 - number of threads used
threadsNo=$1

./runall.sh depots 1 22 $threadsNo >> ./testing_printouts/depots-$(eval echo $threadsNo)_threads.txt
./runall.sh driverlog 1 20 $threadsNo >> ./testing_printouts/driverlog-$(eval echo $threadsNo)_threads.txt
./runall.sh freecell 1 20 $threadsNo >> ./testing_printouts/freecell-$(eval echo $threadsNo)_threads.txt
./runall.sh rovers 1 20 $threadsNo >> ./testing_printouts/rovers-$(eval echo $threadsNo)_threads.txt
./runall.sh satellite 1 20 $threadsNo >> ./testing_printouts/satellite-$(eval echo $threadsNo)_threads.txt
./runall.sh zenotravel 1 20 $threadsNo >> ./testing_printouts/zenotravel-$(eval echo $threadsNo)_threads.txt
