# Stage 1: Build execution using Maven and OpenJDK 17
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Cache dependencies by copying only the pom.xml first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy application source code and compile structural JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Ultra-lightweight alpine runtime environment
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the generated artifact jar from the build stage safely
COPY --from=build /app/target/*.jar app.jar

# Expose internal application port
EXPOSE 8080

# Run the backend execution command
ENTRYPOINT ["java", "-jar", "app.jar"]