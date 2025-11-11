#!/bin/bash
set -e

echo "🚀 FINAL SOLUTION - Complete API Path Fix"

# SSH to the server and deploy the comprehensive solution
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "🔧 Creating FINAL nginx configuration that handles ALL API path variations..."

# Create a comprehensive nginx config that handles all cases
sudo tee /etc/nginx/sites-available/default > /dev/null << 'NGINX_FINAL'
server {
    listen 80;
    listen [::]:80;
    server_name startachapter.duckdns.org;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name startachapter.duckdns.org;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/startachapter.duckdns.org/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/startachapter.duckdns.org/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;

    root /var/www/html;
    index index.html;

    # Frontend Vue.js SPA
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache, no-store, must-revalidate" always;
    }

    # SPECIFIC FIX: Handle exact problematic paths first
    location = /api/api/chapters {
        proxy_pass http://localhost:8081/api/chapters;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With" always;
    }

    location = /api/api/members {
        proxy_pass http://localhost:8081/api/members;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With" always;
    }

    # Catch any other /api/api/* patterns and fix them
    location ~* ^/api/api/(.+)$ {
        proxy_pass http://localhost:8081/api/$1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With" always;
    }

    # Normal API proxy for correct calls
    location /api/ {
        proxy_pass http://localhost:8081/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With" always;
    }

    # Static files
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1d;
        add_header Cache-Control "public, immutable";
    }
}
NGINX_FINAL

echo "🔄 Testing and applying final configuration..."
sudo nginx -t
sudo systemctl reload nginx

echo "✅ FINAL configuration deployed!"

# Comprehensive testing
echo "🔍 COMPREHENSIVE API TESTING:"

echo "1. Backend direct:"
curl -s http://localhost:8081/api/chapters

echo -e "\n2. Correct path /api/chapters:"
curl -k -s https://localhost/api/chapters

echo -e "\n3. PROBLEM path /api/api/chapters:"
curl -k -s https://localhost/api/api/chapters

echo -e "\n4. External HTTPS:"
curl -k -s https://startachapter.duckdns.org/api/chapters

echo -e "\n5. External PROBLEM path:"
curl -k -s https://startachapter.duckdns.org/api/api/chapters

echo -e "\n🎯 ALL should return [] (empty array)"

# Check access logs to verify routing
echo -e "\n📊 Recent access log (last 3 lines):"
sudo tail -3 /var/log/nginx/access.log | grep -E "(api|chapters)"

EOF

echo "🎉 FINAL SOLUTION DEPLOYED!"
echo ""
echo "✅ This configuration handles:"
echo "   - Normal API calls: /api/chapters → backend /api/chapters"  
echo "   - Frontend's wrong calls: /api/api/chapters → backend /api/chapters"
echo "   - All other endpoints: /api/api/members, etc."
echo ""
echo "🌐 Test now at: https://startachapter.duckdns.org/register"
echo "🔥 The registration page should load chapters WITHOUT any 404 errors!"