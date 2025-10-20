#!/bin/bash

# Import Database Script for QA Environment
# This script imports a SQL backup file into the QA database

QA_DATA_DIR="./qa-data"
BACKUP_FILE="$1"

if [ -z "$BACKUP_FILE" ]; then
    echo "❌ Usage: $0 <backup_file.sql>"
    echo "   Example: $0 ./database-exports/qa_backup_20241020_143000.sql"
    echo "   Or use latest: $0 ./database-exports/latest_backup.sql"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "❌ Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "🗄️  Starting database import..."
echo "📁 Source: $BACKUP_FILE"
echo "🎯 Target: $QA_DATA_DIR/chapterdb"

# Create QA data directory if it doesn't exist
mkdir -p "$QA_DATA_DIR"

# Stop any running application first
echo "⚠️  Please ensure the application is stopped before importing"
read -p "Continue with import? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Import cancelled"
    exit 1
fi

# Remove existing database files
if [ -f "$QA_DATA_DIR/chapterdb.mv.db" ]; then
    echo "🗑️  Removing existing database files..."
    rm -f "$QA_DATA_DIR/chapterdb"*.db
fi

echo "📥 Importing data from backup..."

# Use H2's RunScript tool to import
java -cp ~/.m2/repository/com/h2database/h2/*/h2-*.jar org.h2.tools.RunScript \
    -url "jdbc:h2:file:$QA_DATA_DIR/chapterdb" \
    -user sa \
    -password "" \
    -script "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    echo "✅ Database imported successfully!"
    echo ""
    echo "🚀 To start the application with the imported data:"
    echo "   ./mvnw spring-boot:run -Dspring-boot.run.profiles=qa"
    echo ""
    echo "🌐 H2 Console will be available at:"
    echo "   http://localhost:8080/h2-console"
    echo "   JDBC URL: jdbc:h2:file:./qa-data/chapterdb"
    echo "   User: sa"
    echo "   Password: (leave empty)"
else
    echo "❌ Database import failed"
    exit 1
fi