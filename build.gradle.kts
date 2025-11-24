plugins {
    checkstyle
    java
    jacoco
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.github.spotbugs") version "6.0.26"
    id("org.liquibase.gradle") version "3.0.1"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "ru.job4j.devops"
version = "1.0.0"
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core:4.30.0")
    implementation("org.postgresql:postgresql:42.7.4")
}

repositories {
    mavenCentral()
}

val integrationTest by sourceSets.creating {
    java {
        srcDir("src/integrationTest/java")
    }
    resources {
        srcDir("src/integrationTest/resources")
    }

    // Let the integrationTest classpath include the main and test outputs
    compileClasspath += sourceSets["main"].output + sourceSets["test"].output
    runtimeClasspath += sourceSets["main"].output + sourceSets["test"].output
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations["testImplementation"])
}
val integrationTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations["testRuntimeOnly"])
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
            // ПРАВИЛЬНЫЙ СИНТАКСИС ДЛЯ KOTLIN DSL:
            excludes = listOf(
                    "ru.job4j.devops.models.*",
                    "ru.job4j.devops.CalcApplication"
            )
        }

        rule {
            enabled = false  // В Kotlin DSL используем 'enabled' вместо 'isEnabled'
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    implementation(libs.assertj.core)
    liquibaseRuntime("org.liquibase:liquibase-core:4.30.0")
    liquibaseRuntime("org.postgresql:postgresql:42.7.4")
    liquibaseRuntime("javax.xml.bind:jaxb-api:2.3.1")
    liquibaseRuntime("ch.qos.logback:logback-core:1.5.15")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.5.15")
    liquibaseRuntime("info.picocli:picocli:4.6.1")
    testImplementation ("com.h2database:h2")

    testImplementation("com.github.docker-java:docker-java-core:3.3.6")
    testImplementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.6")
    testImplementation ("org.testcontainers:testcontainers:2.0.2")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation("org.testcontainers:postgresql:1.19.8")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:4.30.0")
    }
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
                "logLevel"       to env.LOG_LEVEL.value,
                "url"            to env.DB_URL.value,
                "username"       to env.DB_USERNAME.value,
                "password"       to env.DB_PASSWORD.value,
                "classpath"      to "src/main/resources",
                "changelogFile"  to "db/changelog/db.changelog-master.xml"
        )
    }
    runList = "main"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Zip>("zipJavaDoc") {
    group = "documentation"
    description = "Packs the generated Javadoc into a zip archive"

    dependsOn("javadoc")

    from("build/docs/javadoc")
    archiveFileName.set("javadoc.zip")
    destinationDirectory.set(layout.buildDirectory.dir("archives"))
}

tasks.spotbugsMain {
    ignoreFailures = true

    //reports.create("html") {
    //    required = true
    //    outputLocation.set(layout.buildDirectory.file("reports/spotbugs/spotbugs.html"))
    //}
}

tasks.named<Test>("test") {
    systemProperty("spring.datasource.url", env.DB_URL.value)
    systemProperty("spring.datasource.username", env.DB_USERNAME.value)
    systemProperty("spring.datasource.password", env.DB_PASSWORD.value)
}

tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"

    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath

    // Usually run after regular unit tests
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn("integrationTest")
}
