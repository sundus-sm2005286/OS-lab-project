#!/bin/bash


is_valid_ip() {
    local ip=$1
    if [[ "$ip" =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        local IFS="."
        local -a octets
        read -r -a octets <<< "$ip"
        for octet in "${octets[@]}"; do
            if (( octet < 0 || octet > 255 )); then
                return 1  # Invalid IP address
            fi
        done
        return 0  
    else
        return 1 
    fi
}

target=$1


if ! is_valid_ip "$target"; then
    echo "Invalid IP address format: $target"
    exit 1
fi

# pinging and traceroute
ping -c 3 -W 3 $target
if [ $? -eq 0 ]; then
    current_date=$(date +"%Y-%m-%d %H:%M:%S")
    echo $current_date >> network.log
    echo "Connectivity with $target is ok" | tee -a network.log
else
    current_date=$(date +"%Y-%m-%d %H:%M:%S")
    echo $current_date >> network.log
    echo "Target: $target is not responding. Running traceroute.sh" 
    ./traceroute.sh $target
fi
