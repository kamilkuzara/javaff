#!/bin/bash

java -cp ./src:./lib/jgrapht-0.8.2/jgrapht-jdk1.6.jar javaff/JavaFF $1 ./problems/$2/domain.pddl ./problems/$2/pfile$3.pddl $4
