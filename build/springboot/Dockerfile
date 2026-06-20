FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY target/pelisbdapi-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
