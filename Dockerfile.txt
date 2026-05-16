# ==================================================
# ETAPA 1 - BUILD (Compilación Maven)
# ==================================================
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Instalar bash por compatibilidad
RUN apk add --no-cache bash

# Copiar archivos de Maven primero (mejor cache)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Dar permisos al wrapper
RUN chmod +x mvnw

# Descargar dependencias (cache Docker)
RUN ./mvnw dependency:go-offline

# Copiar código fuente
COPY src ./src
COPY uploads ./uploads

# Compilar proyecto
RUN ./mvnw clean package -DskipTests


# ==================================================
# ETAPA 2 - RUNTIME (Ejecución)
# ==================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Metadata del contenedor
LABEL maintainer="Enyimber Aguilar"
LABEL project="InventoPro"
LABEL description="Sistema de Inventario Spring Boot + MongoDB"

# Zona horaria Colombia
ENV TZ=America/Bogota

# Puerto del proyecto
ENV SERVER_PORT=8201

# Perfil de Spring
ENV SPRING_PROFILES_ACTIVE=prod

# Memoria JVM optimizada
ENV JAVA_OPTS="-Xms256m -Xmx1024m"

# Crear carpeta uploads
RUN mkdir -p /app/uploads

# Copiar jar compilado
COPY --from=build /app/target/*.jar app.jar

# Copiar uploads si existen
COPY --from=build /app/uploads /app/uploads

# Exponer puerto
EXPOSE 8201

# Healthcheck del contenedor
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
CMD wget -qO- http://localhost:8201/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
