#!/bin/bash
set -e

echo "🚀 Deploying Complete HTTPS + Frontend Fix..."

# SSH to the server and configure everything
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "🔧 Creating complete nginx configuration..."

# Create the nginx site configuration
sudo tee /etc/nginx/sites-available/default > /dev/null << 'NGINX_EOF'
# HTTP redirect to HTTPS
server {
    listen 80;
    listen [::]:80;
    server_name startachapter.duckdns.org;
    return 301 https://$server_name$request_uri;
}

# HTTPS server with frontend and API
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

    # Frontend files
    root /var/www/html;
    index index.html index.htm;

    # Serve frontend files
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

    # Static assets caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
NGINX_EOF

echo "🌐 Deploying frontend files..."

# Create a simple frontend if it doesn't exist
if [ ! -f "/var/www/html/index.html" ]; then
    sudo mkdir -p /var/www/html
    sudo tee /var/www/html/index.html > /dev/null << 'HTML_EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Campus Chapter Organizer</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .status {
            background: #e8f5e8;
            border: 1px solid #4caf50;
            padding: 15px;
            border-radius: 5px;
            margin: 20px 0;
        }
        .api-test {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            padding: 15px;
            border-radius: 5px;
            margin: 20px 0;
        }
        button {
            background: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            margin: 5px;
        }
        button:hover {
            background: #0056b3;
        }
        #results {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            padding: 15px;
            border-radius: 5px;
            margin-top: 15px;
            white-space: pre-wrap;
            font-family: monospace;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🎓 Campus Chapter Organizer</h1>
            <p>Welcome to your chapter management system!</p>
        </div>

        <div class="status">
            <h3>✅ System Status: Online</h3>
            <p>HTTPS deployment successful with SSL certificates</p>
            <p>Backend API running with CORS enabled</p>
        </div>

        <div class="api-test">
            <h3>🔧 API Testing</h3>
            <p>Test the backend API connection:</p>
            <button onclick="testAPI('/api/chapters', 'GET')">Test Chapters API</button>
            <button onclick="testAPI('/api/members', 'GET')">Test Members API</button>
            <button onclick="testCORS()">Test CORS Headers</button>
            <div id="results"></div>
        </div>

        <div>
            <h3>📝 Getting Started</h3>
            <ol>
                <li><strong>Registration:</strong> Create your account (no default credentials)</li>
                <li><strong>Chapters:</strong> Manage your campus chapters</li>
                <li><strong>Members:</strong> Track chapter membership</li>
                <li><strong>Authentication:</strong> Uses client-side localStorage</li>
            </ol>
        </div>

        <div>
            <h3>🔗 Navigation</h3>
            <p>This is the main landing page. Your Vue.js application would normally be served here.</p>
            <p>API endpoints are available at: <code>https://startachapter.duckdns.org/api/*</code></p>
        </div>
    </div>

    <script>
        async function testAPI(endpoint, method = 'GET') {
            const results = document.getElementById('results');
            results.textContent = 'Testing ' + endpoint + '...';
            
            try {
                const response = await fetch(endpoint, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                
                const data = await response.json();
                results.textContent = 'Success ✅\n' + 
                    'Status: ' + response.status + '\n' +
                    'Response: ' + JSON.stringify(data, null, 2);
            } catch (error) {
                results.textContent = 'Error ❌\n' + error.message;
            }
        }

        async function testCORS() {
            const results = document.getElementById('results');
            results.textContent = 'Testing CORS headers...';
            
            try {
                const response = await fetch('/api/chapters', {
                    method: 'OPTIONS',
                    headers: {
                        'Origin': 'https://startachapter.duckdns.org'
                    }
                });
                
                const corsHeaders = {
                    'Access-Control-Allow-Origin': response.headers.get('Access-Control-Allow-Origin'),
                    'Access-Control-Allow-Methods': response.headers.get('Access-Control-Allow-Methods'),
                    'Access-Control-Allow-Headers': response.headers.get('Access-Control-Allow-Headers')
                };
                
                results.textContent = 'CORS Headers ✅\n' + 
                    JSON.stringify(corsHeaders, null, 2);
            } catch (error) {
                results.textContent = 'CORS Error ❌\n' + error.message;
            }
        }
    </script>
</body>
</html>
HTML_EOF

    # Set proper permissions
    sudo chown -R www-data:www-data /var/www/html
    sudo chmod -R 755 /var/www/html
fi

echo "🔄 Restarting services..."

# Test nginx configuration
sudo nginx -t

# Restart nginx
sudo systemctl restart nginx
sudo systemctl status nginx --no-pager

echo "🚀 Starting backend..."

# Kill any existing Java processes
sudo pkill -f "java.*chapter" || true
sleep 2

# Start the backend
cd /home/ubuntu
nohup java -jar campus-chapter-organizer-1.0.0-SNAPSHOT.jar --spring.profiles.active=production > backend.log 2>&1 &
sleep 5

echo "✅ Deployment complete!"

# Test everything
echo "🔍 Testing complete setup..."

echo "Backend direct:"
curl -s http://localhost:8081/api/chapters | head -50

echo -e "\nHTTPS frontend:"
curl -k -s https://localhost/ | grep -o '<title>.*</title>'

echo -e "\nHTTPS API:"
curl -k -s https://localhost/api/chapters | head -50

echo -e "\nExternal HTTPS:"
curl -k -s https://startachapter.duckdns.org/ | grep -o '<title>.*</title>'

echo -e "\n🎉 All tests complete!"

EOF

echo "✅ Deployment script completed successfully!"
echo "🌐 Visit: https://startachapter.duckdns.org"