# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy only the pom.xml first to leverage Docker's build cache
COPY pom.xml .

RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

RUN mvn clean install -DskipTests

# Stage 2: Create the runtime image
# Uses a slim JRE based on Eclipse Temurin for OpenJDK 21, on Ubuntu Jammy.

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user and group for enhanced security.
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Define an argument for the JAR file name. 
ARG JAR_FILE=target/task-average-time-tracker-0.0.1-SNAPSHOT.jar

# Copy the built JAR from the 'builder' stage into the /app directory of the runtime image.
COPY --from=builder /app/${JAR_FILE} app.jar

# Switch to the newly created non-root user.
USER appuser

EXPOSE 9000

# Define the command to run your Spring Boot application.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]