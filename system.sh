#!/bin/bash


echo "Generating system info..." > system_info.txt
uname -a >> system_info.txt  


echo "Collecting disk information for HOME directory..." > disk_info.log
df -h ~  


echo "Detailed disk usage for each subdirectory in HOME directory:" >> disk_info.log
du -sh ~/* >> disk_info.log  


echo "Memory Usage (percentage):" >> disk_info.log
free -m | awk 'NR==2{printf "Memory Usage: %.2f%%\n", $3*100/$2}' >> disk_info.log 



echo "Collecting CPU information..." > mem_cpu_info.log
lscpu | grep 'Model name\|CPU(s)' >> mem_cpu_info.log  



echo "System info, disk info, memory info, and CPU info collected."
echo "Files generated: system_info.txt, disk_info.log, mem_cpu_info.log"
