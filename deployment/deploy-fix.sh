#!/bin/bash

# Deployment script for ChapterController routing fix
# This script deploys the corrected application that fixes the /api/chapters/institutions routing conflict

SERVER_IP="184.73.57.225"
SERVER_USER="ubuntu"
DEPLOYMENT_DIR="~/Start_A_Chapter/deployment"

echo "🚀 Starting deployment of ChapterController routing fix..."

# Step 1: Upload the corrected jar file
echo "📦 Uploading corrected application jar..."
scp app.jar ${SERVER_USER}@${SERVER_IP}:${DEPLOYMENT_DIR}/

if [ $? -ne 0 ]; then
    echo "❌ Failed to upload jar file. Please check server connection."
    exit 1
fi

echo "✅ Jar file uploaded successfully"

# Step 2: Connect to server and rebuild containers
echo "🔧 Connecting to server to rebuild containers..."
ssh ${SERVER_USER}@${SERVER_IP} << 'ENDSSH'
cd ~/Start_A_Chapter/deployment

echo "🛑 Stopping existing containers..."
sudo docker-compose -f docker-compose.prod.yml down

echo "🏗️  Rebuilding backend container with corrected routing..."
sudo docker-compose -f docker-compose.prod.yml build --no-cache chapter-backend

echo "🚀 Starting updated containers..."
sudo docker-compose -f docker-compose.prod.yml up -d

echo "⏳ Waiting for containers to fully start..."
sleep 30

echo "🔍 Checking container status..."
sudo docker-compose -f docker-compose.prod.yml ps

echo "📋 Checking backend logs for startup..."
sudo docker-compose -f docker-compose.prod.yml logs chapter-backend --tail=20
ENDSSH

if [ $? -ne 0 ]; then
    echo "❌ Deployment failed on server"
    exit 1
fi

echo "✅ Deployment completed successfully!"
echo ""
echo "🔗 Test the fix:"
echo "   Frontend should now be able to load institutions:"
echo "   https://startachapter.org/api/chapters/institutions"
echo ""
echo "   The routing conflict has been resolved by placing"
echo "   /chapters/institutions before /chapters/{id} in ChapterController"
