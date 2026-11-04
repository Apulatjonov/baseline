FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S signal && adduser -S signal -G signal
WORKDIR /app
COPY --from=build /build/target/signal-0.1.0.jar app.jar
USER signal
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
