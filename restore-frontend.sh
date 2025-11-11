#!/bin/bash
set -e

echo "🚀 Restoring Original Vue.js Frontend..."

# SSH to the server and restore the frontend
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "🗑️ Removing temporary landing page..."
sudo rm -rf /var/www/html/*

echo "✅ Temporary files removed"
EOF

echo "📦 Uploading original Vue.js frontend..."

# Upload the frontend dist files
cd frontend
tar -czf frontend-restore.tar.gz -C dist .
scp frontend-restore.tar.gz ubuntu@3.91.153.33:/tmp/

# SSH back and extract the frontend
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "📂 Extracting Vue.js frontend..."
cd /tmp
sudo tar -xzf frontend-restore.tar.gz -C /var/www/html/

# Set proper permissions
sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html

echo "🔧 Updating nginx configuration for Vue.js SPA..."

# Update nginx config to properly handle Vue.js SPA routing
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
        
        # Add security headers
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-XSS-Protection "1; mode=block" always;
        add_header X-Content-Type-Options "nosniff" always;
    }

    # API proxy to backend
    location /api/ {
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

    # Static assets caching (CSS, JS, images)
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
NGINX_EOF

echo "🔄 Testing and restarting nginx..."
sudo nginx -t
sudo systemctl reload nginx

echo "🧹 Cleaning up..."
rm -f /tmp/frontend-restore.tar.gz

echo "✅ Original Vue.js frontend restored!"

# Test the deployment
echo "🔍 Testing frontend..."
curl -k -s https://localhost/ | grep -o '<title>.*</title>' || echo "Testing..."

EOF

# Clean up local tar file
rm -f frontend/frontend-restore.tar.gz

echo "🎉 Frontend restoration complete!"
echo "🌐 Visit: https://startachapter.duckdns.org"
echo "📱 Your original Vue.js application is now live!"