plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
}

group = "org.crackajoke.agent"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("ai.koog:koog-agents:0.5.2")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("org.slf4j:slf4j-nop:2.0.9")
    //implementation("org.slf4j:slf4j-simple:2.0.7")
    //implementation("ch.qos.logback:logback-classic:1.5.13")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(18)
}