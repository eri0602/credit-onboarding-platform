# Multi-stage Dockerfile for backend
# Build stage: use Maven with Eclipse Temurin JDK 21 to build the jar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
# copy only what's needed to run maven efficiently
COPY pom.xml ./
COPY src ./src
# run maven package (skip tests for faster builds; remove -DskipTests in CI if you want tests)
RUN mvn -B -DskipTests package

# Runtime stage: use a small JRE 21 image
FROM eclipse-temurin:21-jre
# non-root user (optional)
# RUN addgroup --system app && adduser --system --ingroup app app

WORKDIR /app
# copy the fat jar created by Spring Boot plugin
COPY --from=build /workspace/target/*.jar ./app.jar
EXPOSE 8080

# Use the JVM defaults; start the Spring Boot jar directly. Compose already
# waits for the DB service health (depends_on with condition: service_healthy).
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
