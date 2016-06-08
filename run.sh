#! /bin/bash
FOLDERNAME="run-$HOSTNAME-$1"

# Hack for multirunner
mkdir $FOLDERNAME
cd $FOLDERNAME
ln -s ../tinycoop-0.1.0.jar .
java -jar tinycoop-0.1.0.jar &> tinycoop.log
