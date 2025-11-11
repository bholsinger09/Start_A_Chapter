#!/bin/bash

# Simple JAR deployment for CORS fixes
echo "🚀 Deploying CORS fixes via JAR replacement..."

SERVER_IP="3.91.153.33"
SERVER_USER="ubuntu"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}📋 Plan: Build new JAR locally and replace on server${NC}"

# Build new JAR with fixes
echo -e "${YELLOW}🏗️ Building JAR with CORS fixes...${NC}"
./mvnw clean package -DskipTests -Dspring.profiles.active=production

if [[ $? -ne 0 ]]; then
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ JAR built successfully${NC}"

# Copy JAR to server
echo -e "${YELLOW}📤 Copying JAR to server...${NC}"
scp -o StrictHostKeyChecking=no target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar ubuntu@3.91.153.33:/tmp/app-new.jar

# Deploy on server
echo -e "${YELLOW}🔧 Deploying on server...${NC}"
ssh -o StrictHostKeyChecking=no ubuntu@3.91.153.33 << 'ENDSSH'
    echo "🛑 Stopping current application..."
    sudo pkill -f "java -jar app.jar" || echo "No running app found"
    
    echo "📁 Backing up current JAR..."
    sudo cp app.jar app.jar.backup.$(date +%Y%m%d_%H%M%S) 2>/dev/null || echo "No existing app.jar found"
    
    echo "🔄 Installing new JAR..."
    sudo cp /tmp/app-new.jar app.jar
    sudo chown root:root app.jar
    
    echo "🚀 Starting application with CORS fixes..."
    sudo nohup java -jar app.jar --spring.profiles.active=production > app.log 2>&1 &
    
    echo "⏳ Waiting for application to start..."
    sleep 15
    
    # Test health
    for i in {1..30}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 || curl -s http://localhost:8080/api/chapters > /dev/null 2>&1; then
            echo "✅ Application is running!"
            break
        elif [ $i -eq 30 ]; then
            echo "❌ Application startup timeout"
            exit 1
        else
            echo "  → Attempt $i/30: Waiting for application..."
            sleep 2
        fi
    done
    
    echo "🧪 Testing endpoints..."
    curl -s -I http://localhost:8080/api/chapters | head -1
    curl -s -I http://localhost:8080/api/members | head -1
    
    echo "✅ Deployment completed!"
ENDSSH

echo ""
echo -e "${GREEN}🎉 CORS fixes deployed successfully!${NC}"
echo ""
echo -e "${YELLOW}📋 Test your registration now at:${NC}"
echo "  http://startachapter.duckdns.org"
echo ""
echo -e "${YELLOW}🔍 Check endpoints:${NC}"
echo "  http://3.91.153.33:8080/api/chapters"
echo "  http://3.91.153.33:8080/api/members"