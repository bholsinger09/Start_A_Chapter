# Manual Deployment Guide

## Quick Deployment to startachapter.duckdns.org

### Prerequisites
1. SSH access to your server
2. The latest JAR file: `app.jar`

### Option 1: Automatic Deployment Script
```bash
cd deployment
./deploy-to-production.sh
```

### Option 2: Manual Steps

1. **Copy JAR to server:**
```bash
scp app.jar ubuntu@startachapter.duckdns.org:/home/ubuntu/app/
```

2. **SSH into server and restart service:**
```bash
ssh ubuntu@startachapter.duckdns.org
cd /home/ubuntu/app
sudo systemctl stop startachapter
sudo systemctl start startachapter
sudo systemctl status startachapter
```

3. **Verify deployment:**
```bash
curl -I https://startachapter.duckdns.org
```

### If SSH Key Authentication Not Set Up

1. **Generate SSH key (if you don't have one):**
```bash
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

2. **Copy public key to server:**
```bash
ssh-copy-id ubuntu@startachapter.duckdns.org
```

### Alternative: Docker Deployment

If your server uses Docker, you can also build and deploy using Docker:

```bash
# Build Docker image locally
docker build -t startachapter:latest .

# Save image to tar file
docker save startachapter:latest > startachapter.tar

# Copy to server
scp startachapter.tar ubuntu@startachapter.duckdns.org:/home/ubuntu/

# SSH to server and load image
ssh ubuntu@startachapter.duckdns.org
docker load < startachapter.tar
docker-compose down
docker-compose up -d
```

### Troubleshooting

- **Service won't start:** Check logs with `sudo journalctl -u startachapter -f`
- **Permission issues:** Make sure JAR file is executable: `chmod +x app.jar`
- **Port conflicts:** Ensure port 8080 is available: `sudo lsof -i :8080`

### New Features in This Deployment

✨ **University Dropdown Implementation:**
- Replace text input with university/institution dropdown
- Auto-populate state and city fields when university is selected
- Enhanced form validation and user experience
- Complete Vue.js 3 frontend with Bootstrap 5 styling

🔧 **Backend Improvements:**
- Enhanced `/api/chapters/with-institution` endpoint
- Comprehensive SQL logging for debugging
- Global exception handler for better error responses
- Maintained backward compatibility

### Verification

After deployment, verify the new features:
1. Visit https://startachapter.duckdns.org/#/chapters/create
2. Check that university field is now a dropdown
3. Select a university and verify state/city auto-population
4. Test form submission and validation

---

**Last Updated:** November 7, 2025
**Version:** 1.0.0-SNAPSHOT with University Dropdown Feature
