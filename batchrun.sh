#!/bin/bash

# Arguments:
# $1 - setting

./runall.sh $1 depots 1 22 >> ./testing_printouts/depots-$1.txt
./runall.sh $1 driverlog 1 20 >> ./testing_printouts/driverlog-$1.txt
./runall.sh $1 freecell 1 20 >> ./testing_printouts/freecell-$1.txt
./runall.sh $1 rovers 1 20 >> ./testing_printouts/rovers-$1.txt
./runall.sh $1 satellite 1 20 >> ./testing_printouts/satellite-$1.txt
./runall.sh $1 zenotravel 1 20 >> ./testing_printouts/zenotravel-$1.txt
