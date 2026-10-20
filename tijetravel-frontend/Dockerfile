# Etapa 1: Compilacion
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml ./
COPY tijetravel-backend/pom.xml ./tijetravel-backend/
COPY tijetravel-frontend/pom.xml ./tijetravel-frontend/
COPY tijetravel-frontend ./tijetravel-frontend
RUN mvn -B -pl tijetravel-frontend clean package -DskipTests

# Etapa 2: Imagen de ejecucion liviana
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/tijetravel-frontend/target/*.war app.war
ENV PORT=8081
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.war"]

