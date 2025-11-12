# Campus Chapter Organizer

A modern web application for managing student chapter activities, member engagement, and campus community building.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 16+ (for frontend development)
- Maven 3.6+

### Development Setup
```bash
# Clone the repository
git clone https://github.com/bholsinger09/Start_A_Chapter.git
cd Start_A_Chapter

# Start backend (Spring Boot)
mvn spring-boot:run

# Start frontend (in another terminal)
cd frontend
npm install
npm run dev
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **H2 Console**: http://localhost:8080/h2-console

## 📁 Project Structure

```
├── src/main/java/                    # Spring Boot backend
│   ├── controller/                   # REST API controllers
│   ├── service/                      # Business logic layer
│   ├── repository/                   # Data access layer
│   ├── entity/                       # JPA entities
│   └── config/                       # Configuration classes
├── frontend/src/                     # Vue.js frontend
│   ├── components/                   # Reusable Vue components
│   ├── views/                        # Page components
│   ├── composables/                  # Vue composition functions
│   └── services/                     # API service layer
├── deployment/                       # Production deployment configs
└── scripts/                          # Utility scripts
```

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.1.5** - Application framework
- **Spring Data JPA** - Data persistence
- **H2 Database** - Development database
- **PostgreSQL** - Production database
- **Maven** - Dependency management

### Frontend  
- **Vue.js 3** - Frontend framework
- **Vue Router** - Client-side routing
- **Bootstrap 5** - UI components and styling
- **Vite** - Build tool and dev server

## 🏗️ Key Features

- **Chapter Management**: Create and manage student chapters across universities
- **Member Registration**: User registration and profile management
- **Event Coordination**: Event creation and management system
- **Blog Platform**: Member blogging and content sharing
- **Search & Filter**: Advanced search across chapters and members
- **Responsive Design**: Mobile-first, accessible interface

## 📚 API Documentation

The application provides RESTful APIs for all major functionalities:

### Chapters API
- `GET /api/chapters` - List all chapters
- `POST /api/chapters` - Create new chapter
- `GET /api/chapters/{id}` - Get chapter details
- `PUT /api/chapters/{id}` - Update chapter
- `DELETE /api/chapters/{id}` - Delete chapter

### Members API
- `GET /api/members` - List all members  
- `POST /api/members` - Create new member
- `GET /api/members/{id}` - Get member details
- `PUT /api/members/{id}` - Update member profile

### Authentication API
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

## 🚀 Deployment

### Local Development
Uses H2 in-memory database for quick development iterations.

### QA Environment
File-based H2 database with data persistence for testing.

### Production
PostgreSQL database with Docker containerization. See `deployment/` folder for detailed instructions.

## 🧪 Testing

```bash
# Run backend tests
mvn test

# Run frontend tests  
cd frontend
npm run test
```

## 📋 Recent Updates

### Technical Debt Reduction (November 2025)
- **Backend**: Constructor injection, optimized queries, database indexing
- **Frontend**: Centralized auth state with Vue composables  
- **Database**: N+1 query prevention, connection pooling
- **Performance**: Hibernate optimizations, strategic indexing

### Architecture Improvements
- Consistent error handling with `ControllerUtils`
- Reactive authentication state management
- Optimized entity relationships and fetch strategies
- Modern Spring Boot and Vue.js patterns

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🚨 Disclaimer

This application is **NOT** an official Turning Point USA sponsored platform. This is an independent, educational project developed by Ben Holsinger for organizational and learning purposes.

For official Turning Point USA information, visit: [www.turningpointusa.com](https://www.turningpointusa.com)