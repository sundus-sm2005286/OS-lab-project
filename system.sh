#!/bin/bash

df -h >  disk_info.log
free -m | awk 'NR==2{printf"Memory Usage: %s/%sMB (%.2f%%)\n,$3,$2,$3*100/$2}' >> disk_info.log
lscpu | grep 'Model name\|CPU(s)' >> mem_cpu_info.log