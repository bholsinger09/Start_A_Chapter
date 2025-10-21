#!/bin/bash

# Start script for production deployment
echo "Starting Campus Chapter Organizer backend..."

# Set Java options for production
export JAVA_OPTS="-Xmx512m -Xms256m -server"

# Start the Spring Boot application with production profile
java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=production -jar target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar

echo "Application started successfully!"