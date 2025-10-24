FROM ghcr.io/graalvm/graalvm-community:23.0.2
WORKDIR /app
COPY build/libs/user-server-0.0.1-SNAPSHOT.jar app.jar
VOLUME /app/data
ENTRYPOINT ["java", "-jar", "app.jar"]
