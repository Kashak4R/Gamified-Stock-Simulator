# Gamified Stock Market Simulator - Development Rules

## Project Overview
This is a Gamified Stock Market Simulator that allows users to practice and learn stock trading in a competitive, game-like environment.

---

## Technology Stack Rules

### 1. Frontend
- **HTML** - For markup and structure
- **CSS** - For styling and responsive design
- **JavaScript** - For client-side logic and interactivity
- **Framework/Libraries**: Optional (e.g., React, Vue, Vanilla JS)
- **Location**: `/frontend` directory

### 2. Backend
- **Java** - All trading logic, business logic, and server-side operations
- **Framework**: Spring Boot (recommended for REST APIs)
- **Location**: `/backend` directory

### 3. Database
- **MySQL** - For all data persistence
- **Location**: `/database` directory (SQL scripts and schema)

---

## Strict Rules

### ✅ ALLOWED Technologies
- HTML, CSS, JavaScript (Frontend)
- Java (Backend)
- MySQL (Database)
- Related tools: Maven/Gradle (build), Spring Boot (framework)

### ❌ NOT ALLOWED
- Any other programming languages for core components
- Alternative databases (MongoDB, PostgreSQL, etc.)
- Frontend frameworks outside HTML/CSS/JS ecosystem
- No Python, Node.js, PHP, C#, or other backend languages

---

## Code Organization Guidelines

```
Gamified Stock simulator/
├── frontend/
│   ├── index.html
│   ├── css/
│   │   └── styles.css
│   ├── js/
│   │   └── app.js
│   └── assets/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/stocksimulator/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── README.md
├── database/
│   ├── schema.sql
│   ├── seed-data.sql
│   └── migrations/
├── RULES.md
└── README.md
```

---

## Important Development Guidelines

### Rule #5: Permission Required for Stuck Issues
**If you encounter any blockers or get stuck anywhere:**
- ❌ Do NOT attempt to resolve it independently
- ✅ ALWAYS ask for user permission before:
  - Deviating from the tech stack
  - Introducing new tools/libraries
  - Changing architecture decisions
  - Modifying rules

### Code Quality
- Follow Java naming conventions (camelCase for variables/methods, PascalCase for classes)
- Write clean, readable code with proper documentation
- Use appropriate design patterns for trading logic
- Ensure all database operations use proper transaction handling

### Database
- Always use parameterized queries to prevent SQL injection
- Maintain data integrity with proper constraints
- Create backups of schema and migration scripts

### Frontend
- Keep HTML semantic and accessible
- Use vanilla JavaScript or a lightweight framework
- Ensure responsive design for multiple devices
- Separate concerns: HTML structure, CSS styling, JS logic

---

## Violation Policy
Any deviation from this ruleset without explicit user permission is prohibited. If unclear about implementation approach, always ask for clarification.

---

**Last Updated**: March 6, 2026
