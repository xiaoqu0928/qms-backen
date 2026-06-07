FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/query-management-system-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080
