FROM ghcr.io/graalvm/graalvm-community:23.0.2
WORKDIR /app
COPY build/libs/auth-server-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]