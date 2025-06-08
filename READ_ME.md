#Task Average Time Tracker

##1. Description of the Project

This project is a RESTful microservice designed to track the average time it takes to perform a named task. It's built as a production-ready service, considering non-functional requirements like test coverage, scalability, availability, and performance.

The core functionalities of this service are:
•	Record Task Performance: It accepts a task identifier and the duration in milliseconds for a task that has been performed. The service then performs the necessary calculations and data storage.

•	Retrieve Average Time: It provides an endpoint to get the current average time for a specific task, identified by its unique ID, and returns the task identifier and the average duration.

A key feature of the service is its persistence; it is designed to survive restarts. If the service is restarted, all the recorded average times will still be available once it is back up and running.

The application is containerized using Docker and can be deployed using Docker Compose for easy setup and management.

##2. Requirements

This project utilizes the following technologies:

	•	JDK: Version 21 or above
	
	•	Spring Boot: Version 3.3.1
	
	•	Database: MySQL (Version 8.0)
	
	•	Testing: JUnit 5 with Mockito
	
	•	Containerization: Docker
	
	•	Orchestration: Docker Compose
	
	•	Version Control: Git

##3. Installation and Setup

###Prerequisites

	•	Git: You need Git installed to clone the repository.
	•	JDK 21: Make sure you have Java Development Kit version 21 installed.
	•	Maven: Apache Maven is required to build the project and manage dependencies.
	•	Docker Engine: Docker must be installed and running to use the Docker setup.
	
###Getting Started

1.	Clone the repository:

Bash command:

git clone https://github.com/Balajicodebase7/Average-Time-Tracker.git

cd Average-Time-Tracker

2.	Build the project with Maven:
This command will download dependencies and compile the source code.

Bash command:
mvn clean install

##How to Run the Project
You can run the application in two ways: locally using a Dockerized database, or fully containerized using Docker Compose.

###Option 1: Run Locally with Dockerized MySQL

This approach is ideal for development and debugging.

1.	Start the MySQL database container:

Make sure Docker is running, then execute the following command from the root of the project directory. This starts only the database service defined in the docker-compose.yml file.

Bash command:
docker-compose up -d mysql_db

This will start a MySQL container, and the database will be accessible on localhost:3310.

2.	Run the Spring Boot application:

You can run the application from your IDE (like IntelliJ or VSCode) by running the main method in TaskTimeTrackerApplication.java, or by using the following Maven command:

Bash command:
mvn spring-boot:run

The application will connect to the MySQL database running in Docker.

###Option 2: Run Everything with Docker Compose

This is the recommended way to deploy the application as it sets up the entire environment, including the application and the database.

1.	Build and start all services: Make sure Docker is running, then execute the following command from the root of the project directory: 

Bash command:
docker-compose up --build

This command will: 

	o	Build the Docker image for the Spring Boot application using the provided Dockerfile.
	o	Create and start containers for both the application and the MySQL database.
	o	The application will be available on http://localhost:9000.
	
Interacting with the API

You can use any API client like Postman or curl to interact with the service.
•	Record a task performed:
Send a POST request to /tasks/{taskId} with the duration in the request body.
Bash command:
curl -X POST http://localhost:9000/tasks/coding-feature -H "Content-Type: application/json" -d '{"duration": 1200}'

•	Get the current average time:

Send a GET request to /tasks/{taskId}/average.
Bash command:
curl -X GET http://localhost:9000/tasks/coding-feature/average

##4. Testing

The project includes comprehensive unit tests for the service and controller layers to ensure reliability and correctness.
	
	•	Controller Tests: These tests (TaskControllerTest.java) use @WebMvcTest and MockMvc to test the API endpoints, 		ensuring proper request handling, validation, and response generation.
	•	Service Tests: These tests (TaskServiceTest.java) use @ExtendWith(MockitoExtension.class) to unit test the 		business logic within the TaskService, mocking the repository layer to isolate the service logic.
	
To run the tests, execute the following Maven command:
Bash command:
mvn test

##5. Deployment
###Dockerfile

The Dockerfile in the root directory is a multi-stage build file that:
	
	1.	Build Stage: Uses a Maven base image to build the application and create a JAR file.
	2.	Runtime Stage: Copies the JAR file into a slim JRE image (eclipse-temurin:21-jre-jammy) to create a lightweight 		and secure container for running the application.
	
To build the Docker image manually, you can run:
Bash command:
docker build -t your-image-name:tag .

###Docker Compose

The docker-compose.yml file orchestrates the deployment of the application and the MySQL database.
	
	•	Services: It defines two services: app (the Spring Boot application) and mysql_db (the database).
	•	Networking: It sets up a network so the application container can communicate with the database container.
	•	Healthcheck: It includes a health check for the database to ensure the application only starts after the database 		is fully ready.
	•	Volumes: It uses a named volume (mysql_data) to persist database data across container restarts.
	
To deploy the entire stack, use the command:
Bash command:
docker-compose up

To run the containers in detached mode (in the background):
Bash command:
docker-compose up -d

To stop and remove the containers:
Bash command:
docker-compose down



