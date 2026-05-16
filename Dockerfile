# ---------- BUILD ----------
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

RUN apk add --no-cache bash

# Copiar Maven Wrapper
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Permisos
RUN chmod +x mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline

# Copiar código fuente
COPY src src

# Compilar
RUN ./mvnw clean package -DskipTests

# ---------- RUN ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8201

ENV PORT=8201

ENTRYPOINT ["java","-jar","app.jar"]