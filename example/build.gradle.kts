plugins {
	java
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
}

dependencies {
	implementation(project(":maestro-core"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.github.kagkarlsson:db-scheduler:14.0.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
