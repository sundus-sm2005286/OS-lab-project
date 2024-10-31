#!/bin/bash


LOG_FILE="process_info.log"
SERVER_IP="192.168.177.129"
SERVER_USER="sundus"

# Function to gather process information
gather_process_info() {
    local timestamp=$(date "+%Y-%m-%d %H:%M:%S")

    
    echo "" > "$LOG_FILE"

    echo "Process Information - $timestamp" >> "$LOG_FILE"
    echo "===============================" >> "$LOG_FILE"

    echo "Process Tree:" >> "$LOG_FILE"
    pstree >> "$LOG_FILE"
   
    echo -e "\nDead/Zombie Processes:" >> "$LOG_FILE"
    ps aux | awk '$8=="Z"' >> "$LOG_FILE"
   
    echo -e "\nCPU Usage by Process:" >> "$LOG_FILE"
    ps aux --sort=-%cpu | head -n 11 >> "$LOG_FILE"
    
    echo -e "\nMemory Usage by Process:" >> "$LOG_FILE"
    ps aux --sort=-%mem | head -n 11 >> "$LOG_FILE"
    
    echo -e "\nTop 5 Resource-Consuming Processes:" >> "$LOG_FILE"
    ps aux --sort=-%cpu,%mem | head -n 6 >> "$LOG_FILE"
}

# Function to copy log to server
copy_to_server() {
    local timestamp=$(date "+%Y%m%d_%H%M%S")
    local remote_file="process_info_${timestamp}.log"
    scp "$LOG_FILE" "$SERVER_USER@$SERVER_IP:$remote_file" 2>/dev/null
    if [ $? -ne 0 ];
    then
    echo "Transferring file was NOT successfull, please check creds"
    else
    echo "File transferred successfully"
    fi
}

# Main 
main() {
    gather_process_info
    copy_to_server
}

MIN_TO_SEC=60
HOUR_TO_MIN=60
hour_to_sec=$(echo "$MIN_TO_SEC * $HOUR_TO_MIN" | bc)
# Create cron job for hourly execution
while true; do
main
echo "Sleeping...."
sleep $hour_to_sec
done 


