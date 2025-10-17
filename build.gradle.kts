plugins {
    checkstyle
    java
    jacoco
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.github.spotbugs") version "6.0.26"
    id("org.liquibase.gradle") version "3.0.1"
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

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }

        rule {
            isEnabled = false
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
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:4.30.0")
    }
}
/*
liquibase {
    activities.register("main") {
        this.arguments = mapOf(
                "logLevel"       to "info",
                "url"            to "jdbc:postgresql://192.168.0.189:5432/job4j_devops",
                "username"       to "devops",
                "password"       to "1111",
                "classpath"      to "src/main/resources",
                "changelogFile"  to "db/changelog/db.changelog-master.xml"
        )
    }
    runList = "main"
}
*/
liquibase {
    activities.register("main") {
        this.arguments = mapOf(
                "logLevel"       to "info",
                "url"            to "jdbc:h2:mem:testdb",
                "username"       to "sa",
                "password"       to "",
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
    reports.create("html") {
        required = true
        outputLocation.set(layout.buildDirectory.file("reports/spotbugs/spotbugs.html"))
    }
}

tasks.test {
    finalizedBy(tasks.spotbugsMain)
}