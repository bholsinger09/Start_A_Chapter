#!/bin/bash

echo "Starting Chapter Organizer Backend..."
echo "Java Version:"
java -version

echo "Environment Variables:"
echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"
echo "SPRING_DATASOURCE_URL: $SPRING_DATASOURCE_URL"

echo "Starting application..."
exec java $JAVA_OPTS -jar /app/app.jar
