plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
    jacoco
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
