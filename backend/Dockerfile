FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests
CMD ["java", "-jar", "target/*.jar"]