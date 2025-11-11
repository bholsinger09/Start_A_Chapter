#!/bin/bash

# CORS Fix Deployment Script
echo "🚀 Deploying CORS fixes to production server..."

# Server configuration
SERVER_IP="3.91.153.33"
SERVER_USER="ubuntu"  # Adjust if different
SSH_KEY="~/.ssh/id_rsa"  # Adjust path if different
PROJECT_PATH="/opt/start_a_chapter"  # Adjust if different

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}📋 Deployment Plan:${NC}"
echo "  ✅ Deploy CorsConfig.java"
echo "  ✅ Deploy ChapterController.java"
echo "  ✅ Deploy application-production.properties"
echo "  ✅ Deploy frontend api.js"
echo "  ✅ Rebuild and restart application"
echo ""

# Function to check if file exists
check_file() {
    if [[ ! -f "$1" ]]; then
        echo -e "${RED}❌ Error: File $1 not found${NC}"
        exit 1
    fi
}

# Check all files exist before deploying
echo -e "${YELLOW}🔍 Checking files...${NC}"
check_file "src/main/java/com/turningpoint/chapterorganizer/config/CorsConfig.java"
check_file "src/main/java/com/turningpoint/chapterorganizer/controller/ChapterController.java" 
check_file "src/main/resources/application-production.properties"
check_file "frontend/src/services/api.js"

echo -e "${GREEN}✅ All files found${NC}"
echo ""

# Test SSH connection
echo -e "${YELLOW}🔧 Testing SSH connection...${NC}"
if ! ssh -o ConnectTimeout=10 -o StrictHostKeyChecking=no -i $SSH_KEY $SERVER_USER@$SERVER_IP "echo 'SSH connection successful'" 2>/dev/null; then
    echo -e "${RED}❌ Cannot connect to server. Please check:${NC}"
    echo "  - Server IP: $SERVER_IP"
    echo "  - SSH key: $SSH_KEY" 
    echo "  - Username: $SERVER_USER"
    exit 1
fi

echo -e "${GREEN}✅ SSH connection successful${NC}"
echo ""

# Copy files to server
echo -e "${YELLOW}📤 Copying files to server...${NC}"

# Copy Java files
echo "  → Copying CorsConfig.java..."
scp -o StrictHostKeyChecking=no -i $SSH_KEY \
    src/main/java/com/turningpoint/chapterorganizer/config/CorsConfig.java \
    $SERVER_USER@$SERVER_IP:/tmp/

echo "  → Copying ChapterController.java..."
scp -o StrictHostKeyChecking=no -i $SSH_KEY \
    src/main/java/com/turningpoint/chapterorganizer/controller/ChapterController.java \
    $SERVER_USER@$SERVER_IP:/tmp/

# Copy properties file
echo "  → Copying application-production.properties..."
scp -o StrictHostKeyChecking=no -i $SSH_KEY \
    src/main/resources/application-production.properties \
    $SERVER_USER@$SERVER_IP:/tmp/

# Copy frontend api.js
echo "  → Copying api.js..."
scp -o StrictHostKeyChecking=no -i $SSH_KEY \
    frontend/src/services/api.js \
    $SERVER_USER@$SERVER_IP:/tmp/

echo -e "${GREEN}✅ Files copied successfully${NC}"
echo ""

# Deploy on server
echo -e "${YELLOW}🔧 Deploying on server...${NC}"
ssh -o StrictHostKeyChecking=no -i $SSH_KEY $SERVER_USER@$SERVER_IP << 'ENDSSH'
    echo "📁 Creating directories..."
    sudo mkdir -p /opt/start_a_chapter/src/main/java/com/turningpoint/chapterorganizer/config/
    sudo mkdir -p /opt/start_a_chapter/src/main/java/com/turningpoint/chapterorganizer/controller/
    sudo mkdir -p /opt/start_a_chapter/src/main/resources/
    sudo mkdir -p /opt/start_a_chapter/frontend/src/services/
    
    echo "📋 Moving files to project directory..."
    sudo cp /tmp/CorsConfig.java /opt/start_a_chapter/src/main/java/com/turningpoint/chapterorganizer/config/
    sudo cp /tmp/ChapterController.java /opt/start_a_chapter/src/main/java/com/turningpoint/chapterorganizer/controller/
    sudo cp /tmp/application-production.properties /opt/start_a_chapter/src/main/resources/
    sudo cp /tmp/api.js /opt/start_a_chapter/frontend/src/services/
    
    echo "🏗️ Rebuilding application..."
    cd /opt/start_a_chapter
    
    # Stop current services
    echo "  → Stopping services..."
    sudo docker-compose -f docker-compose.prod.yml down || echo "Services already stopped"
    
    # Rebuild backend
    echo "  → Rebuilding backend container..."
    sudo docker-compose -f docker-compose.prod.yml build --no-cache backend
    
    # Rebuild frontend (if needed)
    echo "  → Rebuilding frontend container..."
    sudo docker-compose -f docker-compose.prod.yml build --no-cache frontend
    
    # Start services
    echo "  → Starting services..."
    sudo docker-compose -f docker-compose.prod.yml up -d
    
    # Wait for services to start
    echo "  → Waiting for services to start..."
    sleep 10
    
    # Check if backend is responding
    echo "🔍 Checking backend health..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "✅ Backend is healthy!"
            break
        elif [ $i -eq 30 ]; then
            echo "❌ Backend health check timeout"
            exit 1
        else
            echo "  → Attempt $i/30: Waiting for backend..."
            sleep 2
        fi
    done
    
    # Test CORS endpoints
    echo "🧪 Testing CORS endpoints..."
    curl -s http://localhost:8080/api/chapters > /dev/null && echo "✅ /api/chapters endpoint working" || echo "❌ /api/chapters endpoint failed"
    curl -s http://localhost:8080/api/members > /dev/null && echo "✅ /api/members endpoint working" || echo "❌ /api/members endpoint failed"
    
    echo "🎉 CORS deployment completed!"
ENDSSH

echo ""
echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
echo ""
echo -e "${YELLOW}📋 Next steps:${NC}"
echo "  1. Test registration at: http://startachapter.duckdns.org"
echo "  2. Check API endpoints:"
echo "     - http://3.91.153.33:8080/api/chapters"
echo "     - http://3.91.153.33:8080/api/members"
echo "  3. Monitor logs if needed:"
echo "     ssh -i $SSH_KEY $SERVER_USER@$SERVER_IP 'cd /opt/start_a_chapter && sudo docker-compose logs -f'"
echo ""
echo -e "${GREEN}✅ CORS fixes deployed! Registration should now work without errors.${NC}"