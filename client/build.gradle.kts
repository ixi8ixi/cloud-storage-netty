plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "com.ix8oio8xi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("io.netty:netty-all:4.1.99.Final")
    implementation(project(":common"))
}

application {
    mainClass = "com.ix8oio8xi.client.ClientApplication"
}

javafx {
    version = "19.0.2"
    modules("javafx.controls", "javafx.fxml")
}

tasks.test {
    useJUnitPlatform()
}