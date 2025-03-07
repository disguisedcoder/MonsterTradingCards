# Semesterprojekt - Monster Trading Card Game (MTCG)

---

## Introduction

This project implements a **Monster Trading Card Game (MTCG)** with an HTTP server. The game includes features such as user registration, login, and trading card functionalities. It is built using Java and is structured to follow a layered architecture for scalability and maintainability.

---

## Github

```bash
git clone https://github.com/disguisedcoder/MonsterTradingCards
```
## Requirements
- Java: 21
- Docker

## How to run the database
```yaml
docker compose up -d
```

### To create table
Run the Script.sql in the folder sql on the database

## Project Structure

The project is divided into various layers to maintain a clean separation of concerns. Below is a detailed description of each package and its classes:

```plaintext
│   Main.java
│
├───application
│   │   MonsterTradingCard.java
│   │
│   ├───controller
│   │       BattleController.java
│   │       CardController.java
│   │       Controller.java
│   │       PackageController.java
│   │       ScoreboardController.java
│   │       StatsController.java
│   │       TradingController.java
│   │       UserController.java
│   │
│   ├───data
│   │       BattleRepository.java
│   │       CardRepository.java
│   │       PackageRepository.java
│   │       ScoreboardRepository.java
│   │       StatsRepository.java
│   │       TradingRepository.java
│   │       UserRepository.java
│   │
│   ├───dto
│   │       UserCredentials.java
│   │       UserData.java
│   │
│   ├───entity
│   │       Card.java
│   │       CreatureType.java
│   │       Stats.java
│   │       Trading.java
│   │       User.java
│   │
│   ├───exception
│   │       AlreadyExistsException.java
│   │       AuthenticationFailedException.java
│   │       ControllerNotFoundException.java
│   │       DeckConflictException.java
│   │       InternalServerError.java
│   │       InvalidBodyException.java
│   │       JsonParserException.java
│   │       NotEnoughCoinsException.java
│   │       NotFoundException.java
│   │       NoTradingsException.java
│   │       PackageConflictException.java
│   │       TradeInvalidException.java
│   │       UnauthorizedException.java
│   │
│   ├───routing
│   │       Route.java
│   │       Router.java
│   │
│   ├───service
│   │       BattleService.java
│   │       CardService.java
│   │       PackageService.java
│   │       ScoreboardService.java
│   │       StatsService.java
│   │       TradingService.java
│   │       UserService.java
│   │
│   └───util
│           PostgresConfig.java
│           TokenParser.java
│
└───server
    │   Application.java
    │   RequestHandler.java
    │   Server.java
    │
    ├───http
    │       Method.java
    │       Request.java
    │       Response.java
    │       Status.java
    │
    └───util
            HttpRequestParser.java
            HttpResponseFormatter.java
            HttpSocket.java
            NoHttpStatusException.java
```

## UML

Below is a high-level UML diagram illustrating the core components and their relationships:
 
![UML Diagram](./Semesterprojekt.jpg)

You can also find the PlantUML file (`Semesterprojekt.puml`) in this project folder.


---

### Layer Logic

* **Controller Layer:**
    - Handles all incoming HTTP requests and outgoing responses.
    - Maps request URLs and methods to appropriate controller actions using the routing mechanism.
    - Validates request data and transforms it into a format understood by the service layer.
    - Centralizes error handling and response formatting for consistency.

* **Service Layer:**
    - Contains the core business logic and game rules (e.g., battle mechanics, card management, trading rules).
    - Acts as a mediator between controllers and data repositories, ensuring that business rules are applied before data is persisted or retrieved.
    - Maintains stateful operations like battle simulations, score adjustments, and user authentication.
    - Isolates business logic from the specifics of the data access layer, which enhances testability and maintainability.

* **Repository/Data Layer:**
    - Manages all data access and persistence operations.
    - Provides abstract interfaces for accessing data, allowing for the storage solution to be swapped or modified with minimal impact on the rest of the application.
    - Ensures data integrity and consistency through CRUD operations on entities such as users, cards, and stats.

* **DTOs (Data Transfer Objects):**
    - Serve as intermediaries between the service layer and the presentation layer (or external clients).
    - Allow the application to decouple its internal data models from the representations used in communication (e.g., JSON responses), improving security and flexibility.

* **Utility Layer:**
    - Contains helper classes for common functionalities such as JSON parsing, token parsing, HTTP request/response formatting, and database configuration.
    - Promotes code reuse and helps maintain consistency across various parts of the application.

* **Routing Layer:**
    - Consists of classes like `Route` and `Router` that define how URLs are mapped to controllers.
    - Simplifies adding new endpoints and maintaining a clean separation between different areas of functionality.

* **Server Layer:**
    - Manages the underlying HTTP server, including socket connections and request handling.
    - Provides a foundation upon which the higher layers (controllers, services) operate.

* **Separation of Concerns and Modularity:**
    - Each layer has a well-defined responsibility, ensuring that changes in one layer (e.g., switching the database) have minimal impact on others.
    - This modular design supports scalability, making it easier to add new features, update existing functionalities, or integrate third-party services.

* **Testing and Maintainability:**
    - The layered architecture allows for isolated unit testing of each component (e.g., service tests, repository tests, controller tests), which improves overall code quality and eases debugging.
    - Clear boundaries between layers help developers quickly identify and fix issues without needing to understand the entire system.

* **Extensibility:**
    - New features, such as additional battle mechanics or enhanced trading functionalities, can be added by extending the corresponding service, controller, or repository components without overhauling the entire application.

---

## Unique Feature
If the battle ends with a draw after 100 rounds, there will be a random chance that the players one extra round. Each player get a random number between 1 and 100. The player who has the higher number will be determined as the winner.

---

## Unit Tests

### BattleServiceTest
**Reasons:**
- **Core Battle Mechanics:** Verifies that battle rounds are executed correctly, damage is calculated as expected, and that tie-breaker logic (including random decision and re-rolls) is applied properly after 100 rounds.
- **Concurrency and Queue Handling:** Ensures that players are correctly enqueued and matched, and that the battle result is consistently returned to both players.
- **Statistical Updates:** Checks that wins, losses, draws, ELO adjustments, and games played are correctly updated based on the battle outcome.

---

### CardServiceTest
**Reasons:**
- **Card Management:** Confirms that cards are properly saved, retrieved, and converted into DTOs.
- **Deck Retrieval:** Verifies that fetching a user’s deck via token works correctly and that the conversion to CardDTO maintains all expected card attributes.
- **Robust Error Handling:** Ensures that invalid inputs (such as a null or empty card list or an invalid token) result in appropriate exceptions.

---

### DeckServiceTest
**Reasons:**
- **Deck Configuration:** Validates that the service correctly clears an existing deck when needed and adds new cards when a deck is absent.
- **Data Extraction:** Checks that the method for extracting card IDs from CardDTO objects returns the correct list of IDs.
- **Repository Integration:** Ensures proper interactions with the DeckRepository based on whether the user already has a deck.

---

### PackageServiceTest
**Reasons:**
- **Package Creation Rules:** Ensures that creating a package enforces the rule of exactly five cards, throwing exceptions for null inputs or incorrect card counts.
- **Package Acquisition Flow:** Verifies that acquiring a package correctly checks for user existence, sufficient coins, and package availability, then updates the user's card stack.
- **Error Handling:** Confirms that errors such as a missing user or no available packages are properly managed and reported.

---

### ScoreboardServiceTest
**Reasons:**
- **Scoreboard Retrieval:** Tests that the service retrieves and returns a complete and correctly sorted list of scoreboard entries.
- **Empty Scoreboard Handling:** Ensures that the service gracefully handles and returns an empty list when no scoreboard data is present.

---

### StatsServiceTest
**Reasons:**
- **Statistics Retrieval:** Validates that user statistics (wins, losses, draws, games played, and ELO) are accurately retrieved when provided with a valid token.
- **Error Scenarios:** Checks that the service throws appropriate exceptions (e.g., NullPointerException) when an invalid token results in a missing user.

---

### UserServiceTest
**Reasons:**
- **User Creation and Registration:** Verifies that new users are created successfully and that duplicate usernames trigger exceptions.
- **Authentication and Token Validation:** Ensures that valid credentials return the expected authentication token, while invalid credentials or missing tokens result in proper exceptions.
- **User Data Consistency:** Confirms that user data (e.g., name, bio, image) is accurately updated and retrieved, and that access controls prevent unauthorized modifications.

---

## Tracked Time
For the project, I initially planned 12 hours of active working time, but due complications, it ended up taking 20 hours.

---

## Lessons Learned
- **Enhanced Task Delegation:** Future projects can benefit from a more balanced distribution of tasks, which will improve overall productivity and minimize bottlenecks.
- **Stronger Architectural Planning:** Investing more time upfront to design the system architecture can prevent significant rework and reduce delays during implementation.
- **Utilize Authoritative Sources:** Relying on official documentation and trusted resources has proven to be more effective than depending solely on AI-generated insights.
- **Effective Time Management:** Anticipating potential obstacles and allocating extra time for unforeseen challenges is crucial to meet deadlines without sacrificing quality.
- **Commitment to Continuous Learning:** Expanding expertise in tools and frameworks not only accelerates development but also reduces dependency on external help.


## Author
Jansen Wu
