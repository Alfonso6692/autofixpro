# Dockerfile multi-stage para AutoFixPro
FROM eclipse-temurin:17-jdk-jammy as build

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y wget unzip

# Descargar e instalar Gradle
RUN wget https://services.gradle.org/distributions/gradle-8.4-bin.zip -P /tmp && \
    unzip -d /opt /tmp/gradle-8.4-bin.zip && \
    ln -s /opt/gradle-8.4 /opt/gradle

ENV GRADLE_HOME=/opt/gradle
ENV PATH=$PATH:$GRADLE_HOME/bin

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración
COPY build.gradle ./
COPY settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN gradle build -x test --no-daemon

# Etapa de producción
FROM eclipse-temurin:17-jre-jammy

# Crear usuario no privilegiado
RUN addgroup --system spring && adduser --system spring --ingroup spring

WORKDIR /app

# Copiar el JAR compilado
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar propietario
RUN chown spring:spring app.jar

USER spring:spring

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]