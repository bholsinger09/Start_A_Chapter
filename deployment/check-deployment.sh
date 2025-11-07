#!/bin/bash

# Simple deployment verification and guidance script
# Run this to check deployment readiness and get instructions

echo "🔍 StartAChapter Production Deployment Check"
echo "=========================================="

# Check if JAR exists and is recent
if [ -f "app.jar" ]; then
    JAR_SIZE=$(ls -lh app.jar | awk '{print $5}')
    JAR_DATE=$(ls -l app.jar | awk '{print $6, $7, $8}')
    echo "✅ JAR file ready: app.jar ($JAR_SIZE, modified: $JAR_DATE)"
else
    echo "❌ JAR file not found. Run: cp ../target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar app.jar"
    exit 1
fi

# Check server connectivity
echo ""
echo "🌐 Checking server connectivity..."
if curl -s --head https://startachapter.duckdns.org | head -n 1 | grep -q "HTTP"; then
    echo "✅ Server is reachable at https://startachapter.duckdns.org"
    
    # Check current version
    CURRENT_ASSETS=$(curl -s https://startachapter.duckdns.org | grep -o 'src="[^"]*\.js"' | head -1)
    echo "📋 Current frontend assets: $CURRENT_ASSETS"
else
    echo "❌ Cannot reach https://startachapter.duckdns.org"
    exit 1
fi

echo ""
echo "🚀 DEPLOYMENT OPTIONS:"
echo ""
echo "Option 1: Automatic SSH Deployment (if SSH is configured)"
echo "   ./deploy-to-production.sh"
echo ""
echo "Option 2: Manual Upload via SCP"
echo "   scp app.jar ubuntu@startachapter.duckdns.org:/home/ubuntu/app/"
echo "   ssh ubuntu@startachapter.duckdns.org 'sudo systemctl restart startachapter'"
echo ""
echo "Option 3: Upload via Web Interface (if available)"
echo "   Use your hosting provider's file manager to upload app.jar"
echo ""
echo "Option 4: Setup SSH Key Authentication First"
echo "   ssh-keygen -t rsa (if you don't have a key)"
echo "   ssh-copy-id ubuntu@startachapter.duckdns.org"
echo "   Then run: ./deploy-to-production.sh"
echo ""

# Test SSH connection
echo "🔑 Testing SSH connection..."
if ssh -o ConnectTimeout=5 -o BatchMode=yes ubuntu@startachapter.duckdns.org exit 2>/dev/null; then
    echo "✅ SSH connection works! You can run: ./deploy-to-production.sh"
else
    echo "⚠️  SSH not configured. You'll need to:"
    echo "   1. Set up SSH key authentication, OR"
    echo "   2. Use manual deployment methods"
fi

echo ""
echo "📝 See DEPLOYMENT_GUIDE.md for detailed instructions"
echo ""
echo "🎯 After deployment, verify at: https://startachapter.duckdns.org/#/chapters/create"
echo "   Look for the university dropdown with auto-populating state/city fields!"