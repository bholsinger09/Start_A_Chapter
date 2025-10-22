# 🚀 Free Linux VM Deployment Guide
## Campus Chapter Organizer - Self-Hosted Forever

This guide shows you how to deploy your Campus Chapter Organizer application on a **completely free** Linux virtual machine that runs **24/7** without going through Render or other paid services.

## 🎯 Quick Start (2 minutes)

### Option 1: One-Command Deployment
```bash
curl -sSL https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/deployment/quick-deploy.sh | bash
```

### Option 2: Manual Setup
1. Download the setup script: `wget https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/deployment/oracle-cloud-setup.sh`
2. Make it executable: `chmod +x oracle-cloud-setup.sh`
3. Run it: `./oracle-cloud-setup.sh`
4. Deploy your app: `/opt/chapter-organizer/scripts/deploy.sh`

## 🏆 Recommended: Oracle Cloud Always Free

### Why Oracle Cloud?
- ✅ **Truly Free Forever** - No time limits
- ✅ **Powerful ARM Processors** - 4 CPU cores, 24GB RAM
- ✅ **Generous Limits** - 200GB storage, 10TB bandwidth
- ✅ **Enterprise Grade** - 99.9% uptime SLA
- ✅ **Global Network** - Multiple regions

### Setup Steps:

#### 1. Create Oracle Cloud Account
1. Go to [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/)
2. Sign up (requires phone verification, no credit card charges)
3. Complete identity verification

#### 2. Launch VM Instance
1. Go to **Compute** → **Instances**
2. Click **Create Instance**
3. Configure:
   - **Name**: `chapter-organizer-vm`
   - **Image**: `Ubuntu 22.04 LTS`
   - **Shape**: `VM.Standard.A1.Flex` (ARM-based)
   - **CPU**: 4 OCPUs
   - **Memory**: 24 GB
   - **Boot Volume**: 200 GB
   - **Network**: Assign public IP
4. **Download SSH key** (save it securely!)
5. Click **Create**

#### 3. Configure Security
1. Go to **Networking** → **Virtual Cloud Networks**
2. Click your VCN → **Security Lists** → **Default Security List**
3. Add **Ingress Rules**:
   - Port 22 (SSH): `0.0.0.0/0`
   - Port 80 (HTTP): `0.0.0.0/0`
   - Port 443 (HTTPS): `0.0.0.0/0`
   - Port 8080 (Spring Boot): `0.0.0.0/0`

#### 4. Connect and Deploy
```bash
# Connect to your VM
ssh -i your-ssh-key.key ubuntu@YOUR_VM_IP

# Run quick deployment
curl -sSL https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/deployment/quick-deploy.sh | bash
```

## 🔄 Alternative Free Options

### AWS EC2 Free Tier (12 months)
```bash
# Launch t2.micro instance with Ubuntu 22.04
# Then run:
curl -sSL https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/deployment/quick-deploy.sh | bash
```

### Google Cloud e2-micro (Always Free)
```bash
# Create e2-micro instance with Ubuntu 22.04
# Then run:
curl -sSL https://raw.githubusercontent.com/bholsinger09/Start_A_Chapter/main/deployment/quick-deploy.sh | bash
```

## 🏗️ What Gets Installed

### Software Stack
- **Java 17** - For Spring Boot backend
- **Node.js 18** - For building Vue.js frontend
- **Nginx** - Reverse proxy and static file server
- **PM2** - Process management
- **Certbot** - Free SSL certificates
- **UFW Firewall** - Security

### Directory Structure
```
/opt/chapter-organizer/
├── backend/           # Spring Boot JAR file
├── frontend/          # Built Vue.js static files
├── scripts/
│   ├── deploy.sh     # Deployment script
│   ├── backup.sh     # Backup script
│   └── update.sh     # Update script
├── logs/             # Application logs
└── backups/          # Automatic backups
```

### Services Created
- **chapter-organizer.service** - Systemd service for Spring Boot
- **nginx** - Web server with auto-start
- **Cron job** - Daily backups at 2 AM

## 🔧 Management Commands

### Deployment & Updates
```bash
# Deploy latest code from GitHub
/opt/chapter-organizer/scripts/deploy.sh

# Update everything (backup + deploy)
/opt/chapter-organizer/scripts/update.sh

# Manual backup
/opt/chapter-organizer/scripts/backup.sh
```

### Service Management
```bash
# Check application status
sudo systemctl status chapter-organizer

# View real-time logs
sudo journalctl -u chapter-organizer -f

# Restart application
sudo systemctl restart chapter-organizer

# Check Nginx status
sudo systemctl status nginx
```

### Troubleshooting
```bash
# Check if ports are open
sudo ufw status
sudo netstat -tulpn | grep -E ':80|:443|:8080'

# Test backend directly
curl http://localhost:8080/api/monitoring/health

# Test frontend
curl -I http://localhost/

# Check disk space
df -h

# Check memory usage
free -h
```

## 🔒 Security & SSL

### Automatic SSL Setup
```bash
# For domain-based deployment
sudo certbot --nginx -d yourdomain.com

# Auto-renewal is configured automatically
```

### Security Features
- UFW firewall configured
- Nginx security headers
- Regular security updates via unattended-upgrades
- SSH key-based authentication

## 📊 Monitoring & Maintenance

### Health Checks
- **Application**: `http://your-domain/health`
- **Backend API**: `http://your-domain/api/monitoring/health`
- **System**: Built-in systemd monitoring

### Automatic Backups
- Daily backups at 2 AM
- Keeps last 7 backups
- Located in `/opt/chapter-organizer/backups/`

### Log Management
- Application logs: `sudo journalctl -u chapter-organizer -f`
- Nginx logs: `/var/log/nginx/access.log` and `/var/log/nginx/error.log`
- System logs: `sudo journalctl -f`

## 🚀 CI/CD Integration

### GitHub Actions Setup
1. Add these secrets to your GitHub repository:
   - `HOST`: Your VM's IP address
   - `USERNAME`: `ubuntu` (or your user)
   - `SSH_PRIVATE_KEY`: Contents of your SSH private key

2. The workflow in `.github/workflows/deploy-vm.yml` will automatically deploy on push to main.

### Manual GitHub Integration
```bash
# Set up webhook for auto-deployment
curl -X POST \
  -H "Authorization: token YOUR_GITHUB_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "web", "active": true, "events": ["push"], "config": {"url": "http://YOUR_VM_IP/webhook", "content_type": "json"}}' \
  https://api.github.com/repos/bholsinger09/Start_A_Chapter/hooks
```

## 💰 Cost Comparison

| Platform | Monthly Cost | Resources | Duration |
|----------|-------------|-----------|----------|
| **Oracle Cloud** | **$0** | 4 CPU, 24GB RAM | **Forever** |
| AWS Free Tier | $0 → $8.50 | 1 CPU, 1GB RAM | 12 months |
| Google Cloud | $0 | 1 CPU, 1GB RAM | Forever* |
| DigitalOcean | $4 | 1 CPU, 1GB RAM | Ongoing |
| Vultr | $2.50 | 1 CPU, 512MB RAM | Ongoing |
| **Render** | **$7/month** | Unknown limits | Ongoing |

**Winner**: Oracle Cloud - Most powerful and completely free forever!

## 🆘 Getting Help

### Common Issues

1. **Can't connect to SSH**
   - Check security group allows port 22
   - Verify SSH key permissions: `chmod 600 your-key.pem`

2. **Application won't start**
   - Check logs: `sudo journalctl -u chapter-organizer -f`
   - Verify Java version: `java -version`

3. **Can't access website**
   - Check firewall: `sudo ufw status`
   - Verify Nginx: `sudo nginx -t`
   - Check if port 80/443 are open

4. **Out of disk space**
   - Clean old backups: `find /opt/chapter-organizer/backups -mtime +7 -delete`
   - Check usage: `du -h /opt/chapter-organizer`

### Support Resources
- **Oracle Cloud Docs**: [Oracle Cloud Documentation](https://docs.oracle.com/en-us/iaas/)
- **Ubuntu Server Guide**: [Ubuntu Server Documentation](https://ubuntu.com/server/docs)
- **GitHub Issues**: Report problems in the repository issues

## 🎉 Success!

Once deployed, your Campus Chapter Organizer will be running 24/7 on your free VM:

- **Frontend**: Served by Nginx with caching and compression
- **Backend**: Spring Boot API with health monitoring
- **Database**: H2 with persistent storage
- **SSL**: Free Let's Encrypt certificates
- **Monitoring**: Built-in health checks and logging
- **Backups**: Automatic daily backups
- **Updates**: One-command deployments

**Your application is now self-hosted, free, and under your complete control!** 🚀

---

*No more vendor lock-in, no more monthly fees, no more deployment limits!*