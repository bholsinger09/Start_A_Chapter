# Campus Chapter Organizer - API Documentation

## Overview

The Campus Chapter Organizer provides a RESTful API for managing student organizations, chapters, members, and events across universities.

**Base URL**: `https://your-domain.com/api`  
**Content Type**: `application/json`

## Authentication

Most endpoints require authentication. Include user credentials for protected routes.

### Public Endpoints
- `GET /api/chapters` - List all chapters
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register` - User registration

## Core Endpoints

### Chapters
- `GET /api/chapters` - Get all chapters
- `GET /api/chapters/{id}` - Get chapter by ID
- `POST /api/chapters` - Create new chapter (requires auth)
- `PUT /api/chapters/{id}` - Update chapter (requires auth)
- `DELETE /api/chapters/{id}` - Delete chapter (requires auth)

### Members
- `GET /api/members` - Get all members
- `GET /api/members/{id}` - Get member by ID
- `GET /api/members/username/{username}` - Get member by username
- `POST /api/members` - Create new member (requires auth)
- `PUT /api/members/{id}` - Update member (requires auth)
- `DELETE /api/members/{id}` - Delete member (requires auth)

### Events
- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create new event (requires auth)
- `PUT /api/events/{id}` - Update event (requires auth)
- `DELETE /api/events/{id}` - Delete event (requires auth)

## Request/Response Examples

### Create Chapter
```json
POST /api/chapters
{
  "name": "Alpha Beta Chapter",
  "universityName": "University of California, Los Angeles",
  "state": "California",
  "city": "Los Angeles",
  "description": "Student organization chapter"
}
```

### Create Member
```json
POST /api/members
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john.doe@example.com",
  "phoneNumber": "555-0123",
  "major": "Computer Science",
  "graduationYear": "2025",
  "chapterId": 1
}
```

## Error Responses

The API returns standard HTTP status codes:

- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `404` - Not Found
- `500` - Internal Server Error

Error responses include descriptive messages:

```json
{
  "error": "Chapter not found",
  "message": "Chapter with ID 123 does not exist"
}
```

## Rate Limiting

No rate limiting currently implemented.

## Support

For API support, please refer to the project documentation or create an issue in the repository.