# AWS Deployment Options

This document outlines different deployment strategies for the Campus Chapter Organizer on Amazon Web Services.

## Option 1: Single Instance (Free Tier)

**Best for:** Development, small-scale testing, low traffic

**Resources:**
- EC2 t3.micro instance (free tier eligible)
- Built-in H2 database OR RDS PostgreSQL db.t3.micro
- Elastic IP address
- Basic security groups

**Cost:** $0 (within free tier limits)

**Setup:**
```bash
# Launch t3.micro Ubuntu instance
# Install Docker and Java
sudo apt update && sudo apt install docker.io openjdk-21-jdk
git clone https://github.com/bholsinger09/Start_A_Chapter.git
cd Start_A_Chapter
sudo docker-compose up -d
```

## Option 2: Load Balanced (Production Ready)

**Best for:** Production environments, high availability

**Resources:**
- Application Load Balancer
- 2+ EC2 instances (t3.small or larger)
- RDS PostgreSQL (Multi-AZ)
- ElastiCache Redis (optional)
- Route 53 for DNS
- CloudFront CDN

**Estimated Cost:** $100-200/month

**Architecture:**
```
Internet → Route 53 → CloudFront → ALB → EC2 Instances
                                         ↓
                                    RDS PostgreSQL
```

## Option 3: Container Orchestration

**Best for:** Microservices, scalable deployments

**Resources:**
- ECS Fargate or EKS cluster
- Application Load Balancer
- RDS PostgreSQL
- ECR for container registry
- CloudWatch for logging

**Setup with ECS:**
```bash
# Build and push to ECR
aws ecr create-repository --repository-name campus-chapter-organizer
docker build -t campus-chapter-organizer .
docker tag campus-chapter-organizer:latest 123456789.dkr.ecr.region.amazonaws.com/campus-chapter-organizer:latest
docker push 123456789.dkr.ecr.region.amazonaws.com/campus-chapter-organizer:latest
```

## Option 4: Serverless (AWS Lambda)

**Best for:** Variable traffic, cost optimization

**Resources:**
- AWS Lambda with Spring Cloud Function
- API Gateway
- RDS Proxy for database connections
- CloudFormation for infrastructure

**Considerations:**
- Cold start latency
- 15-minute execution timeout
- Memory and package size limits

## Database Options

### Option A: RDS PostgreSQL
```yaml
# Recommended settings
Engine: PostgreSQL 15
Instance Class: db.t3.micro (free tier) to db.r5.large
Storage: 20GB to 1TB gp2/gp3
Multi-AZ: Yes (for production)
Backup Retention: 7 days minimum
```

### Option B: Aurora Serverless
```yaml
# For variable workloads
Engine: Aurora PostgreSQL
Scaling: 0.5 to 16 ACUs
Backup: Automated to S3
Cost: Pay per request
```

### Option C: DocumentDB (Alternative)
```yaml
# If considering NoSQL migration
Engine: MongoDB-compatible
Instance Class: db.t3.medium+
Storage: Encrypted, automated backups
```

## Networking Architecture

### VPC Configuration
```yaml
VPC CIDR: 10.0.0.0/16
Public Subnets: 10.0.1.0/24, 10.0.2.0/24
Private Subnets: 10.0.10.0/24, 10.0.20.0/24
Database Subnets: 10.0.100.0/24, 10.0.200.0/24
```

### Security Groups
```yaml
ALB Security Group:
  - HTTP (80) from 0.0.0.0/0
  - HTTPS (443) from 0.0.0.0/0

EC2 Security Group:
  - HTTP (8080) from ALB Security Group
  - SSH (22) from Admin IPs

RDS Security Group:
  - PostgreSQL (5432) from EC2 Security Group
```

## Cost Optimization Strategies

### 1. Reserved Instances
- 1-year or 3-year commitments
- Up to 75% savings on compute

### 2. Spot Instances
- Use for development/testing
- 90% savings with interruption risk

### 3. Auto Scaling
```yaml
Target Group: 70% CPU utilization
Min Instances: 2
Max Instances: 10
Scale-out: Add 1 instance when CPU > 70% for 2 minutes
Scale-in: Remove 1 instance when CPU < 40% for 5 minutes
```

### 4. RDS Cost Management
- Use read replicas for read-heavy workloads
- Schedule stop/start for development databases
- Use Aurora Serverless for variable loads

## Monitoring and Alerting

### CloudWatch Metrics
```yaml
Application Metrics:
  - EC2 CPU utilization
  - ALB response times
  - RDS connections
  - Memory utilization

Custom Metrics:
  - Application error rates
  - Database query performance
  - User registration rates
```

### Alerting Setup
```yaml
High Priority:
  - Application down (5xx errors > 5%)
  - Database CPU > 80%
  - Disk space < 20%

Medium Priority:
  - Response time > 2 seconds
  - Memory utilization > 85%
```

## Disaster Recovery

### Backup Strategy
```yaml
RDS Automated Backups: 7 days
Manual DB Snapshots: Before deployments
Application Code: Git + S3 backup
Configuration: AWS Systems Manager Parameter Store
```

### Recovery Procedures
1. **Database Recovery**
   - Restore from automated backup
   - Point-in-time recovery available

2. **Application Recovery**
   - Deploy from latest Git tag
   - Restore configuration from Parameter Store
   - Update DNS if needed

## Security Best Practices

### 1. IAM Roles and Policies
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "rds:DescribeDBInstances",
        "s3:GetObject"
      ],
      "Resource": "*"
    }
  ]
}
```

### 2. Encryption
- EBS volumes encrypted
- RDS encryption at rest
- TLS 1.2+ for all communications
- AWS KMS for key management

### 3. Network Security
- VPC with private subnets
- NAT Gateway for outbound traffic
- VPC Flow Logs enabled
- AWS WAF for application protection

## Deployment Automation

### AWS CodePipeline
```yaml
Source: GitHub repository
Build: AWS CodeBuild
  - Maven build
  - Docker image creation
  - ECR push
Deploy: AWS CodeDeploy
  - Blue/Green deployment
  - Automatic rollback on failure
```

### Infrastructure as Code
```yaml
Tool: AWS CloudFormation or Terraform
Templates:
  - VPC and networking
  - EC2 instances and ALB  
  - RDS database
  - Security groups
  - IAM roles and policies
```

## Migration Considerations

### From Other Clouds
1. Database migration using AWS DMS
2. Application deployment via containers
3. DNS migration with Route 53
4. SSL certificate migration to ACM

### From On-Premises
1. Hybrid connectivity via VPN/Direct Connect
2. Staged migration approach
3. Data synchronization during transition
4. Testing and validation procedures

Choose the deployment option that best matches your requirements for cost, scalability, availability, and operational complexity.