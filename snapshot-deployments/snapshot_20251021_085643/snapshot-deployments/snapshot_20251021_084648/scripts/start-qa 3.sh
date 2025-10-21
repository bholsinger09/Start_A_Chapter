#!/bin/bash

# QA Startup Script with Data Seeding Options
# This script provides options for starting QA environment with different data sets

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
QA_DATA_DIR="$PROJECT_ROOT/qa-data"

echo "🚀 Chapter Organizer QA Environment Startup"
echo "============================================="
echo ""

# Function to start with fresh data
start_fresh() {
    echo "🗑️  Cleaning existing QA data..."
    rm -rf "$QA_DATA_DIR"
    mkdir -p "$QA_DATA_DIR"
    
    echo "🌱 Starting with fresh sample data..."
    cd "$PROJECT_ROOT"
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=qa -Dapp.initialize-sample-data=true
}

# Function to start with existing data
start_existing() {
    if [ ! -f "$QA_DATA_DIR/chapterdb.mv.db" ]; then
        echo "❌ No existing QA database found."
        echo "   Choose option 1 to start fresh or option 3 to import data."
        exit 1
    fi
    
    echo "📊 Starting with existing QA data..."
    cd "$PROJECT_ROOT"
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=qa
}

# Function to start with imported data
start_with_import() {
    echo "📥 Available backup files:"
    if [ -d "$PROJECT_ROOT/database-exports" ]; then
        ls -la "$PROJECT_ROOT/database-exports"/*.sql 2>/dev/null || echo "   No backup files found"
    else
        echo "   No database-exports directory found"
    fi
    echo ""
    
    read -p "Enter backup file path (or 'latest' for latest backup): " backup_file
    
    if [ "$backup_file" = "latest" ]; then
        backup_file="$PROJECT_ROOT/database-exports/latest_backup.sql"
    fi
    
    if [ ! -f "$backup_file" ]; then
        echo "❌ Backup file not found: $backup_file"
        exit 1
    fi
    
    echo "🔄 Importing data and starting..."
    "$SCRIPT_DIR/import-db.sh" "$backup_file"
    
    if [ $? -eq 0 ]; then
        cd "$PROJECT_ROOT"
        ./mvnw spring-boot:run -Dspring-boot.run.profiles=qa
    fi
}

# Main menu
echo "Choose startup option:"
echo "1) Start fresh (clean database with sample data)"
echo "2) Start with existing QA data"
echo "3) Import backup and start"
echo "4) Export current data"
echo "5) Exit"
echo ""

read -p "Enter your choice (1-5): " choice

case $choice in
    1)
        start_fresh
        ;;
    2)
        start_existing
        ;;
    3)
        start_with_import
        ;;
    4)
        echo "📤 Exporting current database..."
        "$SCRIPT_DIR/export-db.sh"
        ;;
    5)
        echo "👋 Goodbye!"
        exit 0
        ;;
    *)
        echo "❌ Invalid choice"
        exit 1
        ;;
esac