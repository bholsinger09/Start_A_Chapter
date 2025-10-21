#!/bin/bash

# Export Database Script for QA Environment
# This script exports the current database to SQL files for backup/restore

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="./database-exports"
QA_DATA_DIR="./qa-data"

echo "🗄️  Starting database export..."

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Check if QA database exists
if [ ! -f "$QA_DATA_DIR/chapterdb.mv.db" ]; then
    echo "❌ QA database not found at $QA_DATA_DIR/chapterdb.mv.db"
    echo "   Please run the application with 'qa' profile first to create the database"
    exit 1
fi

echo "📊 Exporting database schema and data..."

# Use H2's built-in SCRIPT command to export
java -cp ~/.m2/repository/com/h2database/h2/*/h2-*.jar org.h2.tools.Script \
    -url "jdbc:h2:file:$QA_DATA_DIR/chapterdb" \
    -user sa \
    -password "" \
    -script "$BACKUP_DIR/qa_backup_$TIMESTAMP.sql"

if [ $? -eq 0 ]; then
    echo "✅ Database exported successfully to: $BACKUP_DIR/qa_backup_$TIMESTAMP.sql"
    
    # Create a symlink to the latest backup
    ln -sf "qa_backup_$TIMESTAMP.sql" "$BACKUP_DIR/latest_backup.sql"
    echo "🔗 Latest backup symlink created: $BACKUP_DIR/latest_backup.sql"
    
    # List recent backups
    echo ""
    echo "📋 Recent backups:"
    ls -la "$BACKUP_DIR"/qa_backup_*.sql | tail -5
else
    echo "❌ Database export failed"
    exit 1
fi

echo ""
echo "🎉 Export completed!"
echo "   To restore this backup later, run: ./scripts/import-db.sh $BACKUP_DIR/qa_backup_$TIMESTAMP.sql"