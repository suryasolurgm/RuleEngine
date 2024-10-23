Rule Engine with Abstract Syntax Tree (AST)
A Java-based rule engine application that uses Abstract Syntax Trees to evaluate complex conditional rules for user eligibility based on various attributes.
Overview
This project implements a 3-tier rule engine application that consists of:

Simple UI for rule management
REST API for rule operations
Backend processing with AST implementation
MySQL database for rule storage

Features

Create and store complex conditional rules using AST
Combine multiple rules into a single optimized AST
Evaluate user data against stored rules
Dynamic rule modification and management
Support for various attributes (age, department, income, etc.)

Technical Architecture
Data Structure
The AST is represented using a Node class:
javaCopypublic class Node {
    private String type;      // "operator" or "operand"
    private Node left;        // Left child node
    private Node right;       // Right child node
    private String value;     // Value for operand nodes
    
    // Constructors, getters, and setters
}
Database Schema
sqlCopyCREATE TABLE rules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    rule_name VARCHAR(255) NOT NULL,
    ast_json JSON NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
Core API Endpoints

POST /api/rules/create

Creates a new rule from a rule string
Returns the AST representation


POST /api/rules/combine

Combines multiple rules into a single optimized AST
Minimizes redundant checks


POST /api/rules/evaluate

Evaluates user data against a rule AST
Returns eligibility result



Sample Rules
CopyRule 1: "((age > 30 AND department = 'Sales') OR 
         (age < 25 AND department = 'Marketing')) AND 
         (salary > 50000 OR experience > 5)"

Rule 2: "((age > 30 AND department = 'Marketing')) AND 
         (salary > 20000 OR experience > 5)"
Prerequisites

Java 17
Spring Boot
Maven
MySQL

Installation

Clone the repository:

bashCopygit clone https://github.com/suryasolurgm/rule-engine-ast.git

Navigate to project directory:

bashCopycd rule-engine-ast

Configure database connection in application.properties:

propertiesCopyspring.datasource.url=jdbc:mysql://localhost:3306/rule_engine
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
Running the Application

Build the project:

bashCopymvn clean install

Start the application:

bashCopymvn spring-boot:run
Testing
Run the test suite:
bashCopymvn test
Test Cases Include:

Individual rule creation and AST validation
Rule combination and optimization
Rule evaluation with sample data
Error handling and edge cases

Advanced Features
Error Handling

Validation for rule syntax
Data format verification
Proper error messages and status codes

Attribute Validation

Catalog-based attribute verification
Type checking and validation

Rule Modification

Update existing rules
Modify operators and operands
Add/remove sub-expressions
