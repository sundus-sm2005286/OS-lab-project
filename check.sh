#!/bin/bash
# check.sh - find files with 777 permissions and change them to 700
echo "Check script started"
log_file="perm_change.log"  # Define the log file name

# Find files with 777 permissions
find / -type f -perm 777 2>/dev/null | while read -r file; do
    echo "Changing permissions for $file to 700" | tee -a $log_file
    chmod 700 "$file"
done

echo "Permission changes complete. Log saved to $log_file."
echo "Check script finished"