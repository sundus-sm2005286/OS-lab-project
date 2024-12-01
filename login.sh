#!/bin/bash

echo "Login script started"

MAX_LOGIN_ATTEMPTS=3
ATTEMPT_LOG="invalid_attempts.log"
SERVER_USER="sondus"  
SERVER_ADDR="192.168.177.128"   # Server IP 

record_invalid_attempt() {
    local user="$1"
    echo "$(date "+%Y-%m-%d %H:%M:%S") - Failed login attempt by user: $user" >> "$ATTEMPT_LOG"
}

upload_log_to_server() {
    local timestamp=$(date "+%Y%m%d_%H%M%S")
    local remote_log="client_${timestamp}_invalid_attempts.log"
    
    echo "Uploading log to server..."
    sftp "$SERVER_USER@$SERVER_ADDR" <<EOF
put "$ATTEMPT_LOG" "$remote_log" || echo "SFTP upload failed"
quit
EOF
}

initiate_logout() {
    echo "Logging out in 30 seconds..."
    at now + 30 seconds <<EOF
gnome-session-quit --force && echo "Logout initiated" || echo "Logout failed"
EOF
}


login_process() {
    local attempt_count=1
    
    while [ $attempt_count -le $MAX_LOGIN_ATTEMPTS ]; do
        read -p "Enter username: " user
        read -s -p "Enter password: " pass
        echo
        
        # SSH login attempt
        if sshpass -p "$pass" ssh -o StrictHostKeyChecking=no -o ConnectTimeout=10 "$user@$SERVER_ADDR" exit 2>/dev/null; then
            echo "Login successful!"
            exit 0
        else
            record_invalid_attempt "$user"
            echo "Incorrect credentials. Attempt $attempt_count of $MAX_LOGIN_ATTEMPTS"
            ((attempt_count++))
        fi
    done
    
    echo "Unauthorized user!"
    upload_log_to_server
    initiate_logout
}

# Main/start
login_process
echo "Login script finished"