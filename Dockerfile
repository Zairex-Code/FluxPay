# -----------------------------------------------------------------------------
# ETAPA 1: Construcción (Build)
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copia los archivos del proyecto y el Gradle Wrapper
COPY . .

# Da permisos de ejecución al wrapper y construye el JAR ejecutable
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# -----------------------------------------------------------------------------
# ETAPA 2: Entorno de Ejecución Ligero (Runtime)
# -----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia solo el JAR compilado desde la etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Usuario sin privilegios por seguridad bancaria
RUN addgroup -S fluxpay && adduser -S fluxpay -G fluxpay
USER fluxpay

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]