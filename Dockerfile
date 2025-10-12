# Estágio 1: Build da aplicação
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app


# Copia os arquivos de dependência
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Garante que o maven wrapper é executável
RUN chmod +x mvnw

#Baixa as dependências (cache de camada)
RUN ./mvnw dependency:go-offline

# Copia o código fonte e compila
COPY src ./src
RUN ./mvnw package -DskipTests

# Estágio 2: Criação da imagem de execução final
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Cria um usuário e grupo não-root para executar a aplicação
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copia o JAR do estágio de build com as permissões corretas
COPY --from=builder --chown=appuser:appgroup /app/target/*.jar app.jar

# Define o usuário não-root para executar a aplicação
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]