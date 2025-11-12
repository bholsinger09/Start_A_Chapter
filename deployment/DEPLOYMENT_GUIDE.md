# General Deployment Guide

This guide covers general deployment instructions for the Campus Chapter Organizer across different platforms and environments.

## Quick Start

For most deployments, follow these basic steps:

1. **Prerequisites**
   - Java 21+ installed
   - Maven 3.6+ installed  
   - Database (PostgreSQL recommended for production)
   - Web server (nginx recommended)

2. **Build Application**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Run Application**
   ```bash
   java -jar target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar
   ```

## Platform-Specific Guides

### Docker Deployment
```bash
# Build image
docker build -t campus-chapter-organizer .

# Run container
docker run -p 8080:8080 \
  -e DATABASE_URL=your_db_url \
  campus-chapter-organizer
```

### Cloud Platforms

- **AWS**: See `AWS_DEPLOYMENT_GUIDE.md`
- **Oracle Cloud**: See `AWS_DEPLOYMENT_OPTIONS.md` 
- **DigitalOcean**: Similar to AWS guide
- **Heroku**: Use buildpack deployment

### Traditional Servers

1. **Install Java 21**
2. **Configure Database**
3. **Setup nginx reverse proxy**
4. **Configure SSL certificates**
5. **Setup systemd service for auto-restart**

## Configuration

### Database Configuration
```properties
# PostgreSQL (Production)
spring.datasource.url=jdbc:postgresql://localhost:5432/chapterdb
spring.datasource.username=chapter_user
spring.datasource.password=secure_password

# H2 (Development only)  
spring.datasource.url=jdbc:h2:mem:testdb
```

### Profile Configuration
```properties
# Production
spring.profiles.active=production

# Development
spring.profiles.active=dev

# QA Testing
spring.profiles.active=qa
```

## Security Checklist

- [ ] Use HTTPS in production
- [ ] Configure secure database credentials
- [ ] Update default passwords
- [ ] Enable CORS only for trusted domains
- [ ] Set up proper firewall rules
- [ ] Regular security updates

## Monitoring

### Health Checks
- Application: `GET /actuator/health`
- Database: Monitor connection pool
- Memory: Monitor JVM heap usage
- Logs: Centralized logging recommended

### Performance Monitoring
- Response times
- Database query performance  
- Resource utilization
- Error rates

## Backup Strategy

1. **Database Backups**
   - Automated daily backups
   - Test restore procedures
   - Offsite backup storage

2. **Application Backups**
   - Configuration files
   - Custom assets
   - Application logs

## Troubleshooting

### Common Issues

1. **Port Conflicts**
   ```bash
   # Check what's using port 8080
   sudo netstat -tulpn | grep :8080
   ```

2. **Database Connection**
   ```bash
   # Test database connectivity
   telnet database_host 5432
   ```

3. **Memory Issues**
   ```bash
   # Increase JVM heap size
   java -Xmx2g -jar application.jar
   ```

4. **Permission Errors**
   ```bash
   # Fix file permissions
   sudo chown -R app_user:app_group /app/directory
   ```

## Environment Variables

### Required
```bash
DATABASE_URL=jdbc:postgresql://host:5432/dbname
DATABASE_USERNAME=username
DATABASE_PASSWORD=password
SPRING_PROFILES_ACTIVE=production
```

### Optional
```bash
SERVER_PORT=8080
LOGGING_LEVEL_ROOT=INFO
JVM_OPTS="-Xmx1g -Xms512m"
```

## Best Practices

1. **Use environment-specific configurations**
2. **Implement proper logging**
3. **Set up monitoring and alerting**
4. **Regular security updates**
5. **Document deployment procedures**
6. **Test backup and restore processes**
7. **Use infrastructure as code when possible**

## Support

- Check application logs first
- Review platform-specific documentation
- Consult troubleshooting guides
- Create GitHub issues for bugs