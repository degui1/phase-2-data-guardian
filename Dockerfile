# BUILD

FROM maven:3.10.0-eclipse-temurin-21-noble AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# RUN
FROM eclipse-temurin:21-jre-noble

WORKDIR /app/run

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]