FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/omnirent.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]