FROM node:20 AS nextjs-build
WORKDIR /app/ui
COPY ui/package*.json ./
RUN npm install
COPY ui .
RUN npm run build

FROM gradle:8.9.0-jdk17 AS java-build
WORKDIR /home/gradle/src
RUN rm -rf ./maestro-core/src/main/resources/nextjs-app
RUN mkdir -p ./maestro-core/src/main/resources/nextjs-app
COPY --from=nextjs-build /app/ui/out ./maestro-core/src/main/resources/nextjs-app
COPY .. .
RUN gradle assemble --no-daemon

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=java-build /home/gradle/src/example/build/libs/example.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]