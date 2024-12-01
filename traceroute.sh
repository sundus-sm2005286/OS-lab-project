#!/bin/bash


log_status() {
    current_date=$(date +"%Y-%m-%d %H:%M:%S")
    echo "$current_date - $1" | tee -a network.log
}


TARGET=$1


log_status "Displaying routing table"
netstat -rn | tee -a network.log


log_status "Displaying hostname"
hostname | tee -a network.log


log_status "Testing local DNS server"
nslookup google.com | tee -a network.log

# Traceroute to google.com
log_status "Tracing route to google.com"
traceroute google.com | tee -a network.log

# Ping google.com , check internet connection
log_status "Pinging google.com"
ping -c 3 google.com | tee -a network.log
