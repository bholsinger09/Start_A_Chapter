# Virtual Machine QA Environment Setup Guide

This guide walks you through setting up the Chapter Organizer QA environment in a virtual machine for isolated testing.

## 🎯 Overview

Running in a VM provides:
- **Isolated environment** - No conflicts with your host system
- **Consistent testing** - Same environment across team members
- **Clean slate** - Easy to reset/restore VM state
- **Production-like** - Closer to actual deployment environment

## 🚀 VM Setup Options

### Option 1: Ubuntu Server VM (Recommended)

#### Prerequisites
- **VM Software**: VirtualBox, VMware, or Parallels
- **Ubuntu Server 22.04 LTS** ISO
- **VM Resources**: 
  - 4GB RAM minimum (8GB recommended)
  - 20GB disk space minimum
  - 2 CPU cores

#### Step 1: Create Ubuntu VM

1. **Download Ubuntu Server 22.04 LTS**:
   ```bash
   # From: https://ubuntu.com/download/server
   ```

2. **VM Configuration**:
   - **Name**: ChapterOrganizer-QA
   - **RAM**: 4-8GB
   - **Storage**: 20-40GB
   - **Network**: Bridged or NAT with port forwarding

3. **Network Setup** (if using NAT):
   - Port 8080 (Backend API)
   - Port 3000 (Frontend)
   - Port 8081 (H2 Console/Adminer)
   - Port 22 (SSH)

#### Step 2: Install Required Software

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs -y

# Install Git
sudo apt install git -y

# Install Docker (optional)
sudo apt install docker.io docker-compose -y
sudo usermod -aG docker $USER

# Install useful tools
sudo apt install curl jq unzip vim -y

# Verify installations
java -version
node -v
npm -v
git --version
```

#### Step 3: Clone and Setup Project

```bash
# Clone the repository
git clone https://github.com/bholsinger09/Start_A_Chapter.git
cd Start_A_Chapter

# Make scripts executable
chmod +x scripts/*.sh

# Install frontend dependencies
cd frontend
npm install
cd ..

# Build the project (test)
./mvnw clean package -DskipTests
```

#### Step 4: Start QA Environment

```bash
# Interactive startup
./scripts/start-qa.sh

# Or direct startup with fresh data
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa -Dapp.initialize-sample-data=true
```

#### Step 5: Access from Host Machine

**If using Bridged Network:**
```bash
# Find VM IP
ip addr show

# Access from host browser:
# http://VM_IP:8080 (Backend)
# http://VM_IP:8080/h2-console (Database)
```

**If using NAT with Port Forwarding:**
```bash
# Access from host browser:
# http://localhost:8080 (Backend)
# http://localhost:8080/h2-console (Database)
```

### Option 2: Docker-Based VM Setup

#### Step 1: Create Docker VM Script

```bash
# Create VM setup script
cat > vm-docker-setup.sh << 'EOF'
#!/bin/bash

echo "🐳 Setting up Docker-based QA environment..."

# Pull and run the complete environment
docker-compose -f docker-compose.qa.yml up --build -d

echo "✅ QA Environment started!"
echo "🌐 Access points:"
echo "   Backend: http://localhost:8080"
echo "   Frontend: http://localhost:3000"
echo "   Database Admin: http://localhost:8081"

# Show running containers
docker ps
EOF

chmod +x vm-docker-setup.sh
```

#### Step 2: Run Docker Environment

```bash
# In your VM
./vm-docker-setup.sh

# Monitor logs
docker-compose -f docker-compose.qa.yml logs -f
```

## 🗄️ Database Management in VM

### Export Data from VM

```bash
# In VM terminal
./scripts/export-db.sh

# Copy to host machine (via shared folder or SCP)
# Option 1: Shared folder
cp database-exports/latest_backup.sql /shared-folder/

# Option 2: SCP to host
scp database-exports/latest_backup.sql user@host-ip:/path/to/backup/
```

### Import Data to VM

```bash
# Copy backup file to VM
# Option 1: From shared folder
cp /shared-folder/backup_file.sql database-exports/

# Option 2: SCP from host
scp user@host-ip:/path/to/backup.sql database-exports/

# Import the data
./scripts/import-db.sh database-exports/backup_file.sql
```

### Share Data Between VMs

```bash
# VM 1: Export test scenario
./scripts/export-db.sh
# Creates: database-exports/qa_backup_20241020_143000.sql

# Transfer file to VM 2 (via network share, USB, etc.)

# VM 2: Import test scenario
./scripts/import-db.sh database-exports/qa_backup_20241020_143000.sql
```

## 🔧 VM-Specific Configurations

### Performance Optimization

```bash
# In application-qa.properties, add:
# Reduce memory usage for VM
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.batch_versioned_data=true

# Reduce logging in production-like VM
logging.level.org.hibernate.SQL=WARN
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=WARN
```

### VM Network Configuration

```bash
# Check VM network status
ip addr show
netstat -tuln | grep -E '(8080|3000|8081)'

# Test connectivity from host
ping VM_IP
curl http://VM_IP:8080/actuator/health
```

### Firewall Configuration (Ubuntu)

```bash
# Allow required ports
sudo ufw allow 8080
sudo ufw allow 3000
sudo ufw allow 8081
sudo ufw allow 22

# Enable firewall
sudo ufw enable
sudo ufw status
```

## 🧪 Testing Scenarios in VM

### Scenario 1: Feature Development

```bash
# 1. Start VM with fresh data
./scripts/start-qa.sh  # Choose option 1

# 2. Develop feature on host, test in VM
git pull  # In VM to get latest changes
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa

# 3. Export test data
./scripts/export-db.sh

# 4. Share with team
cp database-exports/latest_backup.sql /shared-folder/feature-x-test-data.sql
```

### Scenario 2: Bug Reproduction

```bash
# 1. Import production-like data
./scripts/import-db.sh database-exports/prod-replica.sql

# 2. Reproduce the bug in clean VM environment
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa

# 3. Debug with H2 console
# Access: http://VM_IP:8080/h2-console

# 4. Export problematic state
./scripts/export-db.sh
mv database-exports/latest_backup.sql database-exports/bug-reproduction.sql
```

### Scenario 3: Integration Testing

```bash
# Run full Docker environment
docker-compose -f docker-compose.qa.yml up --build

# Run automated tests against VM environment
curl -X GET http://VM_IP:8080/api/members
curl -X POST http://VM_IP:8080/api/members -H "Content-Type: application/json" -d '{...}'
```

## 🔍 Troubleshooting VM Issues

### Common Problems

**1. Can't Access from Host:**
```bash
# Check VM network
ip addr show
sudo netstat -tuln | grep 8080

# Check firewall
sudo ufw status
sudo ufw allow 8080
```

**2. Performance Issues:**
```bash
# Check VM resources
free -h
df -h
top

# Increase VM RAM/CPU in hypervisor settings
```

**3. Database Connection Issues:**
```bash
# Check QA data directory
ls -la qa-data/

# Check H2 database file
file qa-data/chapterdb.mv.db

# Restart with fresh database
rm -rf qa-data/
./scripts/start-qa.sh  # Choose option 1
```

**4. Port Conflicts:**
```bash
# Check what's using ports
sudo netstat -tuln | grep -E '(8080|3000|8081)'

# Kill conflicting processes
sudo lsof -ti:8080 | xargs sudo kill -9
```

## 📋 VM Management Commands

### VM Lifecycle

```bash
# Start QA environment
./scripts/start-qa.sh

# Stop all services
pkill -f spring-boot
docker-compose -f docker-compose.qa.yml down

# Clean environment
rm -rf qa-data/
docker system prune -f

# Backup VM state
./scripts/export-db.sh
tar -czf qa-backup-$(date +%Y%m%d).tar.gz qa-data/ database-exports/
```

### Resource Monitoring

```bash
# System resources
htop  # or top
df -h
free -h

# Application logs
tail -f logs/spring.log
docker-compose -f docker-compose.qa.yml logs -f

# Network status
ss -tuln
curl -s http://localhost:8080/actuator/health | jq
```

## 🎛️ Advanced VM Configurations

### Multiple QA Environments

```bash
# Environment 1 (port 8080)
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa

# Environment 2 (port 8081)
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa \
  -Dserver.port=8081 \
  -Dspring.datasource.url=jdbc:h2:file:./qa-data2/chapterdb
```

### Automated VM Setup Script

```bash
cat > setup-qa-vm.sh << 'EOF'
#!/bin/bash
set -e

echo "🚀 Setting up Chapter Organizer QA VM..."

# Update system
sudo apt update -y

# Install dependencies
sudo apt install -y openjdk-17-jdk nodejs npm git curl jq

# Clone repository
if [ ! -d "Start_A_Chapter" ]; then
    git clone https://github.com/bholsinger09/Start_A_Chapter.git
fi

cd Start_A_Chapter

# Setup project
chmod +x scripts/*.sh
cd frontend && npm install && cd ..

# Start QA environment
echo "✅ Setup complete! Starting QA environment..."
./scripts/start-qa.sh
EOF

chmod +x setup-qa-vm.sh
```

## 🚨 Security Considerations

- **VM Isolation**: VMs provide good isolation but ensure proper network configuration
- **Data Sensitivity**: QA data may contain sensitive information - handle appropriately
- **Network Access**: Limit network access to required ports only
- **Regular Updates**: Keep VM OS and dependencies updated

---

Your QA environment is now ready to run in virtual machines! 🎉