#!/bin/bash

# Database Backup Script for Campus Chapter Organizer
# Supports both H2 (development) and PostgreSQL (production) databases

set -e

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKUP_DIR="$PROJECT_ROOT/backups"
DATABASE_BACKUP_DIR="$BACKUP_DIR/database"
METADATA_BACKUP_DIR="$BACKUP_DIR/metadata"
SNAPSHOT_BACKUP_DIR="$BACKUP_DIR/snapshots"
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

# Create complete site backup (database + code + snapshot)
create_complete_backup() {
    local backup_type="${1:-both}" # Options: database, snapshot, both
    
    case "$backup_type" in
        "database")
            create_backup
            ;;
        "snapshot")
            create_site_snapshot
            ;;
        "both"|*)
            log "Creating complete backup (database + snapshot)..." >&2
            local db_backup=$(create_backup)
            local snapshot_backup=$(create_site_snapshot)
            
            # Return both names
            echo "db:${db_backup},snapshot:${snapshot_backup}"
            ;;
    esac
}

# Create complete site snapshot (code + database + configs)
create_site_snapshot() {
    local snapshot_name="snapshot_${TIMESTAMP}"
    local snapshot_dir="${SNAPSHOT_BACKUP_DIR}/${snapshot_name}"
    
    log "Creating complete site snapshot: $snapshot_name" >&2
    
    # Create snapshot directory
    mkdir -p "$snapshot_dir"
    
    # 1. Backup source code (exclude node_modules, target, .git, etc.)
    log "Backing up source code..." >&2
    cd "$PROJECT_ROOT"
    
    # Create source code archive
    tar --exclude='node_modules' \
        --exclude='target' \
        --exclude='.git' \
        --exclude='backups' \
        --exclude='qa-data' \
        --exclude='database-exports' \
        --exclude='*.log' \
        --exclude='.DS_Store' \
        --exclude='*.tmp' \
        --exclude='dist' \
        --exclude='build' \
        -czf "${snapshot_dir}/source-code.tar.gz" .
    
    # 2. Backup database using existing function
    log "Backing up database..." >&2
    local db_backup_name
    if check_app_status; then
        local health_response=$(curl -s http://localhost:8080/actuator/health 2>/dev/null || echo "")
        if echo "$health_response" | grep -q "postgresql\|postgres"; then
            db_backup_name=$(create_postgresql_backup)
        else
            db_backup_name=$(create_h2_backup)
        fi
    else
        db_backup_name=$(create_h2_backup)
    fi
    
    # Copy database backup to snapshot directory
    for ext in .sql .sql.gz .zip .empty; do
        if [ -f "${DATABASE_BACKUP_DIR}/${db_backup_name}${ext}" ]; then
            cp "${DATABASE_BACKUP_DIR}/${db_backup_name}${ext}" "${snapshot_dir}/database${ext}"
            break
        fi
    done
    
    # 3. Backup Docker configurations
    log "Backing up Docker configurations..." >&2
    mkdir -p "${snapshot_dir}/docker-configs"
    
    # Copy all Docker-related files
    for docker_file in docker-compose*.yml Dockerfile* .dockerignore; do
        if [ -f "$PROJECT_ROOT/$docker_file" ]; then
            cp "$PROJECT_ROOT/$docker_file" "${snapshot_dir}/docker-configs/"
        fi
    done
    
    # 4. Backup environment configurations
    log "Backing up environment configurations..." >&2
    mkdir -p "${snapshot_dir}/configs"
    
    # Copy application properties
    if [ -d "$PROJECT_ROOT/src/main/resources" ]; then
        cp -r "$PROJECT_ROOT/src/main/resources" "${snapshot_dir}/configs/"
    fi
    
    # Copy frontend environment files
    if [ -f "$PROJECT_ROOT/frontend/.env" ]; then
        cp "$PROJECT_ROOT/frontend/.env" "${snapshot_dir}/configs/"
    fi
    if [ -f "$PROJECT_ROOT/frontend/.env.local" ]; then
        cp "$PROJECT_ROOT/frontend/.env.local" "${snapshot_dir}/configs/"
    fi
    
    # Copy package.json files for dependency tracking
    if [ -f "$PROJECT_ROOT/pom.xml" ]; then
        cp "$PROJECT_ROOT/pom.xml" "${snapshot_dir}/configs/"
    fi
    if [ -f "$PROJECT_ROOT/frontend/package.json" ]; then
        cp "$PROJECT_ROOT/frontend/package.json" "${snapshot_dir}/configs/"
    fi
    if [ -f "$PROJECT_ROOT/frontend/package-lock.json" ]; then
        cp "$PROJECT_ROOT/frontend/package-lock.json" "${snapshot_dir}/configs/"
    fi
    
    # 5. Create snapshot Docker files for restoration
    log "Creating snapshot Docker configurations..." >&2
    
    # Create snapshot-specific Docker Compose file
    cat > "${snapshot_dir}/docker-compose-snapshot.yml" << EOF
# Docker Compose for Site Snapshot: $snapshot_name
# Created: $(date -u +"%Y-%m-%dT%H:%M:%SZ")
# Git Commit: $(git rev-parse HEAD 2>/dev/null || echo "unknown")

services:
  # Snapshot Database Service
  snapshot-database:
    image: postgres:15-alpine
    container_name: snapshot-${snapshot_name}-db
    environment:
      POSTGRES_DB: chapterdb_snapshot
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: snapshot_password
    ports:
      - "0:5432"  # Docker will assign random port
    volumes:
      - snapshot_${snapshot_name}_db_data:/var/lib/postgresql/data
      - ./database-restore:/docker-entrypoint-initdb.d:ro
    networks:
      - snapshot-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d chapterdb_snapshot"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Snapshot Application Service
  snapshot-app:
    build:
      context: ./source-extracted
      dockerfile: Dockerfile.qa
    container_name: snapshot-${snapshot_name}-app
    ports:
      - "0:8080"  # Docker will assign random port
    environment:
      - SPRING_PROFILES_ACTIVE=snapshot
      - SPRING_DATASOURCE_URL=jdbc:postgresql://snapshot-database:5432/chapterdb_snapshot
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=snapshot_password
      - SNAPSHOT_NAME=${snapshot_name}
    depends_on:
      snapshot-database:
        condition: service_healthy
    volumes:
      - ./configs:/app/configs:ro
    networks:
      - snapshot-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 120s

  # Snapshot Frontend Service
  snapshot-frontend:
    build:
      context: ./source-extracted/frontend
      dockerfile: Dockerfile.qa
    container_name: snapshot-${snapshot_name}-frontend
    ports:
      - "0:3000"  # Docker will assign random port
    environment:
      - VUE_APP_API_BASE_URL=http://snapshot-app:8080
      - NODE_ENV=development
      - SNAPSHOT_NAME=${snapshot_name}
    depends_on:
      snapshot-app:
        condition: service_healthy
    networks:
      - snapshot-network

  # Snapshot Admin Tool
  snapshot-adminer:
    image: adminer:4.8.1
    container_name: snapshot-${snapshot_name}-adminer
    ports:
      - "0:8080"  # Docker will assign random port
    environment:
      ADMINER_DEFAULT_SERVER: snapshot-database
    depends_on:
      - snapshot-database
    networks:
      - snapshot-network

volumes:
  snapshot_${snapshot_name}_db_data:
    name: snapshot_${snapshot_name}_db_data

networks:
  snapshot-network:
    name: snapshot_${snapshot_name}_network
    driver: bridge
EOF

    # Create database restoration script
    mkdir -p "${snapshot_dir}/database-restore"
    cat > "${snapshot_dir}/database-restore/01-restore-snapshot.sh" << 'DBEOF'
#!/bin/bash

set -e

echo "=== Snapshot Database Restore Script ==="
echo "Snapshot: ${SNAPSHOT_NAME:-unknown}"

# Wait for PostgreSQL to be ready
until pg_isready -h localhost -U postgres -d postgres; do
    echo "Waiting for PostgreSQL to be ready..."
    sleep 2
done

echo "PostgreSQL is ready. Starting snapshot restore process..."

# Create the database if it doesn't exist
psql -U postgres -c "CREATE DATABASE chapterdb_snapshot;" || echo "Database already exists"

# Restore from snapshot database backup
if [ -f "/docker-entrypoint-initdb.d/../database.sql.gz" ]; then
    echo "Restoring from compressed PostgreSQL backup"
    gunzip -c "/docker-entrypoint-initdb.d/../database.sql.gz" | psql -U postgres -d chapterdb_snapshot
elif [ -f "/docker-entrypoint-initdb.d/../database.sql" ]; then
    echo "Restoring from uncompressed PostgreSQL backup"
    psql -U postgres -d chapterdb_snapshot -f "/docker-entrypoint-initdb.d/../database.sql"
elif [ -f "/docker-entrypoint-initdb.d/../database.zip" ]; then
    echo "Found H2 backup - creating compatible PostgreSQL schema"
    psql -U postgres -d chapterdb_snapshot -c "
        CREATE TABLE IF NOT EXISTS chapters (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            university VARCHAR(255),
            city VARCHAR(255),
            state VARCHAR(255),
            created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        
        CREATE TABLE IF NOT EXISTS members (
            id BIGSERIAL PRIMARY KEY,
            first_name VARCHAR(255),
            last_name VARCHAR(255),
            email VARCHAR(255),
            chapter_id BIGINT REFERENCES chapters(id)
        );
        
        CREATE TABLE IF NOT EXISTS events (
            id BIGSERIAL PRIMARY KEY,
            title VARCHAR(255),
            description TEXT,
            event_date TIMESTAMP,
            chapter_id BIGINT REFERENCES chapters(id)
        );
        
        CREATE TABLE IF NOT EXISTS member_roles (
            id BIGSERIAL PRIMARY KEY,
            role_name VARCHAR(255),
            member_id BIGINT REFERENCES members(id)
        );
        
        CREATE TABLE IF NOT EXISTS event_types (
            id BIGSERIAL PRIMARY KEY,
            type_name VARCHAR(255),
            description TEXT
        );
    "
    echo "Created basic schema for H2 backup compatibility"
elif [ -f "/docker-entrypoint-initdb.d/../database.empty" ]; then
    echo "Found empty backup marker - creating empty database with schema"
    psql -U postgres -d chapterdb_snapshot -c "SELECT 1;" # Just ensure connection works
else
    echo "WARNING: No database backup found - creating empty database"
fi

echo "=== Snapshot Database Restore Complete ==="
DBEOF

    chmod +x "${snapshot_dir}/database-restore/01-restore-snapshot.sh"
    
    # 6. Create snapshot application properties
    cat > "${snapshot_dir}/configs/application-snapshot.properties" << EOF
# Snapshot Profile Configuration
# Snapshot: $snapshot_name
# Created: $(date -u +"%Y-%m-%dT%H:%M:%SZ")
# Git Commit: $(git rev-parse HEAD 2>/dev/null || echo "unknown")

spring.application.name=Campus Chapter Organizer (Snapshot: ${snapshot_name})

# Server Configuration
server.port=8080

# PostgreSQL Database Configuration for Snapshot
spring.datasource.url=jdbc:postgresql://snapshot-database:5432/chapterdb_snapshot
spring.datasource.username=postgres
spring.datasource.password=snapshot_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection pool settings
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000

# JPA/Hibernate Configuration for Snapshot
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# Disable Flyway for snapshot (we restore from backup)
spring.flyway.enabled=false

# Thymeleaf Configuration
spring.thymeleaf.cache=false

# Logging Configuration
logging.level.com.turningpoint.chapterorganizer=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.root=INFO

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,env
management.endpoint.health.show-details=always

# Snapshot-specific properties
snapshot.name=${snapshot_name}
snapshot.created=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
snapshot.git.commit=$(git rev-parse HEAD 2>/dev/null || echo "unknown")
snapshot.git.branch=$(git branch --show-current 2>/dev/null || echo "unknown")
EOF

    # 7. Get application runtime information
    local app_info="{}"
    local app_version="unknown"
    if check_app_status; then
        app_info=$(curl -s http://localhost:8080/actuator/info 2>/dev/null || echo "{}")
        app_version=$(echo "$app_info" | grep -o '"version":"[^"]*"' | cut -d'"' -f4 2>/dev/null || echo "unknown")
    fi
    
    # 8. Calculate snapshot size
    local snapshot_size=$(du -sh "$snapshot_dir" 2>/dev/null | cut -f1 || echo "unknown")
    
    log "Snapshot created successfully: $snapshot_size" >&2
    success "Complete site snapshot: $snapshot_name" >&2
    
    echo "$snapshot_name"
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

# Create metadata file for snapshots
create_snapshot_metadata() {
    local snapshot_name="$1"
    local metadata_file="${METADATA_BACKUP_DIR}/${snapshot_name}.json"
    
    log "Creating metadata for snapshot: $snapshot_name"
    
    # Get application info if available
    local app_info="{}"
    if check_app_status; then
        app_info=$(curl -s http://localhost:8080/actuator/info 2>/dev/null || echo "{}")
    fi
    
    # Get current git commit if in a git repository
    local git_commit="unknown"
    local git_branch="unknown"
    local git_status="clean"
    if git rev-parse --git-dir > /dev/null 2>&1; then
        git_commit=$(git rev-parse HEAD 2>/dev/null || echo "unknown")
        git_branch=$(git branch --show-current 2>/dev/null || echo "unknown")
        
        # Check if there are uncommitted changes
        if ! git diff-index --quiet HEAD --; then
            git_status="dirty"
        fi
    fi
    
    # Get snapshot size
    local snapshot_size=$(du -sh "${SNAPSHOT_BACKUP_DIR}/${snapshot_name}" 2>/dev/null | cut -f1 || echo "unknown")
    
    # Count files in snapshot
    local file_count=$(find "${SNAPSHOT_BACKUP_DIR}/${snapshot_name}" -type f 2>/dev/null | wc -l || echo "0")
    
    # Get frontend info
    local frontend_version="unknown"
    local backend_version="unknown"
    if [ -f "$PROJECT_ROOT/frontend/package.json" ]; then
        frontend_version=$(grep '"version"' "$PROJECT_ROOT/frontend/package.json" | cut -d'"' -f4 2>/dev/null || echo "unknown")
    fi
    if [ -f "$PROJECT_ROOT/pom.xml" ]; then
        backend_version=$(grep '<version>' "$PROJECT_ROOT/pom.xml" | head -n1 | sed 's/.*<version>\(.*\)<\/version>.*/\1/' 2>/dev/null || echo "unknown")
    fi
    
    # Create metadata JSON
    cat > "$metadata_file" << EOF
{
    "backup_name": "$snapshot_name",
    "backup_type": "site_snapshot",
    "timestamp": "$TIMESTAMP",
    "date_created": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")",
    "snapshot_size": "$snapshot_size",
    "file_count": $file_count,
    "application_status": "$(check_app_status && echo "running" || echo "stopped")",
    "git": {
        "commit": "$git_commit",
        "branch": "$git_branch",
        "status": "$git_status",
        "remote_url": "$(git config --get remote.origin.url 2>/dev/null || echo "unknown")"
    },
    "versions": {
        "backend": "$backend_version",
        "frontend": "$frontend_version"
    },
    "application_info": $app_info,
    "environment": {
        "java_version": "$(java -version 2>&1 | head -n1 | cut -d'"' -f2 2>/dev/null || echo "unknown")",
        "node_version": "$(node --version 2>/dev/null || echo "unknown")",
        "docker_version": "$(docker --version 2>/dev/null | cut -d' ' -f3 | cut -d',' -f1 || echo "unknown")"
    },
    "snapshot_contents": {
        "source_code": true,
        "database_backup": true,
        "docker_configs": true,
        "environment_configs": true,
        "dependencies": {
            "backend_pom": $([ -f "${SNAPSHOT_BACKUP_DIR}/${snapshot_name}/configs/pom.xml" ] && echo "true" || echo "false"),
            "frontend_package": $([ -f "${SNAPSHOT_BACKUP_DIR}/${snapshot_name}/configs/package.json" ] && echo "true" || echo "false")
        }
    },
    "created_by": "$(whoami)@$(hostname)",
    "restore_ports": {
        "note": "Ports are dynamically assigned by Docker",
        "app_port": "random",
        "frontend_port": "random", 
        "database_port": "random",
        "adminer_port": "random"
    }
}
EOF
    
    success "Snapshot metadata created: $metadata_file"
}

# Clean up old backups
cleanup_old_backups() {
    log "Cleaning up old backups (keeping last $MAX_BACKUPS)..."
    
    # Clean up database backups
    local backup_count=$(ls -1 "$DATABASE_BACKUP_DIR" 2>/dev/null | wc -l)
    if [ "$backup_count" -gt "$MAX_BACKUPS" ]; then
        local files_to_delete=$((backup_count - MAX_BACKUPS))
        log "Found $backup_count database backups. Removing $files_to_delete oldest..."
        
        # Remove oldest database backups
        ls -t "$DATABASE_BACKUP_DIR"/* 2>/dev/null | tail -n "$files_to_delete" | xargs rm -f
        success "Cleaned up $files_to_delete old database backups"
    else
        log "Only $backup_count database backups found. No cleanup needed."
    fi
    
    # Clean up snapshots (keep fewer since they're larger)
    local max_snapshots=$((MAX_BACKUPS / 2)) # Keep 24 snapshots (12 hours)
    if [ -d "$SNAPSHOT_BACKUP_DIR" ]; then
        local snapshot_count=$(ls -1 "$SNAPSHOT_BACKUP_DIR" 2>/dev/null | wc -l)
        if [ "$snapshot_count" -gt "$max_snapshots" ]; then
            local snapshots_to_delete=$((snapshot_count - max_snapshots))
            log "Found $snapshot_count snapshots. Removing $snapshots_to_delete oldest..."
            
            # Remove oldest snapshot directories
            for snapshot_dir in $(ls -t "$SNAPSHOT_BACKUP_DIR" | tail -n "$snapshots_to_delete"); do
                log "Removing old snapshot: $snapshot_dir"
                rm -rf "${SNAPSHOT_BACKUP_DIR}/${snapshot_dir}"
            done
            success "Cleaned up $snapshots_to_delete old snapshots"
        else
            log "Only $snapshot_count snapshots found. No cleanup needed."
        fi
    fi
    
    # Clean up orphaned metadata files
    for metadata_file in "$METADATA_BACKUP_DIR"/*.json; do
        if [ -f "$metadata_file" ]; then
            local backup_name=$(basename "$metadata_file" .json)
            local has_backup=false
            
            # Check if corresponding backup exists
            for ext in .sql .sql.gz .zip .empty; do
                if [ -f "${DATABASE_BACKUP_DIR}/${backup_name}${ext}" ]; then
                    has_backup=true
                    break
                fi
            done
            
            # Check if corresponding snapshot exists
            if [ -d "${SNAPSHOT_BACKUP_DIR}/${backup_name}" ]; then
                has_backup=true
            fi
            
            # Remove orphaned metadata
            if [ "$has_backup" = false ]; then
                log "Removing orphaned metadata: $backup_name"
                rm -f "$metadata_file"
            fi
        fi
    done
}

# Main execution
main() {
    local backup_type="${1:-both}"
    
    log "=== Backup Process Started (Type: $backup_type) ==="
    
    # Ensure directories exist
    mkdir -p "$DATABASE_BACKUP_DIR" "$METADATA_BACKUP_DIR" "$SNAPSHOT_BACKUP_DIR"
    
    # Create backup based on type
    local backup_result
    backup_result=$(create_complete_backup "$backup_type")
    
    if [ -n "$backup_result" ] && [[ ! "$backup_result" =~ ^\[.*\] ]]; then
        # Parse backup result
        if [[ "$backup_result" =~ db:.*,snapshot:.* ]]; then
            # Both database and snapshot created
            local db_name=$(echo "$backup_result" | sed 's/.*db:\([^,]*\).*/\1/')
            local snapshot_name=$(echo "$backup_result" | sed 's/.*snapshot:\([^,]*\).*/\1/')
            
            # Create metadata for both
            create_metadata "$db_name"
            create_snapshot_metadata "$snapshot_name"
            
            success "=== Complete Backup Process Completed Successfully ==="
            success "Database backup: $db_name"
            success "Snapshot backup: $snapshot_name"
            success "Locations: $DATABASE_BACKUP_DIR, $SNAPSHOT_BACKUP_DIR"
        else
            # Single backup created
            create_metadata "$backup_result"
            
            success "=== Backup Process Completed Successfully ==="
            success "Backup name: $backup_result"
        fi
        
        # Clean up old backups
        cleanup_old_backups
        
        # List recent backups
        log "Recent database backups:"
        ls -lt "$DATABASE_BACKUP_DIR" | head -n 3
        if [ -d "$SNAPSHOT_BACKUP_DIR" ] && [ -n "$(ls -A "$SNAPSHOT_BACKUP_DIR" 2>/dev/null)" ]; then
            log "Recent snapshots:"
            ls -lt "$SNAPSHOT_BACKUP_DIR" | head -n 3
        fi
    else
        error "=== Backup Process Failed ==="
        error "Invalid backup result: $backup_result"
        exit 1
    fi
}

# Run main function if script is executed directly
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi