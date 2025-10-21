# QA Environment Setup Guide

This guide explains how to set up and use the QA environment for testing new features locally with database dumps and different configurations.

## 🎯 Overview

The QA environment provides:
- **Persistent file-based H2 database** for testing
- **Database export/import capabilities** for sharing test data
- **Docker setup** for consistent deployment
- **Multiple startup options** for different testing scenarios

## 📁 Directory Structure

```
├── scripts/
│   ├── start-qa.sh          # Interactive QA startup script
│   ├── export-db.sh         # Export database to SQL
│   └── import-db.sh         # Import database from SQL
├── qa-data/                 # QA database files (auto-created)
├── database-exports/        # SQL backup files
├── docker-compose.qa.yml    # Docker setup for QA
├── Dockerfile.qa           # Backend QA Docker config
└── frontend/Dockerfile.qa  # Frontend QA Docker config
```

## 🚀 Quick Start

### Option 1: Local Development (Recommended)

1. **Start QA environment interactively:**
   ```bash
   ./scripts/start-qa.sh
   ```
   
   This will show you options:
   - Start fresh with sample data
   - Use existing QA data
   - Import from backup
   - Export current data

2. **Access the application:**
   - **Application:** http://localhost:8080
   - **H2 Console:** http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:file:./qa-data/chapterdb`
     - User: `sa`
     - Password: (leave empty)

### Option 2: Docker Environment

1. **Start with Docker Compose:**
   ```bash
   docker-compose -f docker-compose.qa.yml up --build
   ```

2. **Access services:**
   - **Backend:** http://localhost:8080
   - **Frontend:** http://localhost:3000
   - **Database Admin:** http://localhost:8081

## 🗄️ Database Management

### Export Current Data
```bash
./scripts/export-db.sh
```
Creates timestamped backup in `database-exports/` directory.

### Import Data
```bash
./scripts/import-db.sh ./database-exports/qa_backup_20241020_143000.sql
# Or use latest backup:
./scripts/import-db.sh ./database-exports/latest_backup.sql
```

### Manual Database Operations
```bash
# Start QA with fresh data
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa -Dapp.initialize-sample-data=true

# Start QA with existing data
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa

# Start QA on different port
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa -Dserver.port=8081
```

## 🔧 Configuration

### QA Profile Settings (`application-qa.properties`)

- **Database:** File-based H2 at `./qa-data/chapterdb`
- **H2 Console:** Enabled at `/h2-console`
- **Sample Data:** Configurable via `app.initialize-sample-data`
- **Debug Logging:** Enabled for SQL and application
- **CORS:** Configured for local frontend development

### Environment Variables

```bash
# Enable/disable sample data initialization
export APP_INITIALIZE_SAMPLE_DATA=true

# Enable QA mode features
export APP_QA_MODE=true

# Custom database location
export SPRING_DATASOURCE_URL=jdbc:h2:file:./custom-location/db
```

## 🧪 Testing Scenarios

### 1. Feature Development
```bash
# Start with sample data
./scripts/start-qa.sh -> Choose option 1

# Develop your feature...

# Export test data for sharing
./scripts/export-db.sh
```

### 2. Bug Reproduction
```bash
# Import production-like data
./scripts/import-db.sh ./database-exports/prod_replica.sql

# Reproduce the bug...

# Export the problematic state
./scripts/export-db.sh
```

### 3. Integration Testing
```bash
# Start with Docker for full environment
docker-compose -f docker-compose.qa.yml up --build

# Run your integration tests...
```

## 📋 Common Commands

```bash
# Check QA database status
ls -la qa-data/

# View recent backups
ls -la database-exports/

# Clean QA environment
rm -rf qa-data/

# Stop all QA Docker containers
docker-compose -f docker-compose.qa.yml down

# View QA logs
docker-compose -f docker-compose.qa.yml logs -f chapter-organizer-qa
```

## 🔍 Troubleshooting

### Database Issues
- **Can't connect to H2:** Ensure application is running and check the JDBC URL
- **Data not persisting:** Verify `qa-data` directory exists and is writable
- **Import fails:** Stop the application before importing

### Docker Issues
- **Build fails:** Check Docker daemon is running and you have sufficient disk space
- **Port conflicts:** Ensure ports 8080, 3000, 8081 are available

### Performance Issues
- **Slow startup:** QA mode has debug logging enabled; this is normal
- **Large database:** Consider cleaning old data or using a smaller sample dataset

## 🎛️ Advanced Configuration

### Custom Sample Data
1. Create custom data initialization in your `DataSeeder` component
2. Set `app.initialize-sample-data=true` in QA profile
3. Modify data generation logic as needed

### Multiple QA Environments
```bash
# QA Environment 1 (port 8080)
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa

# QA Environment 2 (port 8081) 
./mvnw spring-boot:run -Dspring-boot.run.profiles=qa -Dserver.port=8081 -Dspring.datasource.url=jdbc:h2:file:./qa-data2/chapterdb
```

## 📤 Sharing QA Data

### With Team Members
1. Export your test scenario: `./scripts/export-db.sh`
2. Share the SQL file from `database-exports/`
3. Team member imports: `./scripts/import-db.sh <file>`

### Version Control
- **DO include:** Export scripts, configurations
- **DON'T include:** `qa-data/` directory, backup files (add to `.gitignore`)

## 🚨 Security Notes

- QA environment has H2 console exposed - **never use in production**
- Database files contain sensitive test data - handle appropriately
- CORS is open for development - review before production deployment

---

Happy testing! 🎉