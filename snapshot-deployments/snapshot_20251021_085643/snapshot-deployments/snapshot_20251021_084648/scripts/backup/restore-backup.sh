#!/bin/bash

# Database Restore Script for Campus Chapter Organizer
# Restores database backups and spins up Docker environment

set -e

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKUP_DIR="$PROJECT_ROOT/backups"
DATABASE_BACKUP_DIR="$BACKUP_DIR/database"
METADATA_BACKUP_DIR="$BACKUP_DIR/metadata"
RESTORE_ENV_NAME="restore"

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

# List available backups
list_backups() {
    log "=== Available Backups ==="
    
    if [ ! -d "$METADATA_BACKUP_DIR" ] || [ -z "$(ls -A "$METADATA_BACKUP_DIR" 2>/dev/null)" ]; then
        warning "No backups found in $METADATA_BACKUP_DIR"
        return 1
    fi
    
    local counter=1
    for metadata_file in $(ls -t "$METADATA_BACKUP_DIR"/*.json 2>/dev/null); do
        local backup_name=$(basename "$metadata_file" .json)
        local date_created=$(cat "$metadata_file" | grep -o '"date_created": "[^"]*"' | cut -d'"' -f4)
        local database_type=$(cat "$metadata_file" | grep -o '"database_type": "[^"]*"' | cut -d'"' -f4)
        local backup_size=$(cat "$metadata_file" | grep -o '"backup_size": "[^"]*"' | cut -d'"' -f4)
        local app_status=$(cat "$metadata_file" | grep -o '"application_status": "[^"]*"' | cut -d'"' -f4)
        
        printf "%2d. %-25s | %-19s | %-10s | %-6s | %s\n" \
            "$counter" \
            "$backup_name" \
            "$date_created" \
            "$database_type" \
            "$backup_size" \
            "$app_status"
        
        counter=$((counter + 1))
    done
    
    echo
}

# Select backup interactively
select_backup() {
    list_backups
    
    local backup_count=$(ls -1 "$METADATA_BACKUP_DIR"/*.json 2>/dev/null | wc -l)
    
    if [ "$backup_count" -eq 0 ]; then
        error "No backups available to restore"
        exit 1
    fi
    
    echo -n "Select backup number (1-$backup_count) or 'q' to quit: "
    read -r selection
    
    if [ "$selection" = "q" ] || [ "$selection" = "Q" ]; then
        log "Restore cancelled by user"
        exit 0
    fi
    
    if ! [[ "$selection" =~ ^[0-9]+$ ]] || [ "$selection" -lt 1 ] || [ "$selection" -gt "$backup_count" ]; then
        error "Invalid selection: $selection"
        exit 1
    fi
    
    # Get the backup name for the selected number
    local backup_file=$(ls -t "$METADATA_BACKUP_DIR"/*.json | sed -n "${selection}p")
    local backup_name=$(basename "$backup_file" .json)
    
    echo "$backup_name"
}

# Validate backup exists and get metadata
validate_backup() {
    local backup_name="$1"
    
    log "Validating backup: $backup_name"
    
    local metadata_file="${METADATA_BACKUP_DIR}/${backup_name}.json"
    if [ ! -f "$metadata_file" ]; then
        error "Metadata file not found: $metadata_file"
        return 1
    fi
    
    # Check if database backup file exists
    local database_file_found=false
    for ext in .sql .sql.gz .zip .empty; do
        if [ -f "${DATABASE_BACKUP_DIR}/${backup_name}${ext}" ]; then
            database_file_found=true
            break
        fi
    done
    
    if [ "$database_file_found" = false ]; then
        error "Database backup file not found for: $backup_name"
        return 1
    fi
    
    success "Backup validation passed: $backup_name"
    return 0
}

# Create Docker Compose file for restore environment
create_restore_compose() {
    local backup_name="$1"
    local restore_compose="$PROJECT_ROOT/docker-compose.restore.yml"
    
    log "Creating Docker Compose configuration for restore environment..."
    
    cat > "$restore_compose" << EOF
services:
  # Restore Database Service
  restore-database:
    image: postgres:15-alpine
    container_name: startachapter-restore-db
    environment:
      POSTGRES_DB: chapterdb_restore
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: restore_password
    ports:
      - "5433:5432"  # Use different port to avoid conflicts
    volumes:
      - restore_db_data:/var/lib/postgresql/data
      - ./backups/database:/backups:ro
      - ./scripts/restore:/docker-entrypoint-initdb.d:ro
    networks:
      - restore-network
    healthcheck:
      test: ["CMD-READY", "pg_isready", "-U", "postgres", "-d", "chapterdb_restore"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Restore Application Service
  restore-app:
    build:
      context: .
      dockerfile: Dockerfile.qa
    container_name: startachapter-restore-app
    ports:
      - "8082:8080"  # Use different port to avoid conflicts
    environment:
      - SPRING_PROFILES_ACTIVE=restore
      - SPRING_DATASOURCE_URL=jdbc:postgresql://restore-database:5432/chapterdb_restore
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=restore_password
      - BACKUP_NAME=${backup_name}
    depends_on:
      restore-database:
        condition: service_healthy
    volumes:
      - ./backups/database:/app/backups:ro
      - ./scripts/restore:/app/restore-scripts:ro
    networks:
      - restore-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 120s

  # Restore Frontend Service
  restore-frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile.qa
    container_name: startachapter-restore-frontend
    ports:
      - "3002:3000"  # Use different port to avoid conflicts
    environment:
      - VUE_APP_API_BASE_URL=http://localhost:8082
      - NODE_ENV=development
    depends_on:
      restore-app:
        condition: service_healthy
    networks:
      - restore-network

  # Database Admin Tool for Restore Environment
  restore-adminer:
    image: adminer:4.8.1
    container_name: startachapter-restore-adminer
    ports:
      - "8083:8080"
    environment:
      ADMINER_DEFAULT_SERVER: restore-database
    depends_on:
      - restore-database
    networks:
      - restore-network

volumes:
  restore_db_data:
    name: startachapter_restore_db_data

networks:
  restore-network:
    name: startachapter_restore_network
    driver: bridge
EOF
    
    success "Docker Compose file created: $restore_compose"
}

# Create restore application properties
create_restore_properties() {
    local backup_name="$1"
    local restore_props="$PROJECT_ROOT/src/main/resources/application-restore.properties"
    
    log "Creating restore application properties..."
    
    cat > "$restore_props" << EOF
# Restore Profile Configuration
spring.application.name=Campus Chapter Organizer (Restore: ${backup_name})

# Server Configuration
server.port=8080

# PostgreSQL Database Configuration for Restore
spring.datasource.url=jdbc:postgresql://restore-database:5432/chapterdb_restore
spring.datasource.username=postgres
spring.datasource.password=restore_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection pool settings
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000

# JPA/Hibernate Configuration for Restore
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# Disable Flyway for restore (we'll load from backup)
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

# Custom restore properties
restore.backup.name=${backup_name}
restore.backup.timestamp=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
EOF
    
    success "Restore properties created: $restore_props"
}

# Create database restoration script
create_db_restore_script() {
    local backup_name="$1"
    local restore_scripts_dir="$PROJECT_ROOT/scripts/restore"
    
    mkdir -p "$restore_scripts_dir"
    
    local init_script="$restore_scripts_dir/01-restore-backup.sh"
    
    log "Creating database restoration script..."
    
    cat > "$init_script" << 'EOF'
#!/bin/bash

set -e

echo "=== Database Restore Script ==="
echo "Backup name: $BACKUP_NAME"

# Wait for PostgreSQL to be ready
until pg_isready -h localhost -U postgres -d postgres; do
    echo "Waiting for PostgreSQL to be ready..."
    sleep 2
done

echo "PostgreSQL is ready. Starting restore process..."

# Create the database if it doesn't exist
psql -U postgres -c "CREATE DATABASE chapterdb_restore;" || echo "Database already exists"

# Determine backup file type and restore accordingly
backup_file=""
if [ -f "/backups/${BACKUP_NAME}.sql.gz" ]; then
    echo "Found compressed PostgreSQL backup"
    backup_file="/backups/${BACKUP_NAME}.sql.gz"
    gunzip -c "$backup_file" | psql -U postgres -d chapterdb_restore
elif [ -f "/backups/${BACKUP_NAME}.sql" ]; then
    echo "Found uncompressed PostgreSQL backup"
    backup_file="/backups/${BACKUP_NAME}.sql"
    psql -U postgres -d chapterdb_restore -f "$backup_file"
elif [ -f "/backups/${BACKUP_NAME}.zip" ]; then
    echo "Found H2 backup (will create empty PostgreSQL database)"
    # For H2 backups, we'll need to create the schema manually
    # This is a placeholder - in practice you'd convert H2 to PostgreSQL
    psql -U postgres -d chapterdb_restore -c "
        -- Create basic tables for H2 compatibility
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
    "
    echo "Created basic schema for H2 backup compatibility"
elif [ -f "/backups/${BACKUP_NAME}.empty" ]; then
    echo "Found empty backup marker - creating empty database"
    psql -U postgres -d chapterdb_restore -c "SELECT 1;" # Just ensure connection works
else
    echo "ERROR: No backup file found for $BACKUP_NAME"
    exit 1
fi

echo "=== Database Restore Complete ==="
EOF
    
    chmod +x "$init_script"
    success "Database restore script created: $init_script"
}

# Start the restore environment
start_restore_environment() {
    local backup_name="$1"
    
    log "=== Starting Restore Environment ==="
    log "Backup: $backup_name"
    
    # Stop any existing restore environment
    stop_restore_environment
    
    # Set environment variable for the backup name
    export BACKUP_NAME="$backup_name"
    
    # Start the restore environment
    log "Starting Docker containers..."
    cd "$PROJECT_ROOT"
    docker compose -f docker-compose.restore.yml up --build -d
    
    # Wait for services to be healthy
    log "Waiting for services to start..."
    sleep 10
    
    # Check service status
    log "Service status:"
    docker compose -f docker-compose.restore.yml ps
    
    success "=== Restore Environment Started ==="
    echo
    success "🌐 Application: http://localhost:8082"
    success "🎨 Frontend:    http://localhost:3002"
    success "🗄️  Database:    localhost:5433 (postgres/restore_password)"
    success "🔧 Adminer:     http://localhost:8083"
    echo
    log "To stop the restore environment, run: $0 stop"
}

# Stop the restore environment
stop_restore_environment() {
    log "Stopping restore environment..."
    cd "$PROJECT_ROOT"
    
    if [ -f "docker-compose.restore.yml" ]; then
        docker compose -f docker-compose.restore.yml down -v 2>/dev/null || true
        docker volume rm startachapter_restore_db_data 2>/dev/null || true
        success "Restore environment stopped"
    else
        log "No restore environment found to stop"
    fi
}

# Show restore environment status
show_status() {
    log "=== Restore Environment Status ==="
    cd "$PROJECT_ROOT"
    
    if [ -f "docker-compose.restore.yml" ]; then
        docker compose -f docker-compose.restore.yml ps
        echo
        
        # Check if services are accessible
        if curl -s http://localhost:8082/actuator/health > /dev/null 2>&1; then
            success "✅ Application is running: http://localhost:8082"
        else
            warning "❌ Application is not responding: http://localhost:8082"
        fi
        
        if curl -s http://localhost:3002 > /dev/null 2>&1; then
            success "✅ Frontend is running: http://localhost:3002"
        else
            warning "❌ Frontend is not responding: http://localhost:3002"
        fi
        
        if nc -z localhost 5433 2>/dev/null; then
            success "✅ Database is running: localhost:5433"
        else
            warning "❌ Database is not responding: localhost:5433"
        fi
    else
        log "No restore environment is currently running"
    fi
}

# Show help information
show_help() {
    echo "Database Restore Script for Campus Chapter Organizer"
    echo
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo
    echo "Commands:"
    echo "  restore [backup_name]  Restore from a specific backup (interactive if no name provided)"
    echo "  list                   List all available backups"
    echo "  stop                   Stop the restore environment"
    echo "  status                 Show restore environment status"
    echo "  help                   Show this help message"
    echo
    echo "Examples:"
    echo "  $0 restore                           # Interactive backup selection"
    echo "  $0 restore h2_backup_20250121_1430  # Restore specific backup"
    echo "  $0 list                              # List available backups"
    echo "  $0 stop                              # Stop restore environment"
    echo "  $0 status                            # Check restore status"
    echo
}

# Main execution
main() {
    local command="${1:-restore}"
    local backup_name="$2"
    
    case "$command" in
        "restore")
            if [ -z "$backup_name" ]; then
                backup_name=$(select_backup)
            fi
            
            if validate_backup "$backup_name"; then
                create_restore_compose "$backup_name"
                create_restore_properties "$backup_name"
                create_db_restore_script "$backup_name"
                start_restore_environment "$backup_name"
            else
                error "Backup validation failed: $backup_name"
                exit 1
            fi
            ;;
        "list")
            list_backups
            ;;
        "stop")
            stop_restore_environment
            ;;
        "status")
            show_status
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            error "Unknown command: $command"
            show_help
            exit 1
            ;;
    esac
}

# Run main function if script is executed directly
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    main "$@"
fi