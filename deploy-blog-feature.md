# Deploy Blog Feature to Production

## Steps to deploy the blog feature to your production server (startachapter.duckdns.org):

### 1. SSH into your production server
```bash
ssh your-username@your-server-ip
# or however you access your production server
```

### 2. Navigate to your project directory
```bash
cd /path/to/your/Start_A_Chapter  # Wherever you have the project on the server
```

### 3. Pull the latest changes from GitHub
```bash
git pull origin main
```

### 4. Rebuild and restart the Docker containers
```bash
cd deployment
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.prod.yml up -d
```

### 5. Check deployment status
```bash
docker-compose -f docker-compose.prod.yml logs -f
```

### 6. Verify the blog feature
- Go to https://startachapter.duckdns.org
- Log in with testuser/password123
- Check if the Blog tab appears in navigation
- Test creating a blog post

## Alternative: One-command deployment (if available)
If you have the quick deployment script configured:
```bash
./quick-deploy.sh
```

## Database Migration Notes
The blog feature adds new tables (Blog, Comment) which should be created automatically by:
- Spring Boot JPA auto-creation (if enabled)
- Or Flyway migrations (if you have migration files)

Make sure your production database settings allow for table creation or have the proper migration setup.

## Troubleshooting
- Check logs: `docker-compose -f docker-compose.prod.yml logs backend frontend`
- Verify containers are running: `docker-compose -f docker-compose.prod.yml ps`
- Check database connection and tables are created
- Ensure frontend build includes the new blog components