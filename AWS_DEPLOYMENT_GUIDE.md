# AWS Deployment Guide

This guide covers deploying the Campus Chapter Organizer application to Amazon Web Services (AWS).

## Prerequisites

- AWS Account with appropriate permissions
- AWS CLI configured
- Docker installed locally
- Domain name (optional but recommended)

## Architecture Overview

The recommended AWS architecture includes:

- **EC2 Instance**: t3.micro or larger for application hosting
- **RDS PostgreSQL**: Managed database service
- **Application Load Balancer**: For high availability and SSL termination
- **Route 53**: DNS management (if using custom domain)
- **CloudFront**: CDN for static assets (optional)

## Deployment Options

### Option 1: Single EC2 Instance (Simple)

1. **Launch EC2 Instance**
   ```bash
   # Choose Ubuntu Server 22.04 LTS
   # Instance type: t3.micro (free tier) or larger
   # Security group: Allow HTTP (80), HTTPS (443), SSH (22)
   ```

2. **Install Dependencies**
   ```bash
   sudo apt update
   sudo apt install docker.io docker-compose nginx certbot python3-certbot-nginx
   sudo usermod -aG docker $USER
   ```

3. **Deploy Application**
   ```bash
   git clone https://github.com/bholsinger09/Start_A_Chapter.git
   cd Start_A_Chapter
   sudo docker-compose -f deployment/docker-compose.prod.yml up -d
   ```

### Option 2: RDS + EC2 (Recommended)

1. **Create RDS PostgreSQL Database**
   - Engine: PostgreSQL 15
   - Instance class: db.t3.micro (free tier)
   - Storage: 20 GB gp2
   - Enable automated backups

2. **Configure Environment Variables**
   ```bash
   export DATABASE_URL="postgresql://username:password@rds-endpoint:5432/dbname"
   export SPRING_PROFILES_ACTIVE=production
   ```

3. **Deploy with External Database**
   ```bash
   # Update docker-compose.prod.yml with RDS connection
   sudo docker-compose -f deployment/docker-compose.prod.yml up -d
   ```

## SSL Configuration

### Using Let's Encrypt (Free)

1. **Configure nginx**
   ```bash
   sudo cp deployment/nginx-default.conf /etc/nginx/sites-available/default
   sudo nginx -t && sudo systemctl reload nginx
   ```

2. **Obtain SSL Certificate**
   ```bash
   sudo certbot --nginx -d your-domain.com
   ```

### Using AWS Certificate Manager

1. Request certificate in ACM
2. Configure Application Load Balancer
3. Update security groups and target groups

## Environment Variables

Required for production deployment:

```bash
# Database
DATABASE_URL=postgresql://user:pass@host:5432/dbname
DATABASE_USERNAME=your_db_user  
DATABASE_PASSWORD=your_db_password

# Application
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080

# Security (generate secure values)
JWT_SECRET=your-jwt-secret-key
ENCRYPTION_KEY=your-encryption-key
```

## Monitoring and Logs

### CloudWatch Integration
```bash
# Install CloudWatch agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/amazon_linux/amd64/latest/amazon-cloudwatch-agent.rpm
sudo rpm -U ./amazon-cloudwatch-agent.rpm
```

### Application Logs
```bash
# View application logs
sudo docker logs campus-chapter-organizer
```

## Backup Strategy

1. **RDS Automated Backups**: Enable automated backups with 7-day retention
2. **Manual Snapshots**: Create before major deployments
3. **Application Data**: Export via API endpoints

## Scaling Considerations

### Horizontal Scaling
- Use Application Load Balancer
- Deploy multiple EC2 instances
- Implement session stickiness if needed

### Vertical Scaling  
- Upgrade instance types (t3.small → t3.medium → t3.large)
- Monitor CPU and memory utilization

## Cost Optimization

1. **Free Tier Usage**
   - t3.micro EC2 instance (750 hours/month)
   - RDS db.t3.micro (750 hours/month)
   - 30 GB EBS storage

2. **Cost Monitoring**
   - Set up billing alerts
   - Use AWS Cost Explorer
   - Review resource utilization monthly

## Security Best Practices

1. **Network Security**
   - Restrict security group access
   - Use VPC with private subnets
   - Enable VPC Flow Logs

2. **Application Security**
   - Keep dependencies updated
   - Use IAM roles for EC2
   - Enable CloudTrail logging

## Troubleshooting

### Common Issues

1. **Database Connection Errors**
   ```bash
   # Check security group rules
   # Verify RDS endpoint and credentials
   # Test connection from EC2 instance
   ```

2. **SSL Certificate Issues**
   ```bash
   sudo certbot renew --dry-run
   sudo systemctl status nginx
   ```

3. **Application Not Starting**
   ```bash
   sudo docker logs campus-chapter-organizer
   sudo docker-compose logs
   ```

## Support

For AWS-specific deployment issues:
- Check AWS documentation
- Use AWS Support (if available)
- Review CloudWatch logs and metrics

For application-specific issues:
- Review application logs
- Check GitHub issues
- Refer to main documentation