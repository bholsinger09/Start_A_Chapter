# Use OpenJDK 21 JDK for building
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Copy startup script
COPY startup.sh ./
RUN chmod +x ./startup.sh

# Copy source code
COPY src src

# Build the application (skip test compilation entirely)
RUN ./mvnw clean compile package -DskipTests -Dmaven.test.skip=true

# Copy the JAR file to the expected location
RUN cp target/campus-chapter-organizer-*.jar app.jar

# Expose the port (Render will set this automatically)
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=production

# Run the application using startup script
CMD ["./startup.sh"]