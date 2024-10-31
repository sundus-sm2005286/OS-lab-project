#!/bin/bash


OUTPUT_FILE="bigfile.txt"
ADMIN_EMAIL="ta2102604@qu.edu.qa"

# Function to find large files
find_large_files() {
    local search_date=$(date "+%Y-%m-%d %H:%M:%S")
    
    echo "" > "$OUTPUT_FILE"

    echo "Search Date: $search_date" 
    echo "Search Date: $search_date" >> "$OUTPUT_FILE"
    echo "redirecting findings to $OUTPUT_FILE "
    echo "Files larger than 1M:" >> "$OUTPUT_FILE"

    # Find files larger than 1M
    find "$HOME" -type f -size +1M -exec ls -lh {} \; >> "$OUTPUT_FILE" 2>/dev/null

    # Count files
    local file_count=$(grep -c "^-" "$OUTPUT_FILE")
    echo "found $file_count files"
    echo "Total files found: $file_count" >> "$OUTPUT_FILE"
}

# Function to send email
send_email() {
    if [ -s "$OUTPUT_FILE" ]; then
        # Using mail command to send email
        mail -s "Large Files Report" "$ADMIN_EMAIL" < "$OUTPUT_FILE"
    fi
}

# Main
main() {
    find_large_files
    echo "Sending email.."
    send_email
}

# Execute main function
main
