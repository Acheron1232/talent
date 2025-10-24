FROM ghcr.io/graalvm/graalvm-community:23.0.2
WORKDIR /app
COPY build/libs/social-server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]