# Step 1: Use an official Java runtime as a parent image
FROM openjdk:23-jdk-slim


# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy the JAR file from the local machine to the container
COPY target/BankingApp-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port the app will run on
EXPOSE 8080

# Step 5: Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]