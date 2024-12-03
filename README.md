# NewLoginSpring

NewLoginSpring is a login system developed with Spring Boot, which includes functionalities for account creation, login, password recovery, and support email sending.

## Technologies Used

- Java 17
- Spring Boot 3.4.0
- Spring Data JPA
- Spring Security
- Spring Boot Starter Mail
- PostgreSQL

## Project Setup

### Prerequisites

- Java 17
- Maven
- PostgreSQL

### Database Configuration

Ensure PostgreSQL is installed and running. Update the database configurations in the `src/main/resources/application.properties` file as needed.

### Running the Project

1. Clone the repository:
   ```sh
   git clone https://github.com/luanferreiradev/NewLoginSpring.git
    ```
2. Navigate to the project directory:
    ```sh
    cd NewLoginSpring
    ```
3. Run the project using Maven:
    ```sh
    mvn spring-boot:run
    ```

## Endpoints

### Account Creation

- **URL:** `/api/account/create`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
        "nome": "John Doe",
        "email": "johndoe@example.com",
        "senha": "password123",
        "ativo": true
    }
    ```
### Login

- **URL:** `/api/login`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
        "email": "User's email",
        "senha": "User's password"
    }
    ```
### Password Recovery

- **URL:** `/api/account/recover`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
        "email": "User's email"
    }
    ```

### Password Reset

- **URL:** `/api/account/reset`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
        "email": "User's email",
        "senha": "New password"
    }
    ```


### Support Email

- **URL:** `/api/account/support`
- **Method:** `POST`
- **Request Body:**
    ```json
    {
        "email": "User's email",
        "assunto": "Email subject",
        "mensagem": "Email message"
    }
    ```

## License

Distributed under the MIT License. See [LICENSE](/LICENSE) for more information.

## Author

- [Luan Ferreira](https://github.com/luanferreiradev)
  