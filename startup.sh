#!/bin/bash
# startup.sh - Handle database URL conversion for Render

echo "Starting application with environment setup..."

# Debug: Show all database-related environment variables
echo "DATABASE_URL: $DATABASE_URL"
echo "SPRING_DATASOURCE_URL (before): $SPRING_DATASOURCE_URL"

# Function to fix incomplete PostgreSQL URLs
fix_postgres_url() {
    local url="$1"
    
    # Parse JDBC PostgreSQL URL: jdbc:postgresql://user:pass@host:port/db or jdbc:postgresql://user:pass@host/db
    if [[ "$url" =~ ^jdbc:postgresql://([^:]+):([^@]+)@([^:/]+)(:([0-9]+))?/(.+)$ ]]; then
        local user="${BASH_REMATCH[1]}"
        local pass="${BASH_REMATCH[2]}" 
        local host="${BASH_REMATCH[3]}"
        local port="${BASH_REMATCH[5]:-5432}"  # Default to 5432 if no port
        local db="${BASH_REMATCH[6]}"
        
        # If host doesn't contain a dot (incomplete hostname), try to fix it
        if [[ "$host" =~ ^dpg-.*$ ]] && [[ ! "$host" =~ \. ]]; then
            # Render PostgreSQL hostnames follow pattern: dpg-xxxxx-a.oregon-postgres.render.com
            local fixed_host="${host}.oregon-postgres.render.com"
            echo "jdbc:postgresql://${user}:${pass}@${fixed_host}:${port}/${db}"
        else
            echo "jdbc:postgresql://${user}:${pass}@${host}:${port}/${db}"
        fi
    else
        echo "$url"
    fi
}

# Handle DATABASE_URL from Render
if [[ -n "$DATABASE_URL" && "$DATABASE_URL" =~ ^postgresql:// ]]; then
    echo "Converting DATABASE_URL: $DATABASE_URL"
    export SPRING_DATASOURCE_URL="jdbc:$DATABASE_URL"
    echo "After adding jdbc prefix: $SPRING_DATASOURCE_URL"
    export SPRING_DATASOURCE_URL=$(fix_postgres_url "$SPRING_DATASOURCE_URL")
    echo "After fixing URL: $SPRING_DATASOURCE_URL"
elif [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^postgresql:// ]]; then
    echo "Converting SPRING_DATASOURCE_URL: $SPRING_DATASOURCE_URL"
    export SPRING_DATASOURCE_URL="jdbc:$SPRING_DATASOURCE_URL"
    echo "After adding jdbc prefix: $SPRING_DATASOURCE_URL"
    export SPRING_DATASOURCE_URL=$(fix_postgres_url "$SPRING_DATASOURCE_URL")
    echo "After fixing URL: $SPRING_DATASOURCE_URL"
elif [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^jdbc:postgresql:// ]]; then
    echo "Fixing existing JDBC URL: $SPRING_DATASOURCE_URL"
    export SPRING_DATASOURCE_URL=$(fix_postgres_url "$SPRING_DATASOURCE_URL")
    echo "After fixing URL: $SPRING_DATASOURCE_URL"
fi

echo "Final Database URL: $SPRING_DATASOURCE_URL"

# Validate URL format
if [[ "$SPRING_DATASOURCE_URL" =~ jdbc:postgresql://.*@.*:.*/.* ]]; then
    echo "✓ URL format appears correct"
else
    echo "⚠ URL format may be incorrect - expected: jdbc:postgresql://user:pass@host:port/db"
fi

echo "Starting Java application..."

# Start the Java application
exec java -Dserver.port=${PORT:-8080} -jar target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar