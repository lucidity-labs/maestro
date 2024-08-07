<p align="center">
  <img src="https://github.com/user-attachments/assets/fc1169d0-6a38-45a8-88a7-016b3a7d0567" alt="Maestro">
</p>

### Need something like Temporal but simpler?

### Use Maestro

- durably persists workflows and their activities (workflow steps)
- automatically retries failed activities
- requires only 2 Postgres tables
- packaged as a library that can be included as a simple dependency via Gradle or Maven
- no separate server deployment
- simple, readable codebase

### UI

Access the UI simply by navigating to port `8000` after starting your application. No separate deployment needed!

<img width="1715" alt="Screenshot 2024-08-06 at 22 18 11" src="https://github.com/user-attachments/assets/52f3c4d8-3883-4a43-bb36-2746aac6acc0">


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
   docker compose -f script/docker-compose.yml up --build --force-recreate postgres
   ```

3. Write your durable workflow!

### Example app
Take a look at [the example app](./example) for an example of how to create your first durable workflow! 

Start the app with:
```bash
docker compose -f script/docker-compose.yml up --build --force-recreate
```

Then, try calling the app using [requests.http](./example/script/requests.http).

View all of your workflows and workflow events at http://localhost:8000!
