FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Expose the port the app runs on
#EXPOSE 8080

# Command to run the application
#ENTRYPOINT ["java", "-jar", "target/colmeia-0.0.1-SNAPSHOT.jar"]

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
# Copia apenas o JAR compilado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar
# Expõe a porta que a aplicação Spring Boot usa
EXPOSE 8080
# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]