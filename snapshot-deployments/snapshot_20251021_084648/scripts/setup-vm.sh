#!/bin/bash

# Quick VM Setup Script for Chapter Organizer QA Environment
# This script automates the setup process for Ubuntu-based VMs

set -e

echo "🚀 Chapter Organizer QA VM Setup Starting..."
echo "============================================="

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Check if running as root
if [ "$EUID" -eq 0 ]; then 
    print_error "Please run this script as a regular user, not root"
    exit 1
fi

print_header "1. Updating System Packages"
sudo apt update -y
sudo apt upgrade -y
print_status "System updated successfully"

print_header "2. Installing Java 17"
if ! command -v java &> /dev/null; then
    sudo apt install -y openjdk-17-jdk
    print_status "Java 17 installed"
else
    print_status "Java already installed: $(java -version 2>&1 | head -n 1)"
fi

print_header "3. Installing Node.js 18"
if ! command -v node &> /dev/null; then
    curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
    sudo apt install -y nodejs
    print_status "Node.js installed"
else
    print_status "Node.js already installed: $(node -v)"
fi

print_header "4. Installing Git and Tools"
sudo apt install -y git curl jq unzip vim htop
print_status "Development tools installed"

print_header "5. Installing Docker (Optional)"
read -p "Install Docker? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    if ! command -v docker &> /dev/null; then
        sudo apt install -y docker.io docker-compose
        sudo usermod -aG docker $USER
        print_status "Docker installed - you may need to log out and back in"
    else
        print_status "Docker already installed"
    fi
fi

print_header "6. Cloning Chapter Organizer Repository"
if [ ! -d "Start_A_Chapter" ]; then
    git clone https://github.com/bholsinger09/Start_A_Chapter.git
    print_status "Repository cloned successfully"
else
    print_status "Repository already exists - updating..."
    cd Start_A_Chapter
    git pull
    cd ..
fi

print_header "7. Setting Up Project"
cd Start_A_Chapter

# Make scripts executable
chmod +x scripts/*.sh
print_status "Scripts made executable"

# Install frontend dependencies
if [ -d "frontend" ]; then
    print_status "Installing frontend dependencies..."
    cd frontend
    npm install
    cd ..
    print_status "Frontend dependencies installed"
fi

# Test Maven build
print_status "Testing Maven build..."
./mvnw clean compile -DskipTests
print_status "Maven build successful"

print_header "8. Configuring Firewall"
read -p "Configure firewall to allow QA ports? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    sudo ufw allow 8080 comment "QA Backend"
    sudo ufw allow 3000 comment "QA Frontend" 
    sudo ufw allow 8081 comment "QA Database Admin"
    sudo ufw allow 22 comment "SSH"
    
    # Check if ufw is active
    if ! sudo ufw status | grep -q "Status: active"; then
        read -p "Enable firewall? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            sudo ufw --force enable
            print_status "Firewall enabled with QA ports allowed"
        fi
    else
        print_status "Firewall rules added"
    fi
fi

print_header "9. Network Information"
VM_IP=$(ip route get 8.8.8.8 | grep -oP 'src \K\S+')
print_status "VM IP Address: $VM_IP"

echo ""
echo "🎉 VM Setup Complete!"
echo "===================="
echo ""
echo "📋 Next Steps:"
echo "1. Start QA environment:"
echo "   ./scripts/start-qa.sh"
echo ""
echo "2. Or start with fresh data:"
echo "   ./mvnw spring-boot:run -Dspring-boot.run.profiles=qa -Dapp.initialize-sample-data=true"
echo ""
echo "3. Or use Docker:"
echo "   docker-compose -f docker-compose.qa.yml up --build"
echo ""
echo "🌐 Access Points (from host machine):"
echo "   Backend API: http://$VM_IP:8080"
echo "   H2 Console:  http://$VM_IP:8080/h2-console"
if command -v docker &> /dev/null; then
echo "   Frontend:    http://$VM_IP:3000 (Docker only)"
echo "   DB Admin:    http://$VM_IP:8081 (Docker only)"
fi
echo ""
echo "🔧 Useful Commands:"
echo "   ./scripts/export-db.sh    # Export database"
echo "   ./scripts/import-db.sh    # Import database"
echo "   htop                      # Monitor resources"
echo "   docker ps                 # Check containers (if Docker installed)"
echo ""
echo "📖 See VM-SETUP-GUIDE.md for detailed instructions"