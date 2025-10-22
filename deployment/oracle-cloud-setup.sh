#!/bin/bash
# Oracle Cloud Ubuntu 22.04 Setup Script
# Campus Chapter Organizer Deployment

set -e

echo "🚀 Starting Campus Chapter Organizer Server Setup..."

# Update system
echo "📦 Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install Java 17
echo "☕ Installing Java 17..."
sudo apt install -y openjdk-17-jdk

# Install Node.js 18
echo "📦 Installing Node.js 18..."
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Install Nginx
echo "🌐 Installing Nginx..."
sudo apt install -y nginx

# Install Git
echo "📁 Installing Git..."
sudo apt install -y git

# Install PM2 globally
echo "⚡ Installing PM2..."
sudo npm install -g pm2

# Install Certbot for SSL
echo "🔒 Installing Certbot..."
sudo apt install -y certbot python3-certbot-nginx

# Create application directory
echo "📂 Creating application directories..."
sudo mkdir -p /opt/chapter-organizer/{backend,frontend,nginx,scripts,logs}
sudo chown -R $USER:$USER /opt/chapter-organizer

# Configure firewall
echo "🔥 Configuring UFW firewall..."
sudo ufw allow ssh
sudo ufw allow 'Nginx Full'
sudo ufw allow 8080
sudo ufw --force enable

# Create systemd service for Spring Boot
echo "⚙️  Creating systemd service..."
sudo tee /etc/systemd/system/chapter-organizer.service > /dev/null <<EOF
[Unit]
Description=Campus Chapter Organizer Spring Boot Application
After=network.target

[Service]
Type=simple
User=$USER
WorkingDirectory=/opt/chapter-organizer/backend
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=production app.jar
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# Enable service
sudo systemctl daemon-reload
sudo systemctl enable chapter-organizer

# Create Nginx configuration
echo "🌐 Creating Nginx configuration..."
sudo tee /etc/nginx/sites-available/chapter-organizer > /dev/null <<EOF
server {
    listen 80;
    server_name your-domain.com;  # Replace with your domain
    
    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    # Serve Vue.js frontend
    location / {
        root /opt/chapter-organizer/frontend;
        try_files \$uri \$uri/ /index.html;
        
        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)\$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
    
    # Proxy API requests to Spring Boot
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # Health check endpoint
    location /health {
        proxy_pass http://localhost:8080/api/monitoring/health;
        access_log off;
    }
}
EOF

# Enable Nginx site
sudo ln -sf /etc/nginx/sites-available/chapter-organizer /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl enable nginx
sudo systemctl restart nginx

# Create deployment script
echo "📜 Creating deployment script..."
tee /opt/chapter-organizer/scripts/deploy.sh > /dev/null <<EOF
#!/bin/bash
# Deployment script for Campus Chapter Organizer

set -e
cd /opt/chapter-organizer

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "\${GREEN}🚀 Starting deployment...🚀\${NC}"

# Pull latest code
if [ ! -d "source" ]; then
    echo -e "\${YELLOW}📥 Cloning repository...\${NC}"
    git clone https://github.com/bholsinger09/Start_A_Chapter.git source
else
    echo -e "\${YELLOW}📥 Pulling latest changes...\${NC}"
    cd source && git pull origin main && cd ..
fi

# Build frontend
echo -e "\${YELLOW}🏗️  Building frontend...\${NC}"
cd source/frontend
npm ci
npm run build
cp -r dist/* /opt/chapter-organizer/frontend/
cd /opt/chapter-organizer

# Build backend
echo -e "\${YELLOW}🏗️  Building backend...\${NC}"
cd source
./mvnw clean package -DskipTests
cp target/*.jar /opt/chapter-organizer/backend/app.jar
cd /opt/chapter-organizer

# Restart services
echo -e "\${YELLOW}🔄 Restarting services...\${NC}"
sudo systemctl restart chapter-organizer
sudo systemctl reload nginx

# Wait for startup
echo -e "\${YELLOW}⏳ Waiting for application startup...\${NC}"
sleep 10

# Health check
if curl -f http://localhost:8080/api/monitoring/health > /dev/null 2>&1; then
    echo -e "\${GREEN}✅ Deployment successful! Application is healthy.\${NC}"
else
    echo -e "\${RED}❌ Deployment failed! Check logs with: sudo journalctl -u chapter-organizer -f\${NC}"
    exit 1
fi
EOF

chmod +x /opt/chapter-organizer/scripts/deploy.sh

# Create backup script
echo "💾 Creating backup script..."
tee /opt/chapter-organizer/scripts/backup.sh > /dev/null <<EOF
#!/bin/bash
# Backup script for Campus Chapter Organizer

BACKUP_DIR="/opt/chapter-organizer/backups"
DATE=\$(date +%Y%m%d_%H%M%S)

mkdir -p \$BACKUP_DIR

# Backup application files
tar -czf "\$BACKUP_DIR/app_backup_\$DATE.tar.gz" \
    /opt/chapter-organizer/backend/app.jar \
    /opt/chapter-organizer/frontend/ \
    /etc/nginx/sites-available/chapter-organizer

# Keep only last 7 backups
find \$BACKUP_DIR -name "app_backup_*.tar.gz" -mtime +7 -delete

echo "Backup completed: app_backup_\$DATE.tar.gz"
EOF

chmod +x /opt/chapter-organizer/scripts/backup.sh

# Create update script
echo "🔄 Creating update script..."
tee /opt/chapter-organizer/scripts/update.sh > /dev/null <<EOF
#!/bin/bash
# Update script for Campus Chapter Organizer

echo "🔄 Creating backup before update..."
/opt/chapter-organizer/scripts/backup.sh

echo "📥 Deploying latest version..."
/opt/chapter-organizer/scripts/deploy.sh
EOF

chmod +x /opt/chapter-organizer/scripts/update.sh

# Setup cron jobs
echo "⏰ Setting up cron jobs..."
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/chapter-organizer/scripts/backup.sh") | crontab -

echo ""
echo "✅ Setup complete! 🎉"
echo ""
echo "Next steps:"
echo "1. Replace 'your-domain.com' in /etc/nginx/sites-available/chapter-organizer"
echo "2. Run: /opt/chapter-organizer/scripts/deploy.sh"
echo "3. Setup SSL: sudo certbot --nginx -d your-domain.com"
echo ""
echo "Useful commands:"
echo "- Deploy: /opt/chapter-organizer/scripts/deploy.sh"
echo "- Update: /opt/chapter-organizer/scripts/update.sh"
echo "- Logs: sudo journalctl -u chapter-organizer -f"
echo "- Status: sudo systemctl status chapter-organizer"