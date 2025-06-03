# Use an official OpenJDK runtime as base
# Use an official OpenJDK runtime as base
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the existing folder on Ubuntu
COPY /apps/jars/bpa-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
