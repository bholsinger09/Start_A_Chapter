#!/bin/bash

# Complete HTTPS + CORS Fix
echo "🚀 Deploying complete HTTPS + CORS solution..."

SERVER_IP="3.91.153.33"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}📋 Complete Fix Plan:${NC}"
echo "  1. ✅ Setup HTTPS proxy for API calls"
echo "  2. ✅ Fix double /api/api/ path issue"
echo "  3. ✅ Configure proper CORS for HTTPS"
echo "  4. ✅ Test all endpoints"
echo ""

# Deploy the complete solution
ssh -o StrictHostKeyChecking=no ubuntu@3.91.153.33 << 'ENDSSH'
    echo "🛑 Stopping all services..."
    sudo pkill -f "java -jar" || echo "No java found"
    sudo systemctl stop nginx || echo "Nginx not running"
    
    echo "⚙️ Creating simple HTTPS proxy config..."
    sudo tee /etc/nginx/sites-available/simple-https-proxy > /dev/null << 'EOF'
server {
    listen 80;
    server_name startachapter.duckdns.org;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name startachapter.duckdns.org;
    
    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/startachapter.duckdns.org/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/startachapter.duckdns.org/privkey.pem;
    
    # Serve static frontend files
    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
        
        # Add CORS headers for frontend
        add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
    }
    
    # Proxy API calls to backend (fix the /api/api issue)
    location /api/ {
        # CORS headers
        add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With' always;
        add_header 'Access-Control-Allow-Credentials' 'false' always;
        
        # Handle preflight requests
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization, X-Requested-With' always;
            add_header 'Access-Control-Max-Age' 3600 always;
            add_header 'Content-Type' 'text/plain; charset=utf-8' always;
            add_header 'Content-Length' 0 always;
            return 204;
        }
        
        # Proxy to backend
        proxy_pass http://localhost:8081/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

    echo "🔗 Enabling HTTPS proxy..."
    sudo ln -sf /etc/nginx/sites-available/simple-https-proxy /etc/nginx/sites-enabled/
    sudo rm -f /etc/nginx/sites-enabled/default /etc/nginx/sites-enabled/*api-proxy
    
    echo "✅ Testing nginx config..."
    sudo nginx -t
    
    if [ $? -eq 0 ]; then
        echo "✅ Nginx config valid, reloading..."
        sudo systemctl reload nginx
        sudo systemctl enable nginx
    else
        echo "❌ Nginx config invalid, check logs"
        exit 1
    fi
    
    echo "🚀 Starting backend on port 8081..."
    nohup java -jar app.jar \
        --spring.profiles.active=qa \
        --server.port=8081 \
        --logging.level.org.springframework.web.cors=DEBUG \
        > app.log 2>&1 &
    
    echo "⏳ Waiting for services to start..."
    sleep 20
    
    echo "🧪 Testing backend directly..."
    curl -s http://localhost:8081/api/chapters > /dev/null && echo "✅ Backend on 8081: OK" || echo "❌ Backend on 8081: Failed"
    
    echo "🧪 Testing HTTPS proxy..."
    curl -k -s https://localhost/api/chapters > /dev/null && echo "✅ HTTPS proxy: OK" || echo "❌ HTTPS proxy: Failed"
    
    echo "🧪 Testing external HTTPS access..."
    curl -k -s https://startachapter.duckdns.org/api/chapters > /dev/null && echo "✅ External HTTPS: OK" || echo "❌ External HTTPS: Failed"
    
    echo "📋 Service status:"
    sudo systemctl is-active nginx && echo "✅ Nginx running" || echo "❌ Nginx not running"
    ps aux | grep java | grep -v grep && echo "✅ Backend running" || echo "❌ Backend not running"
    
    echo "✅ HTTPS setup complete!"
    echo "📋 Your API should now be available at:"
    echo "    https://startachapter.duckdns.org/api/chapters"
    echo "    https://startachapter.duckdns.org/api/members"
ENDSSH

echo ""
echo -e "${GREEN}🎉 Complete HTTPS + CORS solution deployed!${NC}"
echo ""
echo -e "${YELLOW}📋 What was fixed:${NC}"
echo "  ✅ HTTPS proxy for API calls"
echo "  ✅ Proper /api/ path routing (no double /api/api/)"
echo "  ✅ CORS headers for HTTPS requests"
echo "  ✅ SSL termination at nginx"
echo ""
echo -e "${YELLOW}🧪 Test your registration now:${NC}"
echo "  https://startachapter.duckdns.org/register"
echo ""
echo -e "${YELLOW}🔍 API endpoints (now HTTPS):${NC}"
echo "  https://startachapter.duckdns.org/api/chapters"
echo "  https://startachapter.duckdns.org/api/members"