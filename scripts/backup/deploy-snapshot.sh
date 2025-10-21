#!/bin/bash

# Snapshot Deployment Script for Campus Chapter Organizer
# Deploys complete site snapshots with source code, database, and configurations

set -e

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKUP_DIR="$PROJECT_ROOT/backups"
SNAPSHOT_BACKUP_DIR="$BACKUP_DIR/snapshots"
METADATA_BACKUP_DIR="$BACKUP_DIR/metadata"
DEPLOY_DIR="$PROJECT_ROOT/snapshot-deployments"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# List available snapshots
list_snapshots() {
    log "=== Available Site Snapshots ==="
    
    if [ ! -d "$SNAPSHOT_BACKUP_DIR" ] || [ -z "$(ls -A "$SNAPSHOT_BACKUP_DIR" 2>/dev/null)" ]; then
        warning "No snapshots found in $SNAPSHOT_BACKUP_DIR"
        return 1
    fi
    
    local counter=1
    for snapshot_dir in $(ls -t "$SNAPSHOT_BACKUP_DIR" 2>/dev/null); do
        if [ -d "$SNAPSHOT_BACKUP_DIR/$snapshot_dir" ]; then
            local metadata_file="${METADATA_BACKUP_DIR}/${snapshot_dir}.json"
            
            if [ -f "$metadata_file" ]; then
                local date_created=$(cat "$metadata_file" | grep -o '"date_created": "[^"]*"' | cut -d'"' -f4 2>/dev/null || echo "unknown")
                local git_commit=$(cat "$metadata_file" | grep -o '"commit": "[^"]*"' | cut -d'"' -f4 | head -c 8 2>/dev/null || echo "unknown")
                local git_branch=$(cat "$metadata_file" | grep -o '"branch": "[^"]*"' | cut -d'"' -f4 2>/dev/null || echo "unknown")
                local snapshot_size=$(cat "$metadata_file" | grep -o '"snapshot_size": "[^"]*"' | cut -d'"' -f4 2>/dev/null || echo "unknown")
                local backend_version=$(cat "$metadata_file" | grep -o '"backend": "[^"]*"' | cut -d'"' -f4 2>/dev/null || echo "unknown")
                local git_status=$(cat "$metadata_file" | grep -o '"status": "[^"]*"' | cut -d'"' -f4 2>/dev/null || echo "clean")
                
                # Color code git status
                local status_color=""
                if [ "$git_status" = "dirty" ]; then
                    status_color="${RED}●${NC}"
                else
                    status_color="${GREEN}●${NC}"
                fi
                
                printf "%2d. %-28s | %-19s | %-8s | %-10s | %-8s | %s %s\n" \
                    "$counter" \
                    "$snapshot_dir" \
                    "$date_created" \
                    "$git_commit" \
                    "$git_branch" \
                    "$snapshot_size" \
                    "$status_color" \
                    "$backend_version"
            else
                printf "%2d. %-28s | %s\n" "$counter" "$snapshot_dir" "No metadata"
            fi
            
            counter=$((counter + 1))
        fi
    done
    
    echo
    echo -e "${GREEN}●${NC} = Clean git state, ${RED}●${NC} = Uncommitted changes"
    echo
}

# Select snapshot interactively
select_snapshot() {
    list_snapshots
    
    local snapshot_count=$(find "$SNAPSHOT_BACKUP_DIR" -maxdepth 1 -type d | wc -l)
    snapshot_count=$((snapshot_count - 1)) # Subtract 1 for the directory itself
    
    if [ "$snapshot_count" -eq 0 ]; then
        error "No snapshots available to deploy"
        exit 1
    fi
    
    echo -n "Select snapshot number (1-$snapshot_count) or 'q' to quit: "
    read -r selection
    
    if [ "$selection" = "q" ] || [ "$selection" = "Q" ]; then
        log "Deployment cancelled by user"
        exit 0
    fi
    
    if ! [[ "$selection" =~ ^[0-9]+$ ]] || [ "$selection" -lt 1 ] || [ "$selection" -gt "$snapshot_count" ]; then
        error "Invalid selection: $selection"
        exit 1
    fi
    
    # Get the snapshot name for the selected number
    local snapshot_name=$(ls -t "$SNAPSHOT_BACKUP_DIR" | sed -n "${selection}p")
    
    echo "$snapshot_name"
}

# Validate snapshot exists and is complete
validate_snapshot() {
    local snapshot_name="$1"
    local snapshot_dir="${SNAPSHOT_BACKUP_DIR}/${snapshot_name}"
    
    log "Validating snapshot: $snapshot_name"
    
    if [ ! -d "$snapshot_dir" ]; then
        error "Snapshot directory not found: $snapshot_dir"
        return 1
    fi
    
    # Check required files exist
    local required_files=(
        "source-code.tar.gz"
        "docker-compose-snapshot.yml"
        "configs/application-snapshot.properties"
        "database-restore/01-restore-snapshot.sh"
    )
    
    for file in "${required_files[@]}"; do
        if [ ! -f "$snapshot_dir/$file" ]; then
            error "Missing required file: $file"
            return 1
        fi
    done
    
    # Check if at least one database backup exists
    local has_db_backup=false
    for ext in .sql .sql.gz .zip .empty; do
        if [ -f "$snapshot_dir/database${ext}" ]; then
            has_db_backup=true
            break
        fi
    done
    
    if [ "$has_db_backup" = false ]; then
        error "No database backup found in snapshot"
        return 1
    fi
    
    success "Snapshot validation passed: $snapshot_name"
    return 0
}

# Prepare snapshot for deployment
prepare_snapshot_deployment() {
    local snapshot_name="$1"
    local snapshot_dir="${SNAPSHOT_BACKUP_DIR}/${snapshot_name}"
    local deployment_dir="${DEPLOY_DIR}/${snapshot_name}"
    
    log "Preparing snapshot deployment: $snapshot_name" >&2
    
    # Create deployment directory
    mkdir -p "$deployment_dir"
    
    # Extract source code
    log "Extracting source code..." >&2
    cd "$deployment_dir"
    tar -xzf "$snapshot_dir/source-code.tar.gz"
    
    # Copy Docker configurations
    log "Setting up Docker configurations..." >&2
    cp "$snapshot_dir/docker-compose-snapshot.yml" "$deployment_dir/"
    
    # Copy database backup and restore scripts
    mkdir -p "$deployment_dir/database-restore"
    cp -r "$snapshot_dir/database-restore/"* "$deployment_dir/database-restore/"
    
    # Copy database backup file
    for ext in .sql .sql.gz .zip .empty; do
        if [ -f "$snapshot_dir/database${ext}" ]; then
            cp "$snapshot_dir/database${ext}" "$deployment_dir/database${ext}"
            break
        fi
    done
    
    # Copy application configurations
    log "Setting up application configurations..." >&2
    mkdir -p "$deployment_dir/source-extracted"
    
    # Move extracted source to subdirectory for Docker build context
    mv src pom.xml mvnw* .mvn* "$deployment_dir/source-extracted/" 2>/dev/null || true
    if [ -d frontend ]; then
        mv frontend "$deployment_dir/source-extracted/"
    fi
    
    # Copy any Docker files to source-extracted
    for docker_file in Dockerfile* .dockerignore; do
        if [ -f "$docker_file" ]; then
            cp "$docker_file" "$deployment_dir/source-extracted/"
        fi
    done
    
    # Copy snapshot configurations
    cp -r "$snapshot_dir/configs" "$deployment_dir/"
    
    # Update environment variables in compose file
    sed -i.bak "s/\${snapshot_name}/${snapshot_name}/g" "$deployment_dir/docker-compose-snapshot.yml"
    rm -f "$deployment_dir/docker-compose-snapshot.yml.bak"
    
    success "Snapshot prepared for deployment: $deployment_dir" >&2
    echo "$deployment_dir"
}

# Deploy snapshot using Docker
deploy_snapshot() {
    local snapshot_name="$1"
    local deployment_dir="$2"
    
    log "=== Deploying Snapshot: $snapshot_name ==="
    
    # Stop any existing deployment with the same name
    stop_snapshot_deployment "$snapshot_name"
    
    # Set environment variables
    export SNAPSHOT_NAME="$snapshot_name"
    export COMPOSE_PROJECT_NAME="snapshot-${snapshot_name}"
    
    # Change to deployment directory
    cd "$deployment_dir"
    
    # Start the snapshot deployment
    log "Starting Docker containers..."
    docker compose -f docker-compose-snapshot.yml up --build -d
    
    # Wait for services to be healthy
    log "Waiting for services to start..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        local app_port=$(docker compose -f docker-compose-snapshot.yml port snapshot-app 8080 2>/dev/null | cut -d: -f2 || echo "")
        local frontend_port=$(docker compose -f docker-compose-snapshot.yml port snapshot-frontend 3000 2>/dev/null | cut -d: -f2 || echo "")
        local db_port=$(docker compose -f docker-compose-snapshot.yml port snapshot-database 5432 2>/dev/null | cut -d: -f2 || echo "")
        local adminer_port=$(docker compose -f docker-compose-snapshot.yml port snapshot-adminer 8080 2>/dev/null | cut -d: -f2 || echo "")
        
        if [ -n "$app_port" ] && [ -n "$frontend_port" ] && [ -n "$db_port" ] && [ -n "$adminer_port" ]; then
            # Test if app is responding
            if curl -s "http://localhost:$app_port/actuator/health" > /dev/null 2>&1; then
                success "=== Snapshot Deployment Complete ==="
                echo
                success "📸 Snapshot: $snapshot_name"
                success "🌐 Application: http://localhost:$app_port"
                success "🎨 Frontend:    http://localhost:$frontend_port"
                success "🗄️  Database:    localhost:$db_port (postgres/snapshot_password)"
                success "🔧 Adminer:     http://localhost:$adminer_port"
                echo
                success "Container names:"
                success "  • snapshot-${snapshot_name}-app"
                success "  • snapshot-${snapshot_name}-frontend"
                success "  • snapshot-${snapshot_name}-db"
                success "  • snapshot-${snapshot_name}-adminer"
                echo
                log "To stop this deployment: $0 stop $snapshot_name"
                return 0
            fi
        fi
        
        log "Waiting for services... (attempt $attempt/$max_attempts)"
        sleep 5
        attempt=$((attempt + 1))
    done
    
    error "Services failed to start properly after $max_attempts attempts"
    log "Checking service status:"
    docker compose -f docker-compose-snapshot.yml ps
    
    log "Checking logs:"
    docker compose -f docker-compose-snapshot.yml logs --tail=20
    
    return 1
}

# Stop snapshot deployment
stop_snapshot_deployment() {
    local snapshot_name="$1"
    local deployment_dir="${DEPLOY_DIR}/${snapshot_name}"
    
    if [ -d "$deployment_dir" ] && [ -f "$deployment_dir/docker-compose-snapshot.yml" ]; then
        log "Stopping snapshot deployment: $snapshot_name"
        cd "$deployment_dir"
        
        docker compose -f docker-compose-snapshot.yml down -v 2>/dev/null || true
        
        # Remove Docker volume
        docker volume rm "snapshot_${snapshot_name}_db_data" 2>/dev/null || true
        
        success "Snapshot deployment stopped: $snapshot_name"
    else
        log "No deployment found for snapshot: $snapshot_name"
    fi
}

# Show deployment status
show_deployment_status() {
    local snapshot_name="$1"
    
    if [ -n "$snapshot_name" ]; then
        # Show specific deployment status
        local deployment_dir="${DEPLOY_DIR}/${snapshot_name}"
        if [ -d "$deployment_dir" ] && [ -f "$deployment_dir/docker-compose-snapshot.yml" ]; then
            log "=== Deployment Status: $snapshot_name ==="
            cd "$deployment_dir"
            docker compose -f docker-compose-snapshot.yml ps
        else
            warning "No deployment found for: $snapshot_name"
        fi
    else
        # Show all deployments
        log "=== All Snapshot Deployments ==="
        
        if [ ! -d "$DEPLOY_DIR" ] || [ -z "$(ls -A "$DEPLOY_DIR" 2>/dev/null)" ]; then
            log "No snapshot deployments found"
            return 0
        fi
        
        for deployment in "$DEPLOY_DIR"/*; do
            if [ -d "$deployment" ]; then
                local name=$(basename "$deployment")
                echo -e "\n${BLUE}Snapshot: $name${NC}"
                
                if [ -f "$deployment/docker-compose-snapshot.yml" ]; then
                    cd "$deployment"
                    local status=$(docker compose -f docker-compose-snapshot.yml ps --format=table 2>/dev/null || echo "Not running")
                    echo "$status"
                    
                    # Show URLs if running
                    local app_port=$(docker compose -f docker-compose-snapshot.yml port snapshot-app 8080 2>/dev/null | cut -d: -f2 || echo "")
                    local frontend_port=$(docker compose -f docker-compose-snapshot.yml port snapshot-frontend 3000 2>/dev/null | cut -d: -f2 || echo "")
                    
                    if [ -n "$app_port" ] && [ -n "$frontend_port" ]; then
                        echo "  🌐 App: http://localhost:$app_port"
                        echo "  🎨 Frontend: http://localhost:$frontend_port"
                    fi
                else
                    echo "  Status: Deployment files missing"
                fi
            fi
        done
    fi
}

# Clean up deployment files
cleanup_deployments() {
    log "=== Cleaning Up Snapshot Deployments ==="
    
    if [ ! -d "$DEPLOY_DIR" ]; then
        log "No deployment directory found"
        return 0
    fi
    
    for deployment in "$DEPLOY_DIR"/*; do
        if [ -d "$deployment" ]; then
            local name=$(basename "$deployment")
            log "Cleaning up deployment: $name"
            
            # Stop deployment if running
            stop_snapshot_deployment "$name"
            
            # Remove deployment files
            rm -rf "$deployment"
        fi
    done
    
    success "All snapshot deployments cleaned up"
}

# Show help
show_help() {
    echo "Snapshot Deployment Script for Campus Chapter Organizer"
    echo
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo
    echo "Commands:"
    echo "  deploy [snapshot_name]  Deploy a site snapshot (interactive if no name)"
    echo "  list                    List all available snapshots"
    echo "  stop [snapshot_name]    Stop a running snapshot deployment"
    echo "  status [snapshot_name]  Show deployment status (all if no name)"
    echo "  cleanup                 Stop and remove all deployments"
    echo "  help                    Show this help message"
    echo
    echo "Examples:"
    echo "  $0 deploy                           # Interactive snapshot selection"
    echo "  $0 deploy snapshot_20250121_1430    # Deploy specific snapshot"
    echo "  $0 list                             # List available snapshots"
    echo "  $0 status                           # Show all running deployments"
    echo "  $0 stop snapshot_20250121_1430      # Stop specific deployment"
    echo "  $0 cleanup                          # Clean up all deployments"
    echo
    echo "Notes:"
    echo "  • Snapshots contain complete site state (code + data + configs)"
    echo "  • Each deployment runs on random Docker-assigned ports"
    echo "  • Multiple snapshots can run simultaneously"
    echo "  • Deployments are isolated and don't affect each other"
    echo
}

# Main execution
main() {
    local command="${1:-deploy}"
    local snapshot_name="$2"
    
    case "$command" in
        "deploy")
            if [ -z "$snapshot_name" ]; then
                snapshot_name=$(select_snapshot)
            fi
            
            if validate_snapshot "$snapshot_name"; then
                local deployment_dir=$(prepare_snapshot_deployment "$snapshot_name")
                deploy_snapshot "$snapshot_name" "$deployment_dir"
            else
                error "Snapshot validation failed: $snapshot_name"
                exit 1
            fi
            ;;
        "list")
            list_snapshots
            ;;
        "stop")
            if [ -z "$snapshot_name" ]; then
                error "Please specify snapshot name to stop"
                echo "Usage: $0 stop <snapshot_name>"
                exit 1
            fi
            stop_snapshot_deployment "$snapshot_name"
            ;;
        "status")
            show_deployment_status "$snapshot_name"
            ;;
        "cleanup")
            cleanup_deployments
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