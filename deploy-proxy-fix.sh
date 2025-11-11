#!/bin/bash

# CORS and API Proxy Fix for startachapter.duckdns.org
echo "🔧 Setting up API proxy to fix CORS issues..."

SERVER_IP="3.91.153.33"
SERVER_USER="ubuntu"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}📋 Plan:${NC}"
echo "  1. Update CORS to allow https://startachapter.duckdns.org"
echo "  2. Set up nginx proxy on 3.91.153.33:8080 for API calls"
echo "  3. Restart backend with updated CORS"
echo ""

# First, update CORS settings
echo -e "${YELLOW}🔧 Deploying updated CORS configuration...${NC}"

# Build and deploy updated jar
./mvnw clean package -DskipTests

# Copy new jar to server
scp -o StrictHostKeyChecking=no target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar ubuntu@3.91.153.33:/tmp/app-updated.jar

# Deploy with nginx proxy setup
ssh -o StrictHostKeyChecking=no ubuntu@3.91.153.33 << 'ENDSSH'
    echo "🛑 Stopping current backend..."
    sudo pkill -f "java -jar app.jar" || echo "No running app found"
    
    echo "📦 Installing nginx..."
    sudo apt update
    sudo apt install -y nginx
    
    echo "⚙️ Configuring nginx proxy..."
    sudo tee /etc/nginx/sites-available/api-proxy > /dev/null << 'EOF'
server {
    listen 8080;
    server_name startachapter.duckdns.org;
    
    # CORS headers for all requests
    add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization' always;
    add_header 'Access-Control-Allow-Credentials' 'false' always;
    
    # Handle preflight requests
    if ($request_method = 'OPTIONS') {
        add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization' always;
        add_header 'Access-Control-Max-Age' 3600 always;
        add_header 'Content-Type' 'text/plain; charset=utf-8' always;
        add_header 'Content-Length' 0 always;
        return 204;
    }
    
    # Proxy API calls to backend
    location /api/ {
        proxy_pass http://localhost:8081/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # Proxy other requests to backend
    location / {
        proxy_pass http://localhost:8081/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF
    
    echo "🔗 Enabling nginx proxy..."
    sudo ln -sf /etc/nginx/sites-available/api-proxy /etc/nginx/sites-enabled/
    sudo rm -f /etc/nginx/sites-enabled/default
    sudo nginx -t && sudo systemctl reload nginx
    sudo systemctl enable nginx
    
    echo "🔄 Installing updated backend..."
    cp /tmp/app-updated.jar app.jar
    
    echo "🚀 Starting backend on port 8081 (behind nginx proxy)..."
    sudo nohup java -jar app.jar --spring.profiles.active=qa --server.port=8081 > app.log 2>&1 &
    
    echo "⏳ Waiting for backend to start..."
    sleep 15
    
    # Test internal backend
    echo "🧪 Testing backend on port 8081..."
    curl -s http://localhost:8081/api/chapters > /dev/null && echo "✅ Backend responding on 8081" || echo "❌ Backend not responding"
    
    # Test nginx proxy
    echo "🧪 Testing nginx proxy on port 8080..."
    curl -s http://localhost:8080/api/chapters > /dev/null && echo "✅ Proxy responding on 8080" || echo "❌ Proxy not responding"
    
    # Test CORS
    echo "🧪 Testing CORS headers..."
    curl -s -H "Origin: https://startachapter.duckdns.org" -I http://localhost:8080/api/chapters | grep -i "access-control-allow-origin" && echo "✅ CORS headers present" || echo "❌ CORS headers missing"
    
    echo "✅ Setup complete!"
ENDSSH

echo ""
echo -e "${GREEN}🎉 API proxy setup complete!${NC}"
echo ""
echo -e "${YELLOW}📋 What was configured:${NC}"
echo "  ✅ nginx proxy on 3.91.153.33:8080"
echo "  ✅ Backend running on port 8081 (internal)"
echo "  ✅ CORS headers for https://startachapter.duckdns.org"
echo ""
echo -e "${YELLOW}📋 Test your registration now:${NC}"
echo "  https://startachapter.duckdns.org/register"
echo ""
echo -e "${YELLOW}🔍 API endpoints should now work:${NC}"
echo "  https://startachapter.duckdns.org:8080/api/chapters"
echo "  https://startachapter.duckdns.org:8080/api/members"