#!/bin/bash

sudo apt update
sudo apt install traceroute


target=$1

echo "Running traceroute for $target " | tee -a network.log
traceroute "$target" | tee -a network.log