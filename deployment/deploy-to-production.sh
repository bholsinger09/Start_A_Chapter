#!/bin/bash

# Deploy to Production Server
# This script deploys the latest version to startachapter.duckdns.org

set -e  # Exit on any error

echo "🚀 Starting deployment to startachapter.duckdns.org..."

# Configuration
REMOTE_HOST="13.222.125.134"  # Your server IP
REMOTE_USER="ubuntu"  # Change this to your actual username
APP_JAR="app.jar"
REMOTE_APP_DIR="/home/$REMOTE_USER"
SERVICE_NAME="startachapter"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if JAR file exists
if [ ! -f "$APP_JAR" ]; then
    print_error "JAR file not found: $APP_JAR"
    print_warning "Run 'mvn clean package' first to build the application"
    exit 1
fi

print_status "JAR file found: $APP_JAR"

# AWS instances typically don't respond to ping for security reasons
echo "🔍 Preparing to connect to $REMOTE_HOST (startachapter.duckdns.org)..."
print_status "Proceeding with deployment to AWS instance"

# Deploy using SCP and SSH
echo "📦 Deploying application..."

# Copy JAR file to server
print_status "Copying JAR file to server..."
scp "$APP_JAR" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_APP_DIR/" || {
    print_error "Failed to copy JAR file"
    exit 1
}

# SSH commands to restart the service
print_status "Restarting application service..."
ssh "$REMOTE_USER@$REMOTE_HOST" << 'ENDSSH'
cd /home/ubuntu

# Stop the existing service
echo "Stopping existing service..."
sudo systemctl stop startachapter || echo "Service was not running"

# Wait a moment for the service to fully stop
sleep 3

# Start the service
echo "Starting service with new version..."
sudo systemctl start startachapter

# Check status
sleep 5
sudo systemctl status startachapter --no-pager

echo "Deployment complete!"
ENDSSH

if [ $? -eq 0 ]; then
    print_status "Deployment completed successfully!"
    echo ""
    print_status "🌐 Your application should now be available at:"
    echo "   https://startachapter.duckdns.org"
    echo ""
    print_warning "Verifying deployment..."
    
    # Wait a moment for the service to start
    sleep 10
    
    # Check if the service is responding
    if curl -s --head https://startachapter.duckdns.org | head -n 1 | grep -q "200 OK"; then
        print_status "✅ Service is responding correctly!"
    else
        print_warning "Service might still be starting up. Check in a few moments."
    fi
else
    print_error "Deployment failed!"
    exit 1
fi