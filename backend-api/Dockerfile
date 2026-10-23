# Stage 1: build the JAR with the JDK and the Maven wrapper
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Copy the wrapper and pom first so dependencies are cached between builds
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Strip Windows line endings from mvnw and make it executable
RUN tr -d '\r' < mvnw > mvnw.tmp && mv mvnw.tmp mvnw && chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy the source code and build the JAR (target/ is not in the repo)
COPY src ./src
RUN ./mvnw -q -DskipTests package

# Stage 2: run the JAR on a smaller JRE image
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# No port or secrets here: Render supplies PORT and SPRING_DATASOURCE_URL
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
