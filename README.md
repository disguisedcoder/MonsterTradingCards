# Semesterprojekt - Monster Trading Card Game (MTCG)

## Table of Contents
- [Introduction](#introduction)
- [Project Structure](#project-structure)
- [UML](#uml)
- [Error Handling](#error-handling)
- [Author](#author)

---

## Introduction

This project implements a **Monster Trading Card Game (MTCG)** with an HTTP server. The game includes features such as user registration, login, and trading card functionalities. It is built using Java and is structured to follow a layered architecture for scalability and maintainability.

---

## Project Structure

The project is divided into various layers to maintain a clean separation of concerns. Below is a detailed description of each package and its classes:

```plaintext
src/
└── main/
    ├── java/
    │   ├── at.technikum
    │   │   ├── application/
    │   │   │   ├── echo/                
    │   │   │   │   └── EchoApplication   # Example echo application
    │   │   │   ├── html/
    │   │   │   │   └── SimpleHtmlApplication  # Example HTML application
    │   │   │   ├── TradingCards/        # Core trading card application
    │   │   │   │   ├── controller/
    │   │   │   │   │   ├── Controller          # Base controller class
    │   │   │   │   │   └── UserController      # Handles user registration and login
    │   │   │   │   ├── DTO/
    │   │   │   │   │   └── UserDTO             # Data Transfer Object for User
    │   │   │   │   ├── entity/
    │   │   │   │   │   ├── card/
    │   │   │   │   │   │   └── Card            # Represents a card in the game
    │   │   │   │   │   ├── packages/           # Placeholder for card packaging logic
    │   │   │   │   │   └── user/
    │   │   │   │   │       └── User            # Represents a user/player in the game
    │   │   │   │   ├── exception/
    │   │   │   │   │   ├── AuthenticationFailedException  # Thrown on failed login
    │   │   │   │   │   ├── ControllerNotFoundException    # For invalid controllers
    │   │   │   │   │   ├── EntityNotFoundException        # For missing entities
    │   │   │   │   │   ├── InvalidBodyException           # Thrown for malformed JSON
    │   │   │   │   │   ├── JsonParserException            # Thrown on JSON parsing errors
    │   │   │   │   │   ├── UserAlreadyExistsException     # Thrown when username is taken
    │   │   │   │   │   └── UserNotFoundException          # Thrown when a user is not found
    │   │   │   │   ├── repository/
    │   │   │   │   │   └── UserRepository                 # Manages user data storage
    │   │   │   │   ├── service/
    │   │   │   │   │   ├── UserService                    # Contains user-related business logic
    │   │   │   │   │   └── MTCG_Application               # Main trading card game service
    │   │   │   │   ├── router/
    │   │   │   │   │   ├── Route                          # Represents an HTTP route
    │   │   │   │   │   └── Router                         # Maps routes to controllers
    │   │   │   │   ├── server/
    │   │   │   │   │   ├── http/
    │   │   │   │   │   │   ├── Method                     # Enum for HTTP methods (GET, POST, etc.)
    │   │   │   │   │   │   ├── Request                    # Represents an HTTP request
    │   │   │   │   │   │   ├── Response                   # Represents an HTTP response
    │   │   │   │   │   │   └── Status                     # Enum for HTTP status codes
    │   │   │   │   │   └── util/
    │   │   │   │   │       ├── HttpRequestParser          # Parses HTTP requests
    │   │   │   │   │       ├── HttpResponseFormatter      # Formats HTTP responses
    │   │   │   │   │       ├── HttpSocket                 # Handles socket connections
    │   │   │   │   │       ├── NoHttpStatusException      # For undefined HTTP statuses
    │   │   │   │   │       ├── Application                # Main application interface
    │   │   │   │   │       ├── RequestHandler             # Handles routing and processing
    │   │   │   │   │       └── Server                     # Starts the HTTP server
    │   │   │   │   └── Main                               # Entry point for the application
```

---
## UML Definition
class Controller {
+ handle(Request): Response
+ fromBody()
+ json()
}

class UserController {
+ register(Request): Response
+ login(Request): Response
}
Controller <|-- UserController

class UserService {
+ createUser(User): UserDTO
+ login(UserDTO): String
}
UserController --> UserService

class UserRepository {
- users: Map<String, User>
+ save(User)
+ findByUsername(String): User
}
UserService --> UserRepository

class User {
- username: String
- password: String
- token: String
- coins: int
- elo: int
- stack: List<Card>
- deck: List<Card
}
UserService --> User

class UserDTO {
- username: String
- elo: int
- coins: int
}
User --> UserDTO

class Card {
- name: String
- damage: int
}


it is still not finished, but these are my first thoughts. Card have more attributes


---

## Error Handling

The application uses custom exceptions to handle errors gracefully. Below are the main exceptions:

- **`UserAlreadyExistsException`**: Thrown when attempting to register an existing username.
- **`UserNotFoundException`**: Thrown when a user is not found during login.
- **`AuthenticationFailedException`**: Thrown for invalid login credentials.
- **`InvalidBodyException`**: Thrown for malformed request bodies.

---

## Future Features
- Add trading card functionalities.
- Implement deck and stack management.
- Add battle and leaderboard features.

---

## Author
[Your Name or Team Name]
