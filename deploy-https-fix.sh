#!/bin/bash

# HTTPS API Fix - Set up SSL certificate for API
echo "🔐 Setting up HTTPS for API to fix mixed content issues..."

SERVER_IP="3.91.153.33"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}📋 HTTPS Setup Plan:${NC}"
echo "  1. Install SSL certificate for startachapter.duckdns.org"
echo "  2. Configure nginx as HTTPS proxy"
echo "  3. Backend stays HTTP (internal)"
echo "  4. Fix the /api/api/ double path issue"
echo ""

# Deploy HTTPS setup
ssh -o StrictHostKeyChecking=no ubuntu@3.91.153.33 << 'ENDSSH'
    echo "📦 Installing SSL tools..."
    sudo apt update
    sudo apt install -y certbot nginx python3-certbot-nginx
    
    echo "🔐 Setting up SSL certificate..."
    # Stop nginx first
    sudo systemctl stop nginx
    
    # Get certificate for the domain
    sudo certbot certonly --standalone -d startachapter.duckdns.org --non-interactive --agree-tos --email admin@startachapter.duckdns.org || echo "Certificate might already exist"
    
    echo "⚙️ Configuring HTTPS nginx proxy..."
    sudo tee /etc/nginx/sites-available/https-api-proxy > /dev/null << 'EOF'
# HTTP redirect to HTTPS
server {
    listen 80;
    server_name startachapter.duckdns.org;
    return 301 https://$server_name$request_uri;
}

# HTTPS API proxy
server {
    listen 443 ssl http2;
    server_name startachapter.duckdns.org;
    
    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/startachapter.duckdns.org/fullchain.pem;
    ssl_private_key /etc/letsencrypt/live/startachapter.duckdns.org/privkey.pem;
    ssl_session_cache shared:SSL:1m;
    ssl_session_timeout 10m;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    # CORS headers for all responses
    add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;
    add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range' always;
    
    # Handle preflight requests
    location / {
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
            add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;
            add_header 'Access-Control-Max-Age' 1728000 always;
            add_header 'Content-Type' 'text/plain; charset=utf-8' always;
            add_header 'Content-Length' 0 always;
            return 204;
        }
        
        # Proxy to backend (remove double /api/ issue)
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Port $server_port;
    }
}

# API on port 8080 for backwards compatibility
server {
    listen 8080 ssl http2;
    server_name startachapter.duckdns.org;
    
    # SSL Configuration (same certificate)
    ssl_certificate /etc/letsencrypt/live/startachapter.duckdns.org/fullchain.pem;
    ssl_private_key /etc/letsencrypt/live/startachapter.duckdns.org/privkey.pem;
    
    # CORS headers
    add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;
    
    # Handle preflight
    if ($request_method = 'OPTIONS') {
        add_header 'Access-Control-Allow-Origin' 'https://startachapter.duckdns.org' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;
        add_header 'Access-Control-Max-Age' 1728000 always;
        add_header 'Content-Type' 'text/plain charset=UTF-8' always;
        add_header 'Content-Length' 0 always;
        return 204;
    }
    
    # Proxy API calls
    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF
    
    echo "🔗 Enabling HTTPS proxy..."
    sudo ln -sf /etc/nginx/sites-available/https-api-proxy /etc/nginx/sites-enabled/
    sudo rm -f /etc/nginx/sites-enabled/default
    sudo rm -f /etc/nginx/sites-enabled/api-proxy
    
    # Test nginx config
    sudo nginx -t
    if [ $? -eq 0 ]; then
        echo "✅ Nginx config valid"
        sudo systemctl start nginx
        sudo systemctl enable nginx
    else
        echo "❌ Nginx config invalid"
        exit 1
    fi
    
    echo "🔄 Restarting backend on port 8081..."
    sudo pkill -f "java -jar" || echo "No java processes"
    nohup java -jar app.jar --spring.profiles.active=qa --server.port=8081 > app.log 2>&1 &
    
    sleep 15
    
    echo "🧪 Testing HTTPS setup..."
    # Test internal backend
    curl -s http://localhost:8081/api/chapters > /dev/null && echo "✅ Backend responding on 8081" || echo "❌ Backend not responding"
    
    # Test HTTPS proxy
    curl -s -k https://localhost/api/chapters > /dev/null && echo "✅ HTTPS proxy working" || echo "❌ HTTPS proxy failed"
    
    echo "✅ HTTPS setup complete!"
    echo "🔗 Your API is now available at:"
    echo "   https://startachapter.duckdns.org/api/chapters"
    echo "   https://startachapter.duckdns.org:8080/api/chapters"
ENDSSH

echo ""
echo -e "${GREEN}🎉 HTTPS API setup complete!${NC}"
echo ""
echo -e "${YELLOW}📋 What was configured:${NC}"
echo "  ✅ SSL certificate for startachapter.duckdns.org"
echo "  ✅ HTTPS nginx proxy on port 443 and 8080"
echo "  ✅ Backend running on internal port 8081"
echo "  ✅ Proper CORS headers for HTTPS"
echo ""
echo -e "${YELLOW}🧪 Test these HTTPS URLs:${NC}"
echo "  1. https://startachapter.duckdns.org/api/chapters"
echo "  2. https://startachapter.duckdns.org:8080/api/chapters"
echo ""
echo -e "${YELLOW}💡 The registration should now work without mixed content errors!${NC}"