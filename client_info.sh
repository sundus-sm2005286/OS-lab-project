#!/bin/bash


LOG_FILE="process_info.log"
SERVER_IP="192.168.177.128"  
SERVER_USER="sondus"         


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

# copy the log file to the server using SCP
copy_to_server() {
    local timestamp=$(date "+%Y%m%d_%H%M%S")
    local remote_file="process_info_${timestamp}.log"

    # Use SCP to transfer the log file securely
    scp "$LOG_FILE" "$SERVER_USER@$SERVER_IP:$remote_file"
    
    if [ $? -ne 0 ]; then
        echo "Transferring file was NOT successful, please check credentials or SSH connection."
    else
        echo "File transferred successfully to $SERVER_USER@$SERVER_IP:$remote_file"
    fi
}

# Main 
main() {
    gather_process_info
    copy_to_server
}

# Run the main function every hour
while true; do
    main
    echo "Sleeping for 1 hour..."
    sleep 3600 
done
