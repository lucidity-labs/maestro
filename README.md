# Maestro

### Need something like Temporal but simpler?

### Use Maestro

- durably persists workflows and their activities (steps)
- automatically retries failed activities
- requires only 2 Postgres tables
- packaged as a library that can be included as a simple dependency via Gradle or Maven
- no separate server deployment
- simple, readable codebase

### Usage Instructions

1. include `maestro-core` as a dependency in your `build.gradle.kts`:
    ```kotlin
    implementation("io.github.lucidity-labs:maestro-core:0.0.1")
    ```
    
    or your `pom.xml`:
    
    ```xml
    <dependency>
        <groupId>io.github.lucidity-labs</groupId>
        <artifactId>maestro-core</artifactId>
        <version>0.0.1</version>
    </dependency>
    ```
   
2. Execute [maestro.sql](./script/maestro.sql) against your Postgres database to create the necessary schema. If you instead wish to start a Dockerized Postgres instance locally, execute `docker compose -f ./script/docker-compose.yml up --build`. In this case, [maestro.sql](./script/maestro.sql) will automatically be applied. 

3. See [the example app in this repo](./example) for an example of how to create your first durable workflow! Start the app with `./gradlew -p ./example bootRun`. Make sure you've already started the Dockerized Postgres instance from step 2! Then, send the app some HTTP requests using [requests.http](./example/script/requests.http).