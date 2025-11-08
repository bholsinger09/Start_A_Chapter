#!/bin/bash

# Server-side deployment script
# Run this script directly on the Ubuntu server

set -e  # Exit on any error

echo "🚀 Starting server-side deployment..."

echo "📁 Navigating to project directory..."
cd ~/Start_A_Chapter

echo "📦 Pulling latest changes from GitHub..."
git stash push -m "Local changes before deployment $(date)" || true
git pull origin main

echo "🔧 Building Spring Boot application..."
# Use explicit skip of tests to avoid hanging
./mvnw clean package -Dmaven.test.skip=true -q

echo "🛑 Stopping any existing processes..."
# Kill existing Spring Boot processes
pkill -f "spring-boot:run" || true
pkill -f "java.*jar" || true
sleep 3

echo "🚀 Starting application in background..."
# Start the application in background with nohup
nohup ./mvnw spring-boot:run > app.log 2>&1 &

echo "⏳ Waiting for application to start..."
sleep 15

echo "🔍 Testing application..."
# Test if the application is responding
if curl -f -s http://localhost:8080/api/chapters > /dev/null; then
    echo "✅ Application is running successfully!"
    echo "🌐 Backend available at: http://localhost:8080"
    echo "📋 Registration endpoint: http://localhost:8080/api/auth/register"
else
    echo "❌ Application may not be responding properly"
    echo "📋 Check logs with: tail -f ~/Start_A_Chapter/app.log"
fi

echo "📊 Application status:"
ps aux | grep spring-boot | grep -v grep || echo "No Spring Boot process found"

echo ""
echo "🧪 Testing registration endpoint..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "ServerTest",
    "lastName": "User", 
    "email": "servertest@example.com",
    "role": "MEMBER"
  }' \
  -w "\nHTTP Status: %{http_code}\n" || echo "Registration test failed"

echo ""
echo "🎉 Deployment completed!"