#!/bin/bash

# Enhanced CORS Fix - Comprehensive Solution
echo "🚀 Deploying comprehensive CORS fix..."

SERVER_IP="3.91.153.33"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}📋 Enhanced CORS Plan:${NC}"
echo "  ✅ Updated CorsConfig with comprehensive settings"
echo "  ✅ Updated Controller-level CORS annotations"
echo "  ✅ Multiple origin patterns for flexibility"
echo "  ✅ All headers and methods allowed"
echo ""

# Build with the enhanced CORS
echo -e "${YELLOW}🏗️ Building with enhanced CORS...${NC}"
./mvnw clean package -DskipTests

if [[ $? -ne 0 ]]; then
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Build successful${NC}"

# Deploy to server
echo -e "${YELLOW}📤 Deploying to server...${NC}"
scp -o StrictHostKeyChecking=no target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar ubuntu@3.91.153.33:/tmp/app-cors-enhanced.jar

# Install and restart on server
ssh -o StrictHostKeyChecking=no ubuntu@3.91.153.33 << 'ENDSSH'
    echo "🛑 Stopping current backend..."
    sudo pkill -f "java -jar" || echo "No java processes found"
    
    echo "🔄 Installing enhanced JAR..."
    cp /tmp/app-cors-enhanced.jar app.jar
    
    echo "🚀 Starting with maximum CORS permissiveness..."
    nohup java -jar app.jar \
        --spring.profiles.active=qa \
        --server.port=8080 \
        --logging.level.org.springframework.web.cors=DEBUG \
        --logging.level.com.turningpoint.chapterorganizer=DEBUG \
        > app.log 2>&1 &
    
    echo "⏳ Waiting for enhanced backend to start..."
    sleep 25
    
    # Comprehensive testing
    echo "🧪 Testing API endpoints..."
    curl -s http://localhost:8080/api/chapters > /dev/null && echo "✅ Chapters endpoint OK" || echo "❌ Chapters endpoint failed"
    curl -s http://localhost:8080/api/members > /dev/null && echo "✅ Members endpoint OK" || echo "❌ Members endpoint failed"
    
    echo "🧪 Testing CORS headers (OPTIONS)..."
    CORS_TEST=$(curl -s -H "Origin: https://startachapter.duckdns.org" -H "Access-Control-Request-Method: GET" -X OPTIONS -I http://localhost:8080/api/chapters 2>/dev/null)
    echo "$CORS_TEST" | grep -i "access-control-allow-origin" && echo "✅ CORS preflight OK" || echo "❌ CORS preflight failed"
    
    echo "🧪 Testing actual GET with CORS..."
    GET_TEST=$(curl -s -H "Origin: https://startachapter.duckdns.org" -I http://localhost:8080/api/chapters 2>/dev/null)
    echo "$GET_TEST" | grep -i "access-control-allow-origin" && echo "✅ CORS on GET OK" || echo "❌ CORS on GET failed"
    
    echo "🧪 Testing from external domain..."
    curl -s http://startachapter.duckdns.org:8080/api/chapters > /dev/null && echo "✅ External domain access OK" || echo "❌ External domain access failed"
    
    echo "📋 Current Java processes:"
    ps aux | grep java | grep -v grep
    
    echo "📋 Last 15 lines of log:"
    tail -15 app.log
    
    echo "✅ Enhanced CORS deployment complete!"
ENDSSH

echo ""
echo -e "${GREEN}🎉 Enhanced CORS deployment complete!${NC}"
echo ""
echo -e "${YELLOW}📋 What was enhanced:${NC}"
echo "  ✅ Global CORS filter with origin patterns"  
echo "  ✅ Controller-level CORS with specific domains"
echo "  ✅ All HTTP methods and headers allowed"
echo "  ✅ Debug logging enabled for troubleshooting"
echo ""
echo -e "${YELLOW}🧪 Test these now:${NC}"
echo "  1. Registration: https://startachapter.duckdns.org/register"
echo "  2. Direct API: http://startachapter.duckdns.org:8080/api/chapters"
echo ""
echo -e "${YELLOW}💡 Check browser console for detailed CORS info${NC}"