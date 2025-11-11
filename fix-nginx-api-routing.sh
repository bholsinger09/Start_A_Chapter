#!/bin/bash
set -e

echo "🔧 Fixing nginx API routing - Backend expects /api/chapters"

# SSH to the server and fix nginx config
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "🔧 Updating nginx configuration - Remove rewrite rule..."

# Update nginx config to properly proxy /api/ to backend /api/ (not root)
sudo tee /etc/nginx/sites-available/default > /dev/null << 'NGINX_EOF'
# HTTP redirect to HTTPS
server {
    listen 80;
    listen [::]:80;
    server_name startachapter.duckdns.org;
    return 301 https://$server_name$request_uri;
}

# HTTPS server with Vue.js frontend and API
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name startachapter.duckdns.org;

    # SSL Configuration
    ssl_certificate /etc/letsencrypt/live/startachapter.duckdns.org/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/startachapter.duckdns.org/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-SHA256:ECDHE-RSA-AES256-SHA384;
    ssl_prefer_server_ciphers on;

    # Vue.js frontend files
    root /var/www/html;
    index index.html;

    # Handle Vue.js SPA routing - serve index.html for all routes
    location / {
        try_files $uri $uri/ /index.html;
        
        # PREVENT CACHING - Force reload of all files
        add_header Cache-Control "no-cache, no-store, must-revalidate" always;
        add_header Pragma "no-cache" always;
        add_header Expires "0" always;
        
        # Add security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-XSS-Protection "1; mode=block" always;
        add_header X-Content-Type-Options "nosniff" always;
    }

    # API proxy to backend - FIXED: proxy /api/ to backend /api/ (keep path)
    location /api/ {
        # DO NOT rewrite - backend expects /api/chapters, not /chapters
        proxy_pass http://localhost:8081/api/;
        
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;

        # CORS headers
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With" always;
        add_header Access-Control-Max-Age 3600 always;

        # Handle preflight requests
        if ($request_method = 'OPTIONS') {
            add_header Access-Control-Allow-Origin "*";
            add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
            add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With";
            add_header Access-Control-Max-Age 3600;
            add_header Content-Type "text/plain; charset=utf-8";
            add_header Content-Length 0;
            return 204;
        }
    }

    # Static assets - NO CACHING during debugging
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        add_header Cache-Control "no-cache, no-store, must-revalidate" always;
        add_header Pragma "no-cache" always;
        add_header Expires "0" always;
    }
}
NGINX_EOF

echo "🔄 Testing and reloading nginx..."
sudo nginx -t
sudo systemctl reload nginx

echo "✅ Nginx configuration fixed - removed rewrite rule!"

# Test the fix
echo "🔍 Testing corrected API routing..."

echo "Direct backend /api/chapters:"
curl -s http://localhost:8081/api/chapters

echo -e "\nNginx proxy /api/chapters (should work now):"
curl -k -s https://localhost/api/chapters

echo -e "\nExternal HTTPS /api/chapters:"
curl -k -s https://startachapter.duckdns.org/api/chapters

EOF

echo "🎉 API routing fix complete!"
echo "🌐 Test at: https://startachapter.duckdns.org/register"
echo "✅ The /api/chapters endpoint should now work correctly!"