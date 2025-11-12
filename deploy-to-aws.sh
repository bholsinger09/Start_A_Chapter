#!/bin/bash

# Deploy Campus Chapter Organizer with Concurrency Improvements to AWS
# Server IP: 3.91.153.33

SERVER_IP="3.91.153.33"
APP_JAR="deployment/app.jar"
DEPLOYMENT_PATH="/home/ubuntu/campus-organizer"

echo "🚀 Deploying Campus Chapter Organizer with Concurrency Improvements to AWS..."
echo "Server: $SERVER_IP"

# Check if JAR file exists
if [ ! -f "$APP_JAR" ]; then
    echo "❌ Error: $APP_JAR not found. Please build the application first."
    exit 1
fi

echo "📦 Copying application JAR to server..."
scp -o StrictHostKeyChecking=no "$APP_JAR" "ubuntu@$SERVER_IP:~/app.jar"

if [ $? -ne 0 ]; then
    echo "❌ Failed to copy JAR file to server"
    exit 1
fi

echo "🔧 Deploying application on server..."
ssh -o StrictHostKeyChecking=no "ubuntu@$SERVER_IP" << 'EOF'
    # Stop existing application
    echo "Stopping existing application..."
    sudo pkill -f "java.*app.jar" || echo "No existing application running"
    
    # Create deployment directory
    sudo mkdir -p /opt/campus-organizer
    sudo chown ubuntu:ubuntu /opt/campus-organizer
    
    # Move JAR to deployment location
    mv ~/app.jar /opt/campus-organizer/app.jar
    chmod +x /opt/campus-organizer/app.jar
    
    # Create systemd service file
    sudo tee /etc/systemd/system/campus-organizer.service > /dev/null << 'SERVICE_EOF'
[Unit]
Description=Campus Chapter Organizer
After=network.target

[Service]
Type=simple
User=ubuntu
ExecStart=/usr/bin/java -Xmx512m -jar /opt/campus-organizer/app.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
SERVICE_EOF

    # Reload systemd and start service
    sudo systemctl daemon-reload
    sudo systemctl enable campus-organizer.service
    sudo systemctl start campus-organizer.service
    
    # Wait a moment for service to start
    sleep 5
    
    # Check service status
    echo "🔍 Checking service status..."
    sudo systemctl status campus-organizer.service --no-pager -l
    
    echo "📋 Recent logs:"
    sudo journalctl -u campus-organizer.service --no-pager -l -n 20
    
    echo "🌐 Application should be available at:"
    echo "http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8080"
EOF

echo "✅ Deployment completed!"
echo ""
echo "🔍 You can check the application status with:"
echo "ssh ubuntu@$SERVER_IP 'sudo systemctl status campus-organizer.service'"
echo ""
echo "📊 View logs with:"
echo "ssh ubuntu@$SERVER_IP 'sudo journalctl -u campus-organizer.service -f'"
echo ""
echo "🌐 Access the application at: http://$SERVER_IP:8080"