# ---------- BUILD ----------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Compilar
RUN mvn clean package -DskipTests

# ---------- RUN ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar jar compilado
COPY --from=build /app/target/*.jar app.jar

# Puerto de Spring Boot
EXPOSE 8201

# Variables de entorno
ENV PORT=8201

# Ejecutar aplicación
ENTRYPOINT ["java","-jar","app.jar"]