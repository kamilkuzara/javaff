#!/bin/bash

java -cp ./src:./lib/jgrapht-0.8.2/jgrapht-jdk1.6.jar javaff/JavaFF ./problems/$1/domain.pddl ./problems/$1/pfile$2.pddl $3
