
## Run Configurations

### Prerequisites
- Docker and Docker Compose installed on your machine.
- `.env` file configured with the necessary environment variables.
- Java Development Kit (JDK) installed.
- Maven installed for building the project.
- **Ensure that nothing is running on port `8080`.**
- **If you are running the local PostgreSQL database. Ensure that port `5432` is free..**

### Run Configurations Overview

#### 1. **Compose Local**
- **Description**: Uses the `compose.local.yaml` file to spin up a local PostgreSQL database and the backend application.
- **Use Case**: Ideal for developing new features where breaking changes might affect the database. This setup ensures you don't interfere with the shared Aiven database.
- **How to Use**:
  - **From Terminal**:
    1. Navigate to the `docker/local` directory.
    2. Run `docker-compose -f compose.local.yaml up`.
    3. Access the backend at `http://localhost:8080`.
  - **From IDE**:
    1. Use the `Compose Local` run configuration available in the IDE.
    2. Start the configuration to spin up the services.

#### 2. **Compose Aiven**
- **Description**: Uses the `compose.aiven.yaml` file to run the backend application connected to the Aiven-managed PostgreSQL database.
- **Use Case**: Use this configuration if you are not making changes to the database schema or need to test against the shared Aiven database.
- **How to Use**:
  - **From Terminal**:
    1. Navigate to the `docker/aiven` directory.
    2. Run `docker-compose -f compose.aiven.yaml up`.
    3. Access the backend at `http://localhost:8080`.
  - **From IDE**:
    1. Use the `Compose Aiven` run configuration available in the IDE.
    2. Start the configuration to spin up the services.

#### 3. **ReMarket Local**
- **Description**: Runs the Spring Boot application locally with the `docker-postgres` profile, connecting to the local PostgreSQL database.
- **Use Case**: Use this when developing locally and testing against the local database.
- **How to Use**:
  1. Open the `ReMarket Local` run configuration in your IDE.
  2. Start the application.

#### 4. **ReMarket Aiven**
- **Description**: Runs the Spring Boot application locally with the `aiven-postgres` profile, connecting to the Aiven-managed PostgreSQL database.
- **Use Case**: Use this when testing the application against the shared Aiven database.
- **How to Use**:
  1. Open the `ReMarket Aiven` run configuration in your IDE.
  2. Start the application.

#### 5. **Maven [clean,install]**
- **Description**: Builds the project and installs the artifacts to the local Maven repository.
- **Use Case**: Use this to ensure the project builds successfully and dependencies are resolved.
- **How to Use**:
  1. Run the `Maven [clean,install]` configuration in your IDE or execute `mvn clean install` in the terminal.

#### 6. **Maven [clean,package]**
- **Description**: Builds the project and packages it into a JAR file.
- **Use Case**: Use this to create a deployable JAR file for the application.
- **How to Use**:
  1. Run the `Maven [clean,package]` configuration in your IDE or execute `mvn clean package` in the terminal.

#### 7. **Maven [test]**
- **Description**: Runs the test suite for the application.
- **Use Case**: Use this to verify that all tests pass successfully.
- **How to Use**:
  1. Run the `Maven [test]` configuration in your IDE or execute `mvn test` in the terminal.

### Recommendations
- **Not Developing Backend**: Use the Compose files (`compose.local.yaml` or `compose.aiven.yaml`) depending on your use case. You can run them either from the terminal or directly from the IDE using the provided run configurations.
- **Developing New Features**: Use the local configuration (`Compose Local` or `ReMarket Local`) to avoid breaking the shared Aiven database.
