# ========================================
# Chess Server - Multi-stage Build
# ========================================

# --- Builder Stage ---
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml files first (leverage Docker layer caching)
COPY pom.xml ./
COPY shared/pom.xml shared/
COPY server/pom.xml server/
COPY client/pom.xml client/

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -f pom.xml

# Copy source code
COPY shared/src shared/src
COPY server/src server/src
COPY client/src client/src

# Build the project (skip tests for a leaner image; use 'mvn package' to include tests)
RUN mvn clean package -f pom.xml -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks and unzip if needed
RUN apk add --no-cache wget

WORKDIR /app

# Copy the server JAR from builder stage (includes all dependencies via Maven shade/jar-with-dependencies)
# Adjust the jar name/path based on your actual target file after build:
COPY --from=builder /app/server/target/*.jar server.jar

# Open firewall port for HTTP + WebSocket
EXPOSE 8080

# Start the server (default port 8080, can be overridden)
ENTRYPOINT ["java", "-jar", "server.jar"]