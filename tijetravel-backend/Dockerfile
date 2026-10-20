# Etapa 1: Compilacion
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY tijetravel-backend/pom.xml ./tijetravel-backend/
COPY tijetravel-frontend/pom.xml ./tijetravel-frontend/
COPY tijetravel-backend ./tijetravel-backend
RUN mvn -B -pl tijetravel-backend clean package -DskipTests

# Etapa 2: Imagen de ejecucion liviana
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/tijetravel-backend/target/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]

