#!/bin/bash


OUTPUT_FILE="bigfile.txt"
ADMIN_EMAIL="sm2005286@qu.edu.qa"  # Replace with actual email


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
    echo "found about $file_count files"
    echo "Total files found: $file_count" >> "$OUTPUT_FILE"
}

#send email
send_email() {
    if [ -s "$OUTPUT_FILE" ]; then

        mail -s "Large Files Report" "$ADMIN_EMAIL" < "$OUTPUT_FILE"
    fi
}


main() {
    find_large_files
    echo "Sending email..."
    send_email
}


main