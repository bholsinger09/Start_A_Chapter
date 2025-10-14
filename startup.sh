#!/bin/bash
# startup.sh - Handle database URL conversion for Render

echo "Starting application with environment setup..."

# Debug: Show all database-related environment variables
echo "DATABASE_URL: $DATABASE_URL"
echo "SPRING_DATASOURCE_URL (before): $SPRING_DATASOURCE_URL"

# Render provides DATABASE_URL - use it and convert to JDBC format
if [[ -n "$DATABASE_URL" && "$DATABASE_URL" =~ ^postgresql:// ]]; then
    export SPRING_DATASOURCE_URL="jdbc:$DATABASE_URL"
    echo "Using DATABASE_URL and converted to JDBC format"
elif [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^postgresql:// ]]; then
    export SPRING_DATASOURCE_URL="jdbc:$SPRING_DATASOURCE_URL"
    echo "Converted SPRING_DATASOURCE_URL to JDBC format"
fi

echo "Final Database URL: $SPRING_DATASOURCE_URL"

# Validate URL format
if [[ "$SPRING_DATASOURCE_URL" =~ jdbc:postgresql://.*@.*\..*/.* ]]; then
    echo "✓ URL format appears correct"
else
    echo "⚠ URL format may be incorrect - expected: jdbc:postgresql://user:pass@host:port/db"
fi

echo "Starting Java application..."

# Start the Java application
exec java -Dserver.port=${PORT:-8080} -jar target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar