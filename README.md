# Test Project – Notes API

Simple REST API for managing notes with tags, pagination, basic validation, and word frequency statistics.  
Built with **Spring Boot 3** + **MongoDB**.

## Features

- Create, read, update, and delete notes  
- Filter notes by one or more tags  
- Pagination & sorting (default: createdDate DESC)  
- Get word frequency statistics for any note (endpoint `/api/notes/{id}/word-stats`)  
- Input validation & global exception handling  

## Tech Stack

- Java 17 / 21  
- Spring Boot 3  
- Spring Data MongoDB  
- Lombok  
- Docker + Docker Compose (recommended way to run)

## API Endpoints

| Method | Endpoint                            | Description                                    | Request Body          |
|--------|-------------------------------------|------------------------------------------------|-----------------------|
| POST   | `/api/notes`                        | Create a new note                              | NoteCreateDto         |
| GET    | `/api/notes`                        | List notes (optional tag filter + pagination)  | — (query params)      |
| GET    | `/api/notes/{id}`                   | Get single note by ID                          | —                     |
| PUT    | `/api/notes/{id}`                   | Update note (partial update supported)         | NoteUpdateDto         |
| DELETE | `/api/notes/{id}`                   | Delete note                                    | —                     |
| GET    | `/api/notes/{id}/word-stats`        | Get word frequency statistics for the note     | —                     |

**Example DTOs:**


```json
{
  "title": "Weekly plan",
  "text": "Weekly plan text",
  "tags": ["IMPORTANT", "PERSONAL"]
}
```

Quick Start (Docker)

# 1. Clone the repository
- git clone <https://github.com/MattIRio/test_project
- cd test_project

# 2. Start the application + MongoDB
- docker compose up --build


## Test Coverage

Automated tests have been implemented using **Spring Boot Test** and **MockMvc** to verify all functional requirements:

### A. Creating Notes
- Test creating a note with valid title, text, and tags  
- Test creating a note without a title → returns **400 Bad Request**  
- Test creating a note without text → returns **400 Bad Request**  
- Test creating a note with invalid tag → returns **400 Bad Request** (handled by global exception handler)  

### B. Updating Notes
- Test updating an existing note's title, text, and tags successfully  
- Test updating a non-existent note → returns **404 Not Found**  

### C. Listing Notes
- Test listing all notes  
- Test listing notes filtered by tags  
- Test pagination and sorting (newest notes first)  

### D. Getting Note by ID
- Test retrieving a note by its ID successfully  
- Test retrieving a non-existent note → returns **404 Not Found**  

### E. Deleting Notes
- Test deleting an existing note successfully  
- Test deleting a non-existent note → returns **404 Not Found**  

### F. Word Statistics
- Test calculating word frequency for a note  
- Test calculating word frequency for empty text → returns empty map  

