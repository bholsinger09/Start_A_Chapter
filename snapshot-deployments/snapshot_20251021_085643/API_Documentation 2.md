# Campus Chapter Organizer API Documentation

## Base URL
```
http://localhost:8080
```

## Overview
The Campus Chapter Organizer provides a RESTful API for managing university chapters, their members, and events. The API supports full CRUD (Create, Read, Update, Delete) operations for all entities.

## Entities

### Chapter
- Represents a university chapter
- Has many members and events
- Fields: id, name, universityName, state, city, description, active, createdAt, updatedAt

### Member
- Represents a chapter member
- Belongs to a chapter
- Fields: id, firstName, lastName, email, phoneNumber, role, active, major, graduationYear, chapter, createdAt, updatedAt

### Event
- Represents a chapter event
- Belongs to a chapter
- Fields: id, title, description, eventDateTime, location, type, maxAttendees, currentAttendees, active, chapter, createdAt, updatedAt

## API Endpoints

### Chapters

#### Get All Chapters
```
GET /api/chapters
```
Returns all chapters with their members and events included.

**Response Example:**
```json
[
  {
    "id": 1,
    "name": "UC Berkeley",
    "universityName": "University of California, Berkeley",
    "state": "California",
    "city": "Berkeley",
    "description": "The flagship chapter at UC Berkeley",
    "active": true,
    "members": [...],
    "events": [...],
    "createdAt": "2025-10-14T11:41:32.60931",
    "updatedAt": "2025-10-14T11:41:32.609335"
  }
]
```

#### Get Chapter by ID
```
GET /api/chapters/{id}
```

#### Create Chapter
```
POST /api/chapters
Content-Type: application/json

{
  "name": "MIT Chapter",
  "universityName": "Massachusetts Institute of Technology",
  "state": "Massachusetts",
  "city": "Cambridge",
  "description": "MIT campus chapter"
}
```

#### Update Chapter
```
PUT /api/chapters/{id}
Content-Type: application/json

{
  "name": "Updated Chapter Name",
  "description": "Updated description"
}
```

#### Delete Chapter (Soft Delete)
```
DELETE /api/chapters/{id}
```

#### Search Chapters
```
GET /api/chapters/search?name={name}&state={state}&city={city}
```

#### Get Chapters by State
```
GET /api/chapters/state/{state}
```

### Members

#### Get All Members
```
GET /api/members
```
Returns all members across all chapters.

**Response Example:**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@berkeley.edu",
    "phoneNumber": null,
    "role": "PRESIDENT",
    "active": true,
    "major": null,
    "graduationYear": null,
    "createdAt": "2025-10-14T11:41:32.740991",
    "updatedAt": "2025-10-14T11:41:32.741003",
    "fullName": "John Doe"
  }
]
```

#### Get Member by ID
```
GET /api/members/{id}
```

#### Get Members by Chapter
```
GET /api/members/chapter/{chapterId}
```

#### Get Active Members by Chapter
```
GET /api/members/chapter/{chapterId}/active
```

#### Get Members by Role
```
GET /api/members/chapter/{chapterId}/role/{role}
```
Available roles: MEMBER, OFFICER, PRESIDENT, VICE_PRESIDENT, SECRETARY, TREASURER

#### Get Chapter President
```
GET /api/members/chapter/{chapterId}/president
```

#### Get Chapter Officers
```
GET /api/members/chapter/{chapterId}/officers
```

#### Create Member
```
POST /api/members
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@berkeley.edu",
  "phoneNumber": "555-1234",
  "role": "MEMBER",
  "major": "Computer Science",
  "graduationYear": "2025",
  "chapter": {
    "id": 1
  }
}
```

#### Update Member
```
PUT /api/members/{id}
Content-Type: application/json

{
  "firstName": "Updated Name",
  "role": "OFFICER"
}
```

#### Update Member Role
```
PUT /api/members/{id}/role
Content-Type: application/json

{
  "role": "PRESIDENT"
}
```

#### Delete Member (Soft Delete)
```
DELETE /api/members/{id}
```

#### Activate Member
```
PUT /api/members/{id}/activate
```

#### Get Member Count by Chapter
```
GET /api/members/chapter/{chapterId}/count
```

### Events

#### Get All Events
```
GET /api/events
```
Returns all events across all chapters.

**Response Example:**
```json
[
  {
    "id": 1,
    "title": "Weekly Chapter Meeting",
    "description": "Regular weekly meeting to discuss chapter activities",
    "eventDateTime": "2025-10-16T11:41:32.75071",
    "location": "Student Union Room 201",
    "type": "MEETING",
    "maxAttendees": 50,
    "currentAttendees": 0,
    "active": true,
    "createdAt": "2025-10-14T11:41:32.751376",
    "updatedAt": "2025-10-14T11:41:32.751382",
    "full": false
  }
]
```

#### Get Event by ID
```
GET /api/events/{id}
```

#### Get Events by Chapter
```
GET /api/events/chapter/{chapterId}
```

#### Get Active Events by Chapter
```
GET /api/events/chapter/{chapterId}/active
```

#### Get Upcoming Events by Chapter
```
GET /api/events/chapter/{chapterId}/upcoming
```

#### Get Past Events by Chapter
```
GET /api/events/chapter/{chapterId}/past
```

#### Get Events by Type
```
GET /api/events/chapter/{chapterId}/type/{type}
```
Available types: EDUCATIONAL, FUNDRAISER, MEETING, NETWORKING, OTHER, POLITICAL, RECRUITMENT, SOCIAL, VOLUNTEER

#### Create Event
```
POST /api/events
Content-Type: application/json

{
  "title": "New Event",
  "description": "Event description",
  "eventDateTime": "2025-11-01T19:00:00",
  "location": "Campus Center",
  "type": "SOCIAL",
  "maxAttendees": 100,
  "chapter": {
    "id": 1
  }
}
```

#### Update Event
```
PUT /api/events/{id}
Content-Type: application/json

{
  "title": "Updated Event Title",
  "maxAttendees": 75
}
```

#### Delete Event (Soft Delete)
```
DELETE /api/events/{id}
```

#### Register for Event
```
PUT /api/events/{id}/register
```

#### Unregister from Event
```
PUT /api/events/{id}/unregister
```

#### Get Upcoming Event Count by Chapter
```
GET /api/events/chapter/{chapterId}/count/upcoming
```

#### Get Event Statistics
```
GET /api/events/{id}/stats
```

## Error Handling

All endpoints return appropriate HTTP status codes:

- `200 OK` - Successful GET, PUT operations
- `201 Created` - Successful POST operations
- `204 No Content` - Successful DELETE operations
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource (e.g., email already exists)
- `500 Internal Server Error` - Server error

Error responses include a JSON object with error details:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Chapter not found with id: 999",
  "timestamp": "2025-10-14T11:39:21.213584"
}
```

## Database Console

You can access the H2 database console at:
```
http://localhost:8080/h2-console
```

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:chapterdb`
- Username: `SA`
- Password: (leave blank)

## Sample Data

The application comes pre-loaded with:
- 3 chapters (UC Berkeley, UCLA, Stanford)
- 12 members across the chapters
- 6 events scheduled across the chapters

## Testing with cURL

Example cURL commands for testing:

```bash
# Get all chapters
curl -X GET http://localhost:8080/api/chapters

# Create a new chapter
curl -X POST http://localhost:8080/api/chapters \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MIT Chapter",
    "universityName": "Massachusetts Institute of Technology", 
    "state": "Massachusetts",
    "city": "Cambridge",
    "description": "MIT campus chapter"
  }'

# Get members by chapter
curl -X GET http://localhost:8080/api/members/chapter/1

# Create a new member
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Johnson",
    "email": "alice.johnson@berkeley.edu",
    "role": "MEMBER",
    "chapter": {"id": 1}
  }'
```