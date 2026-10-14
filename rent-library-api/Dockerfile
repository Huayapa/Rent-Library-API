FROM eclipse-temurin:25-jdk AS base
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

FROM base AS development
COPY src ./src
EXPOSE 8080
CMD ["./mvnw", "spring-boot:run"]

FROM base AS builder
COPY src ./src
RUN ./mvnw clean -q package -DskipTests

FROM eclipse-temurin:25-jre AS production
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]