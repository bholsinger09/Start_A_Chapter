# Production Deployment Guide

This directory contains all production deployment configurations and scripts for the Campus Chapter Organizer application.

## 📋 Deployment Options

### 1. AWS EC2 (Recommended)
- **Guide**: `AWS_DEPLOYMENT_OPTIONS.md`
- **Config**: `docker-compose.prod.yml`
- **Scripts**: `deploy-to-production.sh`, `server-deploy.sh`

### 2. General Cloud Deployment  
- **Guide**: `DEPLOYMENT_GUIDE.md`
- **Docker**: `Dockerfile`
- **nginx**: `nginx-default.conf`

## 🚀 Quick Production Deploy

```bash
# 1. Build and deploy
./server-deploy.sh

# 2. Check deployment status
./check-deployment.sh
```

## 📁 Files Overview

- **Configuration Files**
  - `docker-compose.prod.yml` - Production Docker setup
  - `nginx-default.conf` - nginx reverse proxy config
  - `Dockerfile` - Production container image

- **Deployment Scripts**  
  - `deploy-to-production.sh` - Main deployment script
  - `server-deploy.sh` - Server-side deployment  
  - `check-deployment.sh` - Health check script
  - `quick-deploy.sh` - Fast deployment option

- **Setup Scripts**
  - `oracle-cloud-setup.sh` - Oracle Cloud configuration
  - `manual-deploy-guide.sh` - Step-by-step deployment

## 🔧 Environment Variables

Required environment variables for production:

```bash
# Database
DATABASE_URL=postgresql://user:pass@host:5432/dbname
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password

# Application  
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080
```

## 📊 Monitoring

Check application health:
```bash
curl https://your-domain.com/actuator/health
```

## 🚨 Important Notes

1. **SSL/TLS**: Ensure HTTPS is properly configured
2. **Database**: Use PostgreSQL for production (not H2)
3. **Secrets**: Never commit sensitive credentials
4. **Backup**: Regular database backups recommended

For detailed deployment instructions, see the specific guide files in this directory.