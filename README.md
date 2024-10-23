# Rule Engine with AST

This project is a 3-tier Rule Engine application that determines user eligibility based on attributes like age, department, income, spend, etc. It uses an Abstract Syntax Tree (AST) to represent and evaluate dynamic conditional rules.

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven
- MySQL

## Setting Up the Database

1. **Install MySQL**: If you don't have MySQL installed, download and install it from [MySQL Downloads](https://dev.mysql.com/downloads/).

2. **Create Database**: Open MySQL command line or a MySQL client and run the following commands to create a new database:

    ```sql
    CREATE DATABASE rule_engine;
    ```

3. **Create User**: Create a new MySQL user and grant privileges to the database:

    ```sql
    CREATE USER 'rule_user'@'localhost' IDENTIFIED BY 'password';
    GRANT ALL PRIVILEGES ON rule_engine.* TO 'rule_user'@'localhost';
    FLUSH PRIVILEGES;
    ```

4. **Create Table**: Use the following schema to create the `rules` table in the database:

    ```sql
    CREATE TABLE rules (
        id BIGINT NOT NULL AUTO_INCREMENT,
        rule_name VARCHAR(255) NOT NULL,
        ast_json JSON NOT NULL,
        PRIMARY KEY (id)
    ) ENGINE=InnoDB;
    ```

## Configuring the Backend

1. **Clone the Repository**: Clone this repository to your local machine.

    ```bash
    git clone https://github.com/suryasolurgm/rule-engine-ast.git
    cd rule-engine-ast
    ```

2. **Configure Application Properties**: Open `src/main/resources/application.properties` and update the MySQL database connection details:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/rule_engine
    spring.datasource.username=rule_user
    spring.datasource.password=password
    spring.jpa.hibernate.ddl-auto=update
    ```

## Running the Backend

1. **Build the Project**: Use Maven to build the project.

    ```bash
    mvn clean install
    ```

2. **Run the Application**: Start the Spring Boot application.

    ```bash
    mvn spring-boot:run
    ```

    The backend will be running at `http://localhost:8080`.

## API Endpoints

- **POST /api/rules/create**: Create a new rule and store its AST representation.
- **POST /api/rules/combine**: Combine multiple rules into a single AST.
- **POST /api/rules/evaluate**: Evaluate a rule against the provided user data.

## Sample Rules

1. **Rule 1**:
