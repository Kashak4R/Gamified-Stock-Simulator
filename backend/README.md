# Backend Setup and Installation Guide

## Overview
This is a Spring Boot-based REST API backend for the Gamified Stock Market Simulator. It handles all trading operations, user management, and data persistence.

## Technology Stack
- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Build Tool**: Maven 3.6+
- **Dependencies**: Spring Data JPA, Spring Web, Spring Security, JWT

## Project Structure

```
backend/
├── pom.xml                          # Maven configuration and dependencies
├── src/
│   ├── main/
│   │   ├── java/com/gamified/stocksimulator/
│   │   │   ├── StockSimulatorApplication.java    # Main application entry point
│   │   │   ├── model/
│   │   │   │   ├── User.java                     # User entity (database model)
│   │   │   │   ├── Portfolio.java                # Stock holdings entity
│   │   │   │   └── Trade.java                    # Trade transaction entity
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java           # User database access
│   │   │   │   ├── PortfolioRepository.java      # Portfolio database access
│   │   │   │   └── TradeRepository.java          # Trade database access
│   │   │   ├── service/
│   │   │   │   ├── UserService.java              # User business logic
│   │   │   │   ├── PortfolioService.java         # Portfolio business logic
│   │   │   │   └── TradeService.java             # Trading business logic
│   │   │   └── controller/
│   │   │       ├── UserController.java           # User API endpoints
│   │   │       ├── PortfolioController.java      # Portfolio API endpoints
│   │   │       └── TradeController.java          # Trading API endpoints
│   │   └── resources/
│   │       └── application.properties            # Application configuration
│   └── test/
│       └── java/                                 # Unit and integration tests (coming soon)
└── README.md                        # This file
```

## Prerequisites

Before setting up the backend, ensure you have:

1. **Java Development Kit (JDK) 17 or later**
   - Download from: https://adoptopenjdk.net/ or https://www.oracle.com/java/technologies/javase-jdk17-downloads.html
   - Verify: `java -version`

2. **Maven 3.6 or later** (for building the project)
   - Download from: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **MySQL 8.0 or later**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Verify: `mysql --version`

## Setup Instructions

### Step 1: Create the Database

1. Start MySQL:
   ```bash
   mysql -u root -p
   ```

2. Run the SQL script to create tables:
   ```bash
   source database/schema.sql
   ```

   Or manually in MySQL command line:
   ```sql
   DROP DATABASE IF EXISTS stock_simulator;
   CREATE DATABASE stock_simulator;
   USE stock_simulator;
   -- Then run the SQL from database/schema.sql
   ```

3. Verify tables were created:
   ```sql
   SHOW TABLES;
   ```

### Step 2: Update Database Configuration

Edit `src/main/resources/application.properties`:

```properties
# Change these to match your MySQL setup
spring.datasource.url=jdbc:mysql://localhost:3306/stock_simulator
spring.datasource.username=root
spring.datasource.password=your_mysql_password_here
```

### Step 3: Build the Project

Navigate to the backend directory and build:

```bash
cd backend
mvn clean install
```

This command:
- `clean`: Removes previous builds
- `install`: Compiles code, runs tests, and packages the application

### Step 4: Run the Application

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

Or using the JAR file:

```bash
java -jar target/stock-simulator-1.0.0.jar
```

Expected output:
```
Started StockSimulatorApplication in X seconds
```

The application is now running on: `http://localhost:8080`

## API Endpoints

### User Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register a new user |
| GET | `/api/users/{userId}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users/{userId}/balance` | Get user's current balance |
| GET | `/api/users/{userId}/profitloss` | Calculate user's profit/loss |

### Trading Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/trades/buy` | Execute a buy trade |
| POST | `/api/trades/sell` | Execute a sell trade |
| GET | `/api/trades/user/{userId}` | Get user's trade history |
| GET | `/api/trades/user/{userId}/stock/{symbol}` | Get trades for specific stock |

### Portfolio Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/portfolio/user/{userId}` | Get user's complete portfolio |
| GET | `/api/portfolio/user/{userId}/stock/{symbol}` | Get specific stock holding |
| GET | `/api/portfolio/user/{userId}/stats` | Get portfolio statistics |
| GET | `/api/portfolio/user/{userId}/owns/{symbol}` | Check if user owns stock |

## Example API Requests

### Register a User

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "SecurePassword123",
    "initialBalance": 10000.00
  }'
```

### Buy a Stock

```bash
curl -X POST http://localhost:8080/api/trades/buy \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "symbol": "AAPL",
    "quantity": 10,
    "price": 150.25
  }'
```

### Get Portfolio

```bash
curl -X GET http://localhost:8080/api/portfolio/user/1
```

## Troubleshooting

### Issue: "Connection refused" when connecting to MySQL
- **Solution**: Ensure MySQL is running. Start it with: `mysql.server start` (Mac) or `mysql start` (Windows Service)

### Issue: "Access denied for user 'root'"
- **Solution**: Check your MySQL password in `application.properties` matches your actual password

### Issue: "Unknown database 'stock_simulator'"
- **Solution**: Run the database setup script first: `source database/schema.sql`

### Issue: "Port 8080 already in use"
- **Solution**: Change the port in `application.properties`: `server.port=8081`

## Documentation Standards

All code follows these documentation requirements (Rule 4):
- Every class has a JavaDoc header explaining its purpose
- Every method has clear comments explaining what it does
- Complex logic has inline comments for beginners
- Comments are written in simple, easy-to-understand language

## Running Tests (Coming Soon)

```bash
mvn test
```

## Building for Production

Create an executable JAR:

```bash
mvn clean package -DskipTests
```

The JAR file will be in: `target/stock-simulator-1.0.0.jar`

## Next Steps

1. Set up the frontend (React/Vue.js with HTML/CSS/JavaScript)
2. Implement WebSocket for real-time price updates
3. Add authentication and JWT token handling
4. Connect to real market data APIs for live stock prices
5. Implement comprehensive unit and integration tests

## Support

For issues or questions, refer to the project's GitHub issues or contact the development team.

---

*Last Updated: March 2026*
*Technology Stack: Spring Boot 3.1.5 with Java 17*
