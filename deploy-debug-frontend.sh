#!/bin/bash
set -e

echo "🔧 Trying Different Frontend Build (debug version)..."

# SSH to the server and deploy a different frontend build
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "🗑️ Clearing current frontend..."
sudo rm -rf /var/www/html/*

echo "✅ Ready for new frontend"
EOF

echo "📦 Uploading debug frontend build..."

# Upload a different frontend build
cd /Users/benh/Documents/StartAChapter/frontend
scp frontend-build-debug.tar.gz ubuntu@3.91.153.33:/tmp/

# SSH back and extract the different frontend
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "📂 Extracting debug frontend..."
cd /tmp
sudo tar -xzf frontend-build-debug.tar.gz -C /var/www/html/

# Set proper permissions
sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html

echo "🧹 Cleaning up..."
rm -f /tmp/frontend-build-debug.tar.gz

echo "✅ Debug frontend deployed!"

# Check what API calls this version makes
echo "=== Checking frontend JS for API calls ==="
grep -o '/api/[^"]*' /var/www/html/assets/*.js | head -5 || echo "No /api/ calls found"

# Test the deployment
echo "🔍 Testing frontend..."
curl -k -s https://localhost/ | grep -o '<title>.*</title>' || echo "Testing..."

EOF

echo "🎉 Debug frontend deployment complete!"
echo "🌐 Test at: https://startachapter.duckdns.org/register"
echo "✅ This debug build might have different API configuration!"