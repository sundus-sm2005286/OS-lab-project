#!/bin/bash

sudo apt-get install net-tools


target=$1 


ping -c 3 -W 3 $target #time out 3
if [ $? -eq 0 ]

then
current_date=$(date +"%Y-%m-%d %H:%M:%S")
echo $current_date >> network.log
echo "Connectivity of $target was successfully" | tee -a network.log
else
current_date=$(date +"%Y-%m-%d %H:%M:%S")
echo $current_date >> network.log
echo "Target: $target is not responding. Running traceroute.sh" 
./traceroute.sh $target
fi 
