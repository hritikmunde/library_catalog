# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# cache dependencies first
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw || true
RUN ./mvnw -B -q -DskipTests dependency:go-offline

# build
COPY src src
RUN ./mvnw -B -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /opt/app

# run as non-root
RUN addgroup -S spring && adduser -S spring -G spring

# copy app
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

# default to prod profile; Render will also set SPRING_PROFILES_ACTIVE=prod
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
USER spring
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
