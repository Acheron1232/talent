FROM ghcr.io/graalvm/graalvm-community:23.0.2
WORKDIR /app
COPY build/libs/gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]