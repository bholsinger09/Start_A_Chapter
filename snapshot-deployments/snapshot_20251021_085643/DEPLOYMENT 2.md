# Campus Chapter Organizer - Render Deployment Guide

## Overview
This application consists of a Spring Boot backend and a Vue.js frontend, ready for deployment on Render.

## Architecture
- **Backend**: Spring Boot application with REST API
- **Frontend**: Vue.js SPA (Single Page Application)
- **Database**: PostgreSQL (managed by Render)

## Deployment Files

### `render.yaml`
Defines the complete deployment configuration for Render:
- Backend web service (Java/Spring Boot)
- Frontend web service (Node.js/Vue.js)
- PostgreSQL database

### Configuration Files

#### Backend Configuration
- `src/main/resources/application-production.properties` - Production environment settings
- `src/main/java/com/turningpoint/chapterorganizer/config/CorsConfig.java` - CORS configuration

#### Frontend Configuration  
- `frontend/.env.production` - Production environment variables
- `frontend/src/services/api.js` - API configuration with dynamic base URL

## Render Deployment Steps

### 1. Connect Repository
1. Log into [Render Dashboard](https://render.com)
2. Click "New" → "Blueprint"
3. Connect your GitHub repository
4. Render will automatically detect the `render.yaml` file

### 2. Environment Variables
The following environment variables are automatically configured via `render.yaml`:

**Backend Service:**
- `SPRING_PROFILES_ACTIVE=production`
- `SPRING_DATASOURCE_URL` (from database)
- `SPRING_DATASOURCE_USERNAME` (from database)
- `SPRING_DATASOURCE_PASSWORD` (from database)
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
- `SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.PostgreSQLDialect`
- `CORS_ALLOWED_ORIGINS=https://chapter-organizer-frontend.onrender.com`

**Frontend Service:**
- `VITE_API_BASE_URL=https://chapter-organizer-backend.onrender.com`

### 3. Database Setup
The PostgreSQL database is automatically provisioned as defined in `render.yaml`:
- Database name: `chapter_organizer`
- User: `chapter_user`
- Plan: Free tier

### 4. Build Process
**Backend:**
- Uses Maven wrapper (`./mvnw`) for consistent builds
- Builds executable JAR with `./mvnw clean package -DskipTests`
- Starts with `java -Dserver.port=$PORT -jar target/campus-chapter-organizer-1.0.0-SNAPSHOT.jar`

**Frontend:**
- Uses npm for dependency management
- Builds with `npm ci && npm run build:prod`
- Serves with `npm run preview -- --host 0.0.0.0 --port $PORT`

## Local Development

### Prerequisites
- Java 21+
- Node.js 18+
- Maven 3.8+

### Backend Setup
```bash
# Install dependencies and compile
mvn clean compile

# Run in development mode
mvn spring-boot:run

# Run with production profile
mvn spring-boot:run -Dspring.profiles.active=production
```

### Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build:prod

# Preview production build
npm run preview
```

## Service URLs (after deployment)
- **Backend API**: `https://chapter-organizer-backend.onrender.com`
- **Frontend Web App**: `https://chapter-organizer-frontend.onrender.com`
- **Database**: Managed by Render (internal connection)

## Features
- Real-time chapter data with member calculations
- State-based chapter searching
- Campus Labs integration for Florida universities
- Responsive Bootstrap UI
- RESTful API with full CRUD operations

## Monitoring
Both services include:
- Health check endpoints
- Comprehensive logging
- Error handling and recovery

## Security
- CORS properly configured for cross-origin requests
- Environment-based configuration
- Secure database connections
- Input validation and sanitization

## Support
For deployment issues:
1. Check Render service logs
2. Verify environment variables are set correctly
3. Ensure database connection is active
4. Confirm build artifacts are generated properly