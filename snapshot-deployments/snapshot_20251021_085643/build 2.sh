#!/bin/bash

# Build script for Render deployment

echo "Starting backend build..."

# Clean and package the Spring Boot application
./mvnw clean package -DskipTests

echo "Backend build completed."

# Check if frontend directory exists
if [ -d "frontend" ]; then
    echo "Starting frontend build..."
    cd frontend
    
    # Install dependencies
    npm ci
    
    # Build the Vue.js application
    npm run build:prod
    
    echo "Frontend build completed."
else
    echo "Frontend directory not found, skipping frontend build."
fi

echo "Build process completed successfully!"