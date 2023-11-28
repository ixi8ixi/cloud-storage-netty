plugins {
    id("java")
}

group = "com.ix8oio8xi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.reflections:reflections:0.9.11")

    implementation("io.netty:netty-all:4.1.99.Final")
}

tasks.test {
    useJUnitPlatform()
}