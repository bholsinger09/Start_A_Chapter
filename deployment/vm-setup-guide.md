# Free Linux VM Deployment Guide
## Campus Chapter Organizer - Self-Hosted Solution

### Recommended Platform: Oracle Cloud Infrastructure (OCI)

## Option 1: Oracle Cloud Always Free Tier Setup

### Step 1: Account Creation
1. Go to [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/)
2. Sign up with email (requires phone verification)
3. No credit card charges - truly free forever

### Step 2: VM Configuration
**Recommended Setup:**
- **Instance**: VM.Standard.A1.Flex (ARM-based)
- **CPU**: 4 OCPU cores
- **Memory**: 24 GB RAM
- **Storage**: 200 GB Boot Volume
- **OS**: Ubuntu 22.04 LTS
- **Network**: Public IP with security list allowing ports 80, 443, 8080

### Step 3: Security Setup
```bash
# Create security list rules
Ingress Rules:
- Port 22 (SSH): 0.0.0.0/0
- Port 80 (HTTP): 0.0.0.0/0
- Port 443 (HTTPS): 0.0.0.0/0
- Port 8080 (Spring Boot): 0.0.0.0/0
- Port 3000 (Vue Dev): Your IP only
```

## Alternative Options

### Option 2: AWS EC2 Free Tier (12 months)
- **Instance**: t2.micro (1 vCPU, 1 GB RAM)
- **Storage**: 30 GB EBS
- **Limitations**: Only 12 months free, limited resources

### Option 3: Google Cloud e2-micro (Always Free)
- **Instance**: e2-micro (1 vCPU, 1 GB RAM)
- **Storage**: 30 GB
- **Limitations**: Very low bandwidth (1GB/month outbound)

## Server Architecture

### Deployment Model: Single VM with Reverse Proxy
```
Internet → Nginx (Port 80/443) → {
  /api/* → Spring Boot (Port 8080)
  /* → Vue.js Static Files
}
```

### Directory Structure
```
/opt/chapter-organizer/
├── backend/                 # Spring Boot JAR
├── frontend/               # Built Vue.js files
├── nginx/                  # Nginx configuration
├── scripts/               # Deployment scripts
└── logs/                  # Application logs
```

## Software Stack

### Required Components
1. **Java 17+** (for Spring Boot)
2. **Node.js 18+** (for building Vue.js)
3. **Nginx** (reverse proxy & static file serving)
4. **Git** (for code deployment)
5. **PM2** (process management)
6. **Certbot** (free SSL certificates)

### Optional Enhancements
- **Docker** (containerized deployment)
- **PostgreSQL** (if switching from H2)
- **Redis** (caching)
- **Fail2ban** (security)

## Cost Analysis

### Oracle Cloud (Recommended)
- **Monthly Cost**: $0
- **Setup Time**: 2-3 hours
- **Maintenance**: Low
- **Performance**: Excellent (ARM processors)

### AWS Free Tier
- **Monthly Cost**: $0 (first 12 months), then ~$8.50/month
- **Setup Time**: 1-2 hours
- **Maintenance**: Low
- **Performance**: Good (limited resources)

### Vultr/DigitalOcean (Post-Credit)
- **Monthly Cost**: $4-6/month
- **Setup Time**: 1 hour
- **Maintenance**: Very low
- **Performance**: Good

## Next Steps

1. Choose your platform (Oracle Cloud recommended)
2. Create account and provision VM
3. Configure security groups/firewall
4. Install required software stack
5. Set up automated deployment pipeline
6. Configure domain name and SSL
7. Implement monitoring and backups

Would you like me to proceed with creating the detailed setup scripts for your chosen platform?