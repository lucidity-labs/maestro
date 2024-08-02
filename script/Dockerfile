FROM gradle:8.9.0-jdk17 AS build
WORKDIR /home/gradle/src
COPY .. .
RUN gradle assemble --no-daemon

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/example/build/libs/example.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]