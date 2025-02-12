# kata-bank-account

This is a Spring Boot application for managing bank accounts. It provides RESTful APIs for creating accounts, retrieving account details, performing deposits and withdrawals, and generating account statements. The application is documented using OpenAPI (Swagger) for easy API exploration and testing.

## Features

- **Create Account**: Initialize a new bank account with zero balance.
- **Retrieve Account**: Get details of an account by its ID.
- **Deposit Funds**: Add funds to an account.
- **Withdraw Funds**: Withdraw funds from an account.
- **Account Statement**: Retrieve a statement of account operations up to a specific date-time.

## Technologies Used

- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Web**
- **Hibernate Validator**
- **Springdoc OpenAPI**
- **JUnit 5** for testing
- **Mockito** for mocking in tests

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ericpauwawe/kata-bank-account.git
   cd bank-account

2. Build the project:
   ```bash
   mvn clean install

3. Run the application:
   ```bash
   mvn spring-boot:run
   
 
4. Accessing the API
Once the application is running, you can access the API documentation at:
http://localhost:9090/swagger-ui.html


5. Running Tests
To run the test suite, execute:
   ```bash
   mvn test
