# ---------- ETAPA 1: COMPILAR ----------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Compilar proyecto
RUN mvn clean package -DskipTests

# ---------- ETAPA 2: EJECUTAR ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR generado
COPY --from=build /app/target/*.jar app.jar

# Puerto Spring Boot
EXPOSE 8201

ENV PORT=8201

# Ejecutar app
ENTRYPOINT ["java","-jar","app.jar"]