<p align="center">
  <img src="https://github.com/user-attachments/assets/534ec567-c91f-4748-ab80-ec500e53d9de" alt="Maestro">
</p>




### Need something like Temporal but simpler?

### Use Maestro

- durably persists workflows and their activities (steps)
- automatically retries failed activities
- requires only 2 Postgres tables
- packaged as a library that can be included as a simple dependency via Gradle or Maven
- no separate server deployment
- simple, readable codebase

### UI

Access the UI simply by navigating to port `3000` after starting your application. There is no separate deployment needed!

<img width="1726" alt="Screenshot 2024-08-01 at 20 48 14" src="https://github.com/user-attachments/assets/4237695b-e472-41db-bc77-339d27646197">


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
   
2. Execute [maestro.sql](./script/maestro.sql) against your Postgres database to create the necessary schema. If you instead wish to start a Dockerized Postgres instance locally with [maestro.sql](./script/maestro.sql) already applied, just execute: 
   ```bash 
   docker compose -f ./script/docker-compose.yml up --build
   ```

3. Write your durable workflow!

### Example app
Take a look at [the example app](./example) for an example of how to create your first durable workflow! 

Start the app with:
```bash
export MAESTRO_DB_URL=jdbc:postgresql://localhost:5432/application_db
export MAESTRO_DB_USERNAME=postgres
export MAESTRO_DB_PASSWORD=password

./gradlew -p ./example bootRun
```
Note: this requires the Dockerized Postgres instance from step 2 above.

Then, send the app some HTTP requests using [requests.http](./example/script/requests.http).
