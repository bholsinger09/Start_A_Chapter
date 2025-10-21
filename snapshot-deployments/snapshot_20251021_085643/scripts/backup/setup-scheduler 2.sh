#!/bin/bash

# Backup Scheduler Setup for Campus Chapter Organizer
# Sets up automatic backups every 30 minutes using cron

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKUP_SCRIPT="$PROJECT_ROOT/scripts/backup/create-backup.sh"
CRON_LOG="$PROJECT_ROOT/backups/cron-backup.log"

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

# Install cron job for automatic backups
install_cron_job() {
    log "Installing automatic backup cron job..."
    
    # Create cron entry that runs every 30 minutes
    local cron_entry="*/30 * * * * $BACKUP_SCRIPT >> $CRON_LOG 2>&1"
    
    # Check if cron job already exists
    if crontab -l 2>/dev/null | grep -q "$BACKUP_SCRIPT"; then
        warning "Cron job already exists for backup script"
        log "Current cron jobs:"
        crontab -l 2>/dev/null | grep "$BACKUP_SCRIPT" || true
        return 0
    fi
    
    # Add cron job
    (crontab -l 2>/dev/null; echo "$cron_entry") | crontab -
    
    success "Cron job installed successfully!"
    success "Backups will run every 30 minutes"
    success "Logs will be written to: $CRON_LOG"
}

# Remove cron job
uninstall_cron_job() {
    log "Removing automatic backup cron job..."
    
    if crontab -l 2>/dev/null | grep -q "$BACKUP_SCRIPT"; then
        crontab -l 2>/dev/null | grep -v "$BACKUP_SCRIPT" | crontab -
        success "Cron job removed successfully"
    else
        log "No cron job found for backup script"
    fi
}

# Show current cron status
show_cron_status() {
    log "=== Backup Scheduler Status ==="
    
    if crontab -l 2>/dev/null | grep -q "$BACKUP_SCRIPT"; then
        success "✅ Automatic backup is ENABLED"
        log "Cron schedule:"
        crontab -l 2>/dev/null | grep "$BACKUP_SCRIPT"
        echo
        
        # Show recent log entries
        if [ -f "$CRON_LOG" ]; then
            log "Recent backup log entries (last 10 lines):"
            tail -n 10 "$CRON_LOG" 2>/dev/null || echo "No log entries yet"
        else
            log "No backup log file found yet: $CRON_LOG"
        fi
    else
        warning "❌ Automatic backup is DISABLED"
        log "Run '$0 install' to enable automatic backups"
    fi
    
    echo
    log "Backup script location: $BACKUP_SCRIPT"
    log "Log file location: $CRON_LOG"
}

# Test the backup script manually
test_backup() {
    log "=== Testing Backup Script Manually ==="
    
    if [ ! -x "$BACKUP_SCRIPT" ]; then
        error "Backup script not found or not executable: $BACKUP_SCRIPT"
        exit 1
    fi
    
    log "Running backup script..."
    "$BACKUP_SCRIPT"
    
    success "Manual backup test completed"
}

# Show backup logs
show_logs() {
    local lines="${1:-50}"
    
    log "=== Backup Logs (last $lines lines) ==="
    
    if [ -f "$CRON_LOG" ]; then
        tail -n "$lines" "$CRON_LOG"
    else
        warning "No log file found: $CRON_LOG"
    fi
}

# Setup launchd service (macOS alternative to cron)
install_launchd_service() {
    local service_name="com.turningpoint.chapterorganizer.backup"
    local plist_file="$HOME/Library/LaunchAgents/$service_name.plist"
    
    log "Installing macOS LaunchAgent service..."
    
    # Create LaunchAgent plist
    cat > "$plist_file" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>$service_name</string>
    <key>ProgramArguments</key>
    <array>
        <string>$BACKUP_SCRIPT</string>
    </array>
    <key>StartInterval</key>
    <integer>1800</integer>
    <key>StandardOutPath</key>
    <string>$CRON_LOG</string>
    <key>StandardErrorPath</key>
    <string>$CRON_LOG</string>
    <key>RunAtLoad</key>
    <true/>
    <key>KeepAlive</key>
    <false/>
</dict>
</plist>
EOF
    
    # Load the service
    launchctl load "$plist_file" 2>/dev/null || true
    
    success "LaunchAgent service installed: $service_name"
    success "Service file: $plist_file"
    success "Backups will run every 30 minutes (1800 seconds)"
}

# Remove launchd service
uninstall_launchd_service() {
    local service_name="com.turningpoint.chapterorganizer.backup"
    local plist_file="$HOME/Library/LaunchAgents/$service_name.plist"
    
    log "Removing macOS LaunchAgent service..."
    
    # Unload the service
    launchctl unload "$plist_file" 2>/dev/null || true
    
    # Remove the plist file
    rm -f "$plist_file"
    
    success "LaunchAgent service removed"
}

# Auto-detect platform and install appropriate scheduler
auto_install() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        log "Detected macOS - installing LaunchAgent service"
        install_launchd_service
    else
        log "Detected Unix/Linux - installing cron job"
        install_cron_job
    fi
}

# Auto-detect platform and uninstall scheduler
auto_uninstall() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        log "Detected macOS - removing LaunchAgent service"
        uninstall_launchd_service
    else
        log "Detected Unix/Linux - removing cron job"
        uninstall_cron_job
    fi
}

# Show help
show_help() {
    echo "Backup Scheduler Setup for Campus Chapter Organizer"
    echo
    echo "Usage: $0 [COMMAND]"
    echo
    echo "Commands:"
    echo "  install     Install automatic backup scheduler (every 30 minutes)"
    echo "  uninstall   Remove automatic backup scheduler"
    echo "  status      Show scheduler status and recent logs"
    echo "  test        Run backup script manually for testing"
    echo "  logs [N]    Show last N lines of backup logs (default: 50)"
    echo "  cron        Install cron job (Unix/Linux)"
    echo "  launchd     Install LaunchAgent service (macOS)"
    echo "  help        Show this help message"
    echo
    echo "Examples:"
    echo "  $0 install                # Auto-install scheduler for current platform"
    echo "  $0 status                 # Check if scheduler is running"
    echo "  $0 logs 100               # Show last 100 log lines"
    echo "  $0 test                   # Test backup script manually"
    echo
}

# Main execution
main() {
    local command="${1:-status}"
    
    # Ensure directories exist
    mkdir -p "$(dirname "$CRON_LOG")"
    
    case "$command" in
        "install")
            auto_install
            ;;
        "uninstall")
            auto_uninstall
            ;;
        "status")
            show_cron_status
            ;;
        "test")
            test_backup
            ;;
        "logs")
            show_logs "$2"
            ;;
        "cron")
            install_cron_job
            ;;
        "launchd")
            install_launchd_service
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