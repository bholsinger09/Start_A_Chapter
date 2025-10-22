#!/bin/bash
# One-Command Deployment for Campus Chapter Organizer
# Usage: curl -sSL https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/deployment/quick-deploy.sh | bash

set -e

echo "🚀 Campus Chapter Organizer - Quick Deployment"
echo "=============================================="

# Check if running as root
if [[ $EUID -eq 0 ]]; then
   echo "❌ This script should not be run as root. Please run as a regular user with sudo privileges."
   exit 1
fi

# Detect OS
if [[ -f /etc/os-release ]]; then
    source /etc/os-release
    OS=$ID
    VERSION=$VERSION_ID
else
    echo "❌ Cannot detect OS. This script supports Ubuntu/Debian."
    exit 1
fi

# Check supported OS
if [[ "$OS" != "ubuntu" && "$OS" != "debian" ]]; then
    echo "❌ Unsupported OS: $OS. This script supports Ubuntu/Debian."
    exit 1
fi

echo "✅ Detected OS: $OS $VERSION"

# Check internet connection
if ! ping -c 1 google.com &> /dev/null; then
    echo "❌ No internet connection. Please check your network."
    exit 1
fi

echo "✅ Internet connection confirmed"

# Ask for domain name
read -p "🌐 Enter your domain name (or press Enter to use localhost): " DOMAIN
DOMAIN=${DOMAIN:-localhost}

echo "🔧 Setting up for domain: $DOMAIN"

# Download and run main setup script
echo "📥 Downloading setup script..."
curl -sSL https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/deployment/oracle-cloud-setup.sh -o setup.sh
chmod +x setup.sh

# Run setup with domain substitution
sed -i "s/your-domain.com/$DOMAIN/g" setup.sh
./setup.sh

# Initial deployment
echo "🚀 Running initial deployment..."
/opt/chapter-organizer/scripts/deploy.sh

# Setup SSL if not localhost
if [[ "$DOMAIN" != "localhost" ]]; then
    read -p "🔒 Setup SSL certificate? (y/n): " SETUP_SSL
    if [[ "$SETUP_SSL" == "y" || "$SETUP_SSL" == "Y" ]]; then
        sudo certbot --nginx -d "$DOMAIN" --non-interactive --agree-tos --email admin@"$DOMAIN"
    fi
fi

# Display final information
echo ""
echo "🎉 Deployment Complete! 🎉"
echo "========================="
echo ""
echo "🌐 Application URL: http://$DOMAIN"
if [[ "$DOMAIN" != "localhost" ]]; then
    echo "🔒 HTTPS URL: https://$DOMAIN"
fi
echo ""
echo "📊 Useful Commands:"
echo "  - Check status: sudo systemctl status chapter-organizer"
echo "  - View logs: sudo journalctl -u chapter-organizer -f"
echo "  - Deploy updates: /opt/chapter-organizer/scripts/deploy.sh"
echo "  - Create backup: /opt/chapter-organizer/scripts/backup.sh"
echo ""
echo "🔧 Configuration files:"
echo "  - Nginx: /etc/nginx/sites-available/chapter-organizer"
echo "  - Service: /etc/systemd/system/chapter-organizer.service"
echo "  - App directory: /opt/chapter-organizer/"
echo ""

# Cleanup
rm -f setup.sh

echo "✅ Setup complete! Your Campus Chapter Organizer is now running!"