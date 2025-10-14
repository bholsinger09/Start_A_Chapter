#!/bin/bash
# startup.sh - Handle database URL conversion for Render

echo "Starting application with environment setup..."

# Debug: Show all database-related environment variables
echo "DATABASE_URL: $DATABASE_URL"
echo "SPRING_DATASOURCE_URL (before): $SPRING_DATASOURCE_URL"

# Function to fix incomplete PostgreSQL URLs
fix_postgres_url() {
    local url="$1"
    
    # Check if URL is missing port and full hostname
    if [[ "$url" =~ jdbc:postgresql://(.+):(.+)@([^:/]+)/(.+) ]]; then
        local user="${BASH_REMATCH[1]}"
        local pass="${BASH_REMATCH[2]}" 
        local host="${BASH_REMATCH[3]}"
        local db="${BASH_REMATCH[4]}"
        
        # If host doesn't contain a dot (incomplete hostname), try to fix it
        if [[ "$host" =~ ^dpg-.*$ ]] && [[ ! "$host" =~ \. ]]; then
            # Render PostgreSQL hostnames follow pattern: dpg-xxxxx-a.oregon-postgres.render.com
            local fixed_host="${host}.oregon-postgres.render.com:5432"
            echo "jdbc:postgresql://${user}:${pass}@${fixed_host}/${db}"
        else
            echo "$url"
        fi
    else
        echo "$url"
    fi
}

# Handle DATABASE_URL from Render
if [[ -n "$DATABASE_URL" && "$DATABASE_URL" =~ ^postgresql:// ]]; then
    export SPRING_DATASOURCE_URL="jdbc:$DATABASE_URL"
    export SPRING_DATASOURCE_URL=$(fix_postgres_url "$SPRING_DATASOURCE_URL")
    echo "Using DATABASE_URL and converted to JDBC format"
elif [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^postgresql:// ]]; then
    export SPRING_DATASOURCE_URL="jdbc:$SPRING_DATASOURCE_URL"
    export SPRING_DATASOURCE_URL=$(fix_postgres_url "$SPRING_DATASOURCE_URL")
    echo "Converted SPRING_DATASOURCE_URL to JDBC format"
elif [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^jdbc:postgresql:// ]]; then
    export SPRING_DATASOURCE_URL=$(fix_postgres_url "$SPRING_DATASOURCE_URL")
    echo "Fixed existing JDBC URL format"
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