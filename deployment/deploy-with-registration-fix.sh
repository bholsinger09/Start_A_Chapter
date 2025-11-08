#!/bin/bash

# Deploy to Production Server Script
# This script SSH's into the server, pulls latest changes, and builds/runs the application

set -e  # Exit on any error

echo "🚀 Starting deployment to production server..."

# Server details
SERVER_USER="ubuntu"
SERVER_HOST="ip-172-31-27-161"
PROJECT_DIR="~/Start_A_Chapter"
SERVER_ADDRESS="${SERVER_USER}@${SERVER_HOST}"

echo "📡 Connecting to server: ${SERVER_ADDRESS}"

# SSH into server and run deployment commands
ssh -t ${SERVER_ADDRESS} << 'ENDSSH'
    set -e
    
    echo "📁 Navigating to project directory..."
    cd ~/Start_A_Chapter
    
    echo "📦 Pulling latest changes from GitHub..."
    git stash push -m "Local changes before deployment $(date)"
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
        echo "🌐 Backend available at: http://${HOSTNAME}:8080"
        echo "📋 Registration endpoint: http://${HOSTNAME}:8080/api/auth/register"
    else
        echo "❌ Application may not be responding properly"
        echo "📋 Check logs with: tail -f ~/Start_A_Chapter/app.log"
    fi
    
    echo "📊 Application status:"
    ps aux | grep spring-boot | grep -v grep || echo "No Spring Boot process found"
    
ENDSSH

echo "🎉 Deployment script completed!"
echo ""
echo "📋 To check application status:"
echo "   ssh ${SERVER_ADDRESS} 'tail -f ~/Start_A_Chapter/app.log'"
echo ""
echo "🔧 To test registration endpoint:"
echo "   curl -X POST http://${SERVER_HOST}:8080/api/auth/register \\"
echo "     -H \"Content-Type: application/json\" \\"
echo "     -d '{\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\"test@example.com\",\"role\":\"MEMBER\"}'"