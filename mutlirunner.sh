#! /bin/bash
for RUN_NUM in 1 2 3 4 5 6 7 8
do
	nohup ./run.sh $RUN_NUM &
done
