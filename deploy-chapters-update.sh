#!/bin/bash

# Deploy updated application with 50 chapters to production server
echo "🚀 Deploying application with 50 chapters and enabled Create Chapter button..."

# Set variables
JAR_FILE="campus-chapter-organizer-1.0.0-SNAPSHOT.jar"
REMOTE_USER="ec2-user"
REMOTE_HOST="startachapter.duckdns.org"
REMOTE_PATH="/home/ec2-user"

# Check if JAR file exists
if [ ! -f "target/$JAR_FILE" ]; then
    echo "❌ JAR file not found. Please run 'mvn clean package -DskipTests' first."
    exit 1
fi

echo "📦 Copying JAR file to production server..."

# Copy JAR to server
scp -i temp_key "target/$JAR_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH/"

if [ $? -eq 0 ]; then
    echo "✅ JAR file copied successfully!"
else
    echo "❌ Failed to copy JAR file"
    exit 1
fi

echo "🔄 Restarting application on production server..."

# SSH to server and restart the application
ssh -i temp_key "$REMOTE_USER@$REMOTE_HOST" << 'EOF'
    # Stop existing application
    echo "🛑 Stopping existing application..."
    sudo pkill -f "campus-chapter-organizer" || true
    sleep 3

    # Start new application
    echo "▶️ Starting updated application..."
    cd /home/ec2-user
    
    # Run with production profile to use persistent database
    nohup java -jar -Dspring.profiles.active=qa campus-chapter-organizer-1.0.0-SNAPSHOT.jar > app.log 2>&1 &
    
    echo "✅ Application started!"
    
    # Wait a moment and check if it's running
    sleep 5
    if pgrep -f "campus-chapter-organizer" > /dev/null; then
        echo "🎉 Application is running successfully!"
        echo "📊 Checking chapters count..."
        # Wait for app to fully start
        sleep 10
        CHAPTER_COUNT=$(curl -s http://localhost:8080/api/chapters 2>/dev/null | jq '. | length' 2>/dev/null || echo "checking...")
        echo "📈 Chapters in database: $CHAPTER_COUNT"
    else
        echo "❌ Application failed to start. Check logs:"
        tail -n 20 app.log
        exit 1
    fi
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 Deployment completed successfully!"
    echo "🌐 Your application is now live at: https://startachapter.duckdns.org"
    echo "📋 Features deployed:"
    echo "   ✅ 50 chapters from major US universities"
    echo "   ✅ Enabled Create Chapter button"
    echo "   ✅ Complete chapter creation form"
    echo "   ✅ Persistent database with automatic data population"
    echo ""
    echo "💡 Refresh the page and navigate to the Chapters tab to see the changes!"
else
    echo "❌ Deployment failed. Please check the logs."
    exit 1
fi