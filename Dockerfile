# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw || true
RUN ./mvnw -B -q -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -B -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app
RUN addgroup -S spring && adduser -S spring -G spring
VOLUME ["/var/data"]                                # SQLite persistent mount
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENV SQLITE_PATH=/var/data/library.db
EXPOSE 8080
USER spring
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
