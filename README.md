# JavaFF
This repository contains the implementation of the parallel algorithms proposed as part of the 3rd year individual project entitled "Introducing parallelism to the JavaFF planner". The algorithms developed reside in different branches:

 - Vanilla JavaFF - branch name: main
 - Parallel Computation of Heuristics - branch name: concurrent-heuristics-BFS      (this is the only algorithm described in the report)
 - Parallel Best-First Search - branch name: concurrent-BFS
 - Parallel Enforced Hill-Climbing Search - branch name: parallel-EHC

You are currently in branch: concurrent-heuristics-BFS. In order to switch to a different branch, type:
    git checkout <branch-name>

# Usage
The system comes with a suite of shell scripts designed to make using and testing of the system more convenient. Each script is listed below along with a description of what it does and how to use it.

1. clean.sh - removes all .class files from the workspace, it is recommended to use this script every time before compiling the code; 
    usage: ./clean.sh
2. compile.sh - compiles the source code; 
    usage: ./compile.sh
3. run.sh - runs the program on the given input; takes 3 arguments: domain name, number of the problem file, solution file path (optionally); problems can be selected from the "problem" directory;
    note: the program must be compiled before executing run.sh; 
    usage: ./run.sh <domain-name> <problem-file-number> [<solution-file>]
            e.g. ./run.sh driverlog 8
4. validate.sh - runs the validate program from the "res" directory; note: res/validate must first be made executable (to do this, run: chmod u+x ./res/validate);
    usage: ./validate.sh <domain-name> <problem-file-number> <solution-file>
            e.g. ./validate.sh driverlog 8 plan.sol
5. runall.sh - runs the program for multiple problems with a 10 min limit for a problem, otherwise equivalent to executing run.sh multiple times;
    usage: ./runall.sh <domain-name> <first-problem-file-number> <last-problem-file-number>
            e.g. ./runall.sh driverlog 1 15     <- will run JavaFF on all problems from 1 to 15 (inclusive)

Note, that the scripts may need to be made executable first. To do this, run:
    chmod u+x <script-name>
    e.g. chmod u+x run.sh   <- to make the run.sh script executable

The rest of the scripts have been used for testing purposes and are not needed in order to use JavaFF.

# Problem Files
The repository has been supplied with domains and problem files from the Strips track of the 2002 IPC. They can be found in the "problems" directory. When using any of the shell scripts, where JavaFF is executed, the problems will be selected from this directory.
