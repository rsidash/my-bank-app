FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/config-server-0.0.1-SNAPSHOT.jar app.jar
COPY ../config-repo /app/config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
