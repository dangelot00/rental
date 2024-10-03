# Film Rental Exercise

Spring Boot application exercise developed with Java 17, designed to manage film rentals with a set of specific business rules. It uses an in-memory H2 database and Hibernate for data persistence. Authentication is handled using JWT tokens, and the API is documented with OpenAPI (Swagger UI).

## Table of Contents
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
    - [Authentication Endpoints](#authentication-endpoints)
    - [Film Endpoints](#film-endpoints)
    - [Rental Endpoints](#rental-endpoints)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Additional Info](#additional-information)
- [Important Links Recap](#Important-links-recap)

## Features
- **User Authentication**: Secure login and registration using JWT tokens.
- **Film Management**: CRUD operations for films, including different genres.
- **Rental Management**: Rent and return films with business-specific constraints.
- **Pricing Rules**:
    - Standard Rental: 5 francs per day plus a 3 franc deposit fee.
    - Last Exit Rental: 7 francs per day plus a 4 franc fee.
    - Children's Films: 10 francs per week with a 1 franc deposit.
    - **Deposit Waiver**: From the third rental onward, deposit is waived if no films have been missed.
- **Late Return Penalties**:
    - Additional 2 francs per day's delay.
    - Loss of deposit for delayed returns.
- **High Performance**: Optimized for speed and efficiency.
- **In-Memory Database**: Uses H2 for quick data access during development.
- **OpenAPI Documentation**: Interactive API docs with Swagger UI.
- **Unit and Integration Tests**: Comprehensive test coverage using JUnit and Mockito.

## Technologies Used
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Hibernate
- H2 Database
- MapStruct: For mapping entities to DTOs.
- JUnit 5 & Mockito: For testing.
- OpenAPI (Swagger UI): For API documentation.
- Lombok: Reduces boilerplate code.
- JWT (JSON Web Tokens): For stateless authentication.

## Getting Started

### Prerequisites
- Java 17 or higher installed.
- Maven >= 3.8.x installed.
- An IDE like IntelliJ IDEA (Preferred) or Eclipse.

### Installation

Clone the Repository:
```bash
git clone https://github.com/dangelot00/rental.git
cd rental
```

### Running the Application
You can run the application in several ways:

#### From Your IDE (Preferred)
- Import the project as a Maven project.
- Run the class in org.blockbuster.rental.RentalApplication.

#### Using Maven
```bash
mvn spring-boot:run
```

#### Using the Executable JAR
```bash
java -jar ./target/rental-0.0.1-SNAPSHOT.jar
```

## API Documentation

The application uses OpenAPI with Swagger UI for interactive API documentation.

Access Swagger UI: ```http://localhost:8080/swagger-ui.html```

### Authentication Endpoints

#### Register a New User
- **Endpoint**: ```POST /auth/signup```
- **Description**: Registers a new user.
- **Request Body**:
  ```json
  {
    "username": "test",
    "password": "123"
  }
  ```
- **Response**: ```200 OK``` with user details.

#### Login
- **Endpoint**: ```POST /auth/login```
- **Description**: Authenticates a user and returns a JWT token.
- **Request Body**:
  ```json
  {
    "username": "test",
    "password": "123"
  }
  ```
- **Response**: ```200 OK``` with JWT token.

### Film Endpoints

#### Get All Films
- **Endpoint**: ```GET /films```
- **Description**: Retrieves a list of all films.
- **Response**: ```200 OK``` with an array of film details.

#### Get Film by ID
- **Endpoint**: ```GET /films/{title}```
- **Description**: Retrieves details of a specific film.
- **Response**: ```200 OK``` with film details.

### Rental Endpoints

#### Rent a Film
- **Endpoint**: ```POST /rentals/rent```
- **Description**: Rents a film (requires authentication).
- **Request Parameters**:
    - ```filmTitle```: Title of the film to rent.
    - ```durationInDays```: Number of days to rent.
- **Response**: ```200 OK``` with rental details.

#### Return a Film
- **Endpoint**: ```POST /rentals/return```
- **Description**: Returns a rented film (requires authentication).
- **Request Parameters**:
    - ```rentalId```: ID of the rental to return.
- **Response**: ```200 OK``` with confirmation message.

#### Get User's Rentals
- **Endpoint**: ```GET /rentals/my-rentals```
- **Description**: Retrieves rentals of the authenticated user.
- **Response**: ```200 OK``` with an array of rental details.

## Testing

The project includes a mini suite of unit tests, only regarding the most critical methods.

### Running Tests

#### From Command Line:
```bash
mvn test
```

#### From Your IDE (Preferred):
- Right-click on the test directory and select **Run 'All Tests'**.

## Project Structure
```plaintext
src
├── main
│   ├── java
│   │   └── org.blockbuster.rental
│   │       ├── config
│   │       │   ├── jwt
│   │       │   │   ├── JwtAuthenticationFilter.java
│   │       │   │   └── JwtTokenProvider.java
│   │       │   ├── ApplicationConfiguration.java
│   │       │   ├── OpenApiConfig.java
│   │       │   └── SecurityConfig.java
│   │       ├── controller
│   │       │   ├── AuthenticationController.java
│   │       │   ├── FilmController.java
│   │       │   ├── RentalController.java
│   │       │   └── UserController.java
│   │       ├── entity
│   │       │   ├── Film.java
│   │       │   ├── Rental.java
│   │       │   └── User.java
│   │       ├── enums
│   │       │   └── FilmGenre.java
│   │       ├── exception
│   │       │   ├── handler
│   │       │   │   └── GlobalExceptionHandler.java
│   │       │   ├── CalculationCostException.java
│   │       │   ├── FilmNotFoundException.java
│   │       │   ├── NotEnoughBalanceException.java
│   │       │   ├── RentalAlreadyReturnedException.java
│   │       │   ├── RentalNotFoundException.java
│   │       │   └── UserNotFoundException.java
│   │       ├── impl
│   │       │   ├── FilmServiceImpl.java
│   │       │   ├── RentalServiceImpl.java
│   │       │   └── UserServiceImpl.java
│   │       ├── mapper
│   │       │   ├── FilmMapper.java
│   │       │   └── RentalMapper.java
│   │       ├── repository
│   │       │   ├── FilmRepository.java
│   │       │   ├── RentalRepository.java
│   │       │   └── UserRepository.java
│   │       ├── service
│   │       │   ├── AuthenticationService.java
│   │       │   ├── FilmService.java
│   │       │   ├── RentalService.java
│   │       │   └── UserService.java
│   │       ├── web
│   │       │   ├── response
│   │       │   │   ├── ErrorResponse.java
│   │       │   │   └── LoginResponse.java
│   │       │   ├── FilmDTO.java
│   │       │   ├── LoginUserDTO.java
│   │       │   ├── RegisterUserDTO.java
│   │       │   └── RentalDTO.java
│   │       └── FilmRentalApplication.java
│   └── resources
│       ├── application.properties
│       ├── data.sql
│       └── schema.sql
└── test
    └── java
        └── org.blockbuster.rental
            └── impl
                └── RentalServiceImplTest.java
```

### Additional Information

#### Accessing the H2 Database Console
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: ```jdbc:h2:mem:testdb```
- **User Name**: ```sa```
- **Password**: (leave blank)

#### Configuration Settings

Application Properties: Located in ```src/main/resources/application.properties```.

### Important Links Recap
- **Application URL**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **API Documentation**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **API Documentation (Swagger UI)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

