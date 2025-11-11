#!/bin/bash
set -e

echo "🔧 Deploying Working Frontend Build..."

# SSH to the server and deploy the working frontend
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "🗑️ Clearing current frontend..."
sudo rm -rf /var/www/html/*

echo "✅ Ready for new frontend"
EOF

echo "📦 Uploading working frontend build..."

# Upload the working frontend
cd /Users/benh/Documents/StartAChapter/frontend
scp frontend-final.tar.gz ubuntu@3.91.153.33:/tmp/

# SSH back and extract the working frontend
ssh ubuntu@3.91.153.33 << 'EOF'
set -e

echo "📂 Extracting working frontend..."
cd /tmp
sudo tar -xzf frontend-final.tar.gz -C /var/www/html/

# Set proper permissions
sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html

echo "🧹 Cleaning up..."
rm -f /tmp/frontend-final.tar.gz

echo "✅ Working frontend deployed!"

# Test the deployment
echo "🔍 Testing frontend..."
curl -k -s https://localhost/ | grep -o '<title>.*</title>' || echo "Testing..."

# Check what API calls the new frontend makes
echo "=== Checking frontend JS for API calls ==="
grep -o '/api/[^"]*' /var/www/html/assets/*.js | head -5 || echo "No /api/ calls found"

EOF

echo "🎉 Working frontend deployment complete!"
echo "🌐 Visit: https://startachapter.duckdns.org"
echo "✅ This should resolve the API path issues!"