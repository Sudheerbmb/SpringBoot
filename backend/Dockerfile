FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

CMD ["java", "-jar", "target/student-app-0.0.1-SNAPSHOT.jar"]
