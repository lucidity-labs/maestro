plugins {
	java
	id("com.vanniktech.maven.publish") version "0.29.0"
}

dependencies {
	implementation("org.postgresql:postgresql:42.7.3")
	implementation("com.zaxxer:HikariCP:5.1.0")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
	implementation("io.github.resilience4j:resilience4j-retry:2.2.0")
	implementation("com.github.kagkarlsson:db-scheduler:14.0.1")

	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
