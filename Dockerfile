# Stage 1: Build the application
# Uses Maven with Eclipse Temurin JDK 21 to build your Spring Boot application.
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
# Copy only the pom.xml first to leverage Docker's build cache
COPY pom.xml .
# Download all dependencies. This step is cached as long as pom.xml doesn't change.
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src
# Build the application, skipping tests as they are handled in the CI pipeline or separately.
RUN mvn clean install -DskipTests

# Stage 2: Create the runtime image
# Uses a slim JRE based on Eclipse Temurin for OpenJDK 21, on Ubuntu Jammy.
# This keeps the final image small and secure, containing only the JRE needed to run the app.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user and group for enhanced security.
# Running applications as root inside a container is generally discouraged.
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Define an argument for the JAR file name. This makes it easier to update the JAR name.
ARG JAR_FILE=target/task-average-time-tracker-0.0.1-SNAPSHOT.jar
# Copy the built JAR from the 'builder' stage into the /app directory of the runtime image.
COPY --from=builder /app/${JAR_FILE} app.jar

# Switch to the newly created non-root user.
# All subsequent commands and the application itself will run as this user.
USER appuser

# Expose the port on which your Spring Boot application runs (defined in application.properties).
# This is for documentation and container introspection; actual port mapping is done in docker-compose.yml.
EXPOSE 9000

# Define the command to run your Spring Boot application.
# The database connection details are picked up from application.properties or environment variables.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]