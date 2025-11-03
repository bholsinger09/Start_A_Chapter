#!/bin/bash
# Quick deployment commands for server
# Copy and paste these commands one by one in your server terminal

echo "🚀 Starting ChapterController routing fix deployment..."

echo "📂 Navigating to deployment directory..."
cd ~/Start_A_Chapter/deployment

echo "🛑 Stopping containers..."
sudo docker-compose -f docker-compose.prod.yml down

echo "🏗️ Rebuilding backend container..."
sudo docker-compose -f docker-compose.prod.yml build --no-cache chapter-backend

echo "▶️ Starting containers..."
sudo docker-compose -f docker-compose.prod.yml up -d

echo "⏳ Waiting for startup..."
sleep 30

echo "📋 Checking container status..."
sudo docker-compose -f docker-compose.prod.yml ps

echo "📝 Checking backend logs..."
sudo docker-compose -f docker-compose.prod.yml logs chapter-backend --tail=20

echo "🧪 Testing the fix..."
curl -s https://startachapter.duckdns.org/api/chapters/institutions | head -c 200

echo ""
echo "✅ Deployment complete! Test the institutions endpoint:"
echo "   curl https://startachapter.duckdns.org/api/chapters/institutions"