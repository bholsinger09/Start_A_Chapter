#!/bin/bash

# Simple CORS fix without nginx
echo "🔧 Simple CORS fix for startachapter.duckdns.org..."

SERVER_IP="3.91.153.33"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}📋 Simple Plan: Run backend with direct CORS for your domain${NC}"

ssh -o StrictHostKeyChecking=no ubuntu@3.91.153.33 << 'ENDSSH'
    echo "🛑 Stopping any running services..."
    sudo pkill -f "java -jar" || echo "No java processes found"
    sudo systemctl stop nginx || echo "Nginx not running"
    
    echo "📁 Fixing file permissions..."
    sudo chmod 666 app.jar 2>/dev/null || echo "File permissions OK"
    
    echo "🔄 Installing updated JAR..."
    cp /tmp/app-updated.jar app.jar
    sudo chown ubuntu:ubuntu app.jar
    
    echo "🚀 Starting backend with HTTPS CORS support..."
    # Start with explicit CORS configuration
    nohup java -jar app.jar \
        --spring.profiles.active=qa \
        --server.port=8080 \
        --cors.allowed-origins="https://startachapter.duckdns.org,http://startachapter.duckdns.org,https://startachapter.duckdns.org:8080,http://startachapter.duckdns.org:8080" \
        > app.log 2>&1 &
    
    echo "⏳ Waiting for backend to start..."
    sleep 20
    
    # Test if backend is responding
    echo "🧪 Testing backend..."
    for i in {1..10}; do
        if curl -s http://localhost:8080/api/chapters > /dev/null 2>&1; then
            echo "✅ Backend responding!"
            break
        else
            echo "  → Attempt $i/10: Waiting..."
            sleep 3
        fi
    done
    
    # Test CORS specifically
    echo "🧪 Testing CORS for your domain..."
    CORS_TEST=$(curl -s -H "Origin: https://startachapter.duckdns.org" -H "Access-Control-Request-Method: GET" -X OPTIONS -I http://localhost:8080/api/chapters 2>/dev/null | grep -i "access-control-allow-origin" || echo "")
    
    if [[ ! -z "$CORS_TEST" ]]; then
        echo "✅ CORS headers working!"
        echo "   $CORS_TEST"
    else
        echo "❌ CORS headers not found"
    fi
    
    # Show what's running
    echo "📊 Current processes:"
    ps aux | grep java | grep -v grep || echo "No Java processes found"
    
    echo "📋 Backend log (last 10 lines):"
    tail -10 app.log || echo "No log file found"
    
    echo "✅ Deployment complete!"
ENDSSH

echo ""
echo -e "${GREEN}🎉 Simple deployment complete!${NC}"
echo ""
echo -e "${YELLOW}📋 Test these URLs:${NC}"
echo "  Backend API: http://3.91.153.33:8080/api/chapters"
echo "  Your registration: https://startachapter.duckdns.org/register"
echo ""
echo -e "${YELLOW}💡 If still getting CORS errors:${NC}"
echo "  The frontend might need to call 3.91.153.33:8080 directly"
echo "  or startachapter.duckdns.org needs to point to 3.91.153.33"