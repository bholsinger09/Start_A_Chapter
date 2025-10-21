#!/bin/bash

# Database Backup Script for Campus Chapter Organizer
# Supports both H2 (development) and PostgreSQL (production) databases

set -e

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKUP_DIR="$PROJECT_ROOT/backups"
DATABASE_BACKUP_DIR="$BACKUP_DIR/database"
METADATA_BACKUP_DIR="$BACKUP_DIR/metadata"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
MAX_BACKUPS=48  # Keep 48 backups (24 hours at 30-minute intervals)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if Spring Boot app is running
check_app_status() {
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Create H2 database backup
create_h2_backup() {
    local backup_name="h2_backup_${TIMESTAMP}"
    local backup_file="${DATABASE_BACKUP_DIR}/${backup_name}.zip"
    
    log "Creating H2 database backup: $backup_name" >&2
    
    # Check if app is running to get current data
    if check_app_status; then
        log "Application is running. Creating backup via H2 export..." >&2
        
        # Use curl to trigger H2 export via SQL command
        # This creates a SQL dump that can be imported later
        local export_sql="SCRIPT TO '${DATABASE_BACKUP_DIR}/${backup_name}.sql'"
        
        # Alternative: Copy the H2 database files if they exist
        if [ -f "$PROJECT_ROOT/chapterdb.mv.db" ]; then
            log "Backing up H2 database files..." >&2
            cd "$PROJECT_ROOT"
            zip -q "$backup_file" chapterdb.mv.db chapterdb.trace.db 2>/dev/null || true
        else
            warning "H2 database files not found. Application may be using in-memory database." >&2
            # Create empty backup for consistency
            touch "${DATABASE_BACKUP_DIR}/${backup_name}.empty"
        fi
    else
        log "Application not running. Checking for existing H2 files..." >&2
        if [ -f "$PROJECT_ROOT/chapterdb.mv.db" ]; then
            cd "$PROJECT_ROOT"
            zip -q "$backup_file" chapterdb.mv.db chapterdb.trace.db 2>/dev/null || true
        else
            warning "No H2 database files found." >&2
            touch "${DATABASE_BACKUP_DIR}/${backup_name}.empty"
        fi
    fi
    
    echo "$backup_name"
}

# Create PostgreSQL database backup (for production)
create_postgresql_backup() {
    local backup_name="postgresql_backup_${TIMESTAMP}"
    local backup_file="${DATABASE_BACKUP_DIR}/${backup_name}.sql"
    
    log "Creating PostgreSQL database backup: $backup_name"
    
    # Read database connection details from environment or config
    local db_host="${POSTGRES_HOST:-localhost}"
    local db_port="${POSTGRES_PORT:-5432}"
    local db_name="${POSTGRES_DB:-chapterdb}"
    local db_user="${POSTGRES_USER:-postgres}"
    
    # Create PostgreSQL dump
    if command -v pg_dump > /dev/null 2>&1; then
        PGPASSWORD="${POSTGRES_PASSWORD}" pg_dump \
            -h "$db_host" \
            -p "$db_port" \
            -U "$db_user" \
            -d "$db_name" \
            --no-password \
            --clean \
            --create \
            --if-exists \
            > "$backup_file"
        
        # Compress the backup
        gzip "$backup_file"
        backup_file="${backup_file}.gz"
    else
        error "pg_dump not found. Please install PostgreSQL client tools."
        return 1
    fi
    
    echo "$backup_name"
}

# Detect database type and create appropriate backup
create_backup() {
    log "Starting database backup process..." >&2
    
    local backup_name=""
    # Check which database is configured
    if check_app_status; then
        # Query the application to determine database type
        local health_response=$(curl -s http://localhost:8080/actuator/health 2>/dev/null || echo "")
        if echo "$health_response" | grep -q "postgresql\|postgres"; then
            backup_name=$(create_postgresql_backup)
        else
            backup_name=$(create_h2_backup)
        fi
    else
        # Default to H2 for development
        log "Application not running. Defaulting to H2 backup..." >&2
        backup_name=$(create_h2_backup)
    fi
    
    echo "$backup_name"
}

# Create metadata file for the backup
create_metadata() {
    local backup_name="$1"
    local metadata_file="${METADATA_BACKUP_DIR}/${backup_name}.json"
    
    log "Creating metadata for backup: $backup_name"
    
    # Get application info if available
    local app_info="{}"
    if check_app_status; then
        app_info=$(curl -s http://localhost:8080/actuator/info 2>/dev/null || echo "{}")
    fi
    
    # Get current git commit if in a git repository
    local git_commit="unknown"
    local git_branch="unknown"
    if git rev-parse --git-dir > /dev/null 2>&1; then
        git_commit=$(git rev-parse HEAD 2>/dev/null || echo "unknown")
        git_branch=$(git branch --show-current 2>/dev/null || echo "unknown")
    fi
    
    # Create metadata JSON
    cat > "$metadata_file" << EOF
{
    "backup_name": "$backup_name",
    "timestamp": "$TIMESTAMP",
    "date_created": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")",
    "database_type": "$(echo "$backup_name" | cut -d'_' -f1)",
    "application_status": "$(check_app_status && echo "running" || echo "stopped")",
    "git_commit": "$git_commit",
    "git_branch": "$git_branch",
    "application_info": $app_info,
    "backup_size": "$(du -h "${DATABASE_BACKUP_DIR}/${backup_name}"* 2>/dev/null | head -n1 | cut -f1 || echo "unknown")",
    "created_by": "$(whoami)@$(hostname)"
}
EOF
    
    success "Metadata created: $metadata_file"
}

# Clean up old backups
cleanup_old_backups() {
    log "Cleaning up old backups (keeping last $MAX_BACKUPS)..."
    
    # Clean up database backups
    local backup_count=$(ls -1 "$DATABASE_BACKUP_DIR" 2>/dev/null | wc -l)
    if [ "$backup_count" -gt "$MAX_BACKUPS" ]; then
        local files_to_delete=$((backup_count - MAX_BACKUPS))
        log "Found $backup_count backups. Removing $files_to_delete oldest backups..."
        
        # Remove oldest database backups
        ls -t "$DATABASE_BACKUP_DIR"/* 2>/dev/null | tail -n "$files_to_delete" | xargs rm -f
        
        # Remove corresponding metadata files
        for metadata_file in $(ls -t "$METADATA_BACKUP_DIR"/* 2>/dev/null | tail -n "$files_to_delete"); do
            rm -f "$metadata_file"
        done
        
        success "Cleaned up $files_to_delete old backups"
    else
        log "Only $backup_count backups found. No cleanup needed."
    fi
}

# Main execution
main() {
    log "=== Database Backup Process Started ==="
    
    # Ensure directories exist
    mkdir -p "$DATABASE_BACKUP_DIR" "$METADATA_BACKUP_DIR"
    
    # Create backup
    local backup_name
    backup_name=$(create_backup)
    
    if [ -n "$backup_name" ] && [[ ! "$backup_name" =~ ^\[.*\] ]]; then
        # Create metadata
        create_metadata "$backup_name"
        
        # Clean up old backups
        cleanup_old_backups
        
        success "=== Backup Process Completed Successfully ==="
        success "Backup name: $backup_name"
        success "Backup location: $DATABASE_BACKUP_DIR"
        
        # List recent backups
        log "Recent backups:"
        ls -lt "$DATABASE_BACKUP_DIR" | head -n 5
    else
        error "=== Backup Process Failed ==="
        error "Invalid backup name: $backup_name"
        exit 1
    fi
}

# Run main function if script is executed directly
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi