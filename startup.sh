#!/bin/bash
# startup.sh - Handle database URL conversion for Render

echo "Starting application with environment setup..."

# Debug: Show all database-related environment variables
echo "DATABASE_URL: $DATABASE_URL"
echo "SPRING_DATASOURCE_URL (before): $SPRING_DATASOURCE_URL"

# Function to parse PostgreSQL URL and extract components
parse_postgres_url() {
    local url="$1"
    
    # Remove postgresql:// or jdbc:postgresql:// prefix
    url="${url#postgresql://}"
    url="${url#jdbc:postgresql://}"
    
    # Parse: user:pass@host:port/db or user:pass@host/db
    if [[ "$url" =~ ^([^:]+):([^@]+)@([^:/]+)(:([0-9]+))?/(.+)$ ]]; then
        local user="${BASH_REMATCH[1]}"
        local pass="${BASH_REMATCH[2]}" 
        local host="${BASH_REMATCH[3]}"
        local port="${BASH_REMATCH[5]:-5432}"  # Default to 5432 if no port
        local db="${BASH_REMATCH[6]}"
        
        # If host doesn't contain a dot (incomplete hostname), fix it
        if [[ "$host" =~ ^dpg-.*$ ]] && [[ ! "$host" =~ \. ]]; then
            host="${host}.oregon-postgres.render.com"
        fi
        
        # Set individual environment variables for Spring Boot
        export SPRING_DATASOURCE_USERNAME="$user"
        export SPRING_DATASOURCE_PASSWORD="$pass"
        export DB_HOST="$host"
        export DB_PORT="$port"
        export DB_NAME="$db"
        
        # Construct clean JDBC URL
        local jdbc_url="jdbc:postgresql://${host}:${port}/${db}"
        echo "$jdbc_url"
    else
        echo "Failed to parse URL: $url"
        return 1
    fi
}

# Handle DATABASE_URL from Render
if [[ -n "$DATABASE_URL" && "$DATABASE_URL" =~ ^postgresql:// ]]; then
    echo "Parsing DATABASE_URL: $DATABASE_URL"
    SPRING_DATASOURCE_URL=$(parse_postgres_url "$DATABASE_URL")
elif [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^postgresql:// ]]; then
    echo "Parsing SPRING_DATASOURCE_URL: $SPRING_DATASOURCE_URL"
    SPRING_DATASOURCE_URL=$(parse_postgres_url "$SPRING_DATASOURCE_URL")
elif [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^jdbc:postgresql:// ]]; then
    echo "Parsing existing JDBC URL: $SPRING_DATASOURCE_URL"
    SPRING_DATASOURCE_URL=$(parse_postgres_url "$SPRING_DATASOURCE_URL")
fi

export SPRING_DATASOURCE_URL

# Set production database settings to preserve data
# CRITICAL: These settings prevent database from being wiped on deployment
export SPRING_JPA_HIBERNATE_DDL_AUTO="${SPRING_JPA_HIBERNATE_DDL_AUTO:-validate}"
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-production}"

echo "==================== DATABASE PROTECTION CONFIGURATION ===================="
echo "CRITICAL SETTINGS FOR DATA PERSISTENCE:"
echo "  DDL Auto Setting: $SPRING_JPA_HIBERNATE_DDL_AUTO"
echo "  Spring Profile: $SPRING_PROFILES_ACTIVE"
echo ""
echo "Database Connection Details:"
echo "  Database URL: $SPRING_DATASOURCE_URL"
echo "  Username: $SPRING_DATASOURCE_USERNAME"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  Database: $DB_NAME"
echo ""
if [ "$SPRING_JPA_HIBERNATE_DDL_AUTO" = "validate" ]; then
    echo "✅ DATA PROTECTION: ON - Database will be preserved"
else
    echo "⚠️  WARNING: DDL_AUTO='$SPRING_JPA_HIBERNATE_DDL_AUTO' may wipe data!"
fi
echo "========================================================================"

# Validate URL format
if [[ "$SPRING_DATASOURCE_URL" =~ jdbc:postgresql://.*:.*/.* ]]; then
    echo "✓ URL format appears correct"
else
    echo "⚠ URL format may be incorrect"
fi

echo "Starting Java application..."

# Start the Java application
exec java -Dserver.port=${PORT:-8080} -jar target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar