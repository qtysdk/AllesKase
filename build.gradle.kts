import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
    jacoco
}

group = "org.gaas.alleskase"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.4")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    // report is always generated after tests run
    finalizedBy(tasks.getByName("jacocoTestReport"))
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

