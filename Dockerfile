# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:21-jre-slim
WORKDIR /app

# Create a non-root user and group
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy the JAR from the builder stage
ARG JAR_FILE=target/task-time-tracker-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/${JAR_FILE} app.jar

# Create a directory for H2 data and change ownership
RUN mkdir /data && chown appuser:appgroup /data

# Switch to the non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Command to run the application
# We point the H2 database to the /data directory inside the container.
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.datasource.url=jdbc:h2:file:/data/tasktrackerdb"]