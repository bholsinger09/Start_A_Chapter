#!/bin/bash
# startup.sh - Handle database URL conversion for Render

echo "Starting application with environment setup..."

# Convert postgresql:// to jdbc:postgresql:// if needed
if [[ -n "$SPRING_DATASOURCE_URL" && "$SPRING_DATASOURCE_URL" =~ ^postgresql:// ]]; then
    export SPRING_DATASOURCE_URL="jdbc:$SPRING_DATASOURCE_URL"
    echo "Converted database URL to JDBC format"
fi

# Also handle DATABASE_URL if provided by Render
if [[ -n "$DATABASE_URL" && "$DATABASE_URL" =~ ^postgresql:// ]]; then
    export SPRING_DATASOURCE_URL="jdbc:$DATABASE_URL"
    echo "Using DATABASE_URL and converted to JDBC format"
fi

echo "Database URL: $SPRING_DATASOURCE_URL"
echo "Starting Java application..."

# Start the Java application
exec java -Dserver.port=${PORT:-8080} -jar target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar