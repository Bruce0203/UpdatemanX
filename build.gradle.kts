plugins {
    kotlin("jvm") version "1.8.21"
    id ("com.github.johnrengelman.shadow") version "7.1.1"
    kotlin("plugin.serialization") version "1.8.21"
    id("kr.entree.spigradle") version "2.1.1"

}

group = "io.github.bruce0203.updatemanx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    implementation("io.github.bruce0203:jgit:5")
    implementation("io.github.bruce0203:mccommand:6")
    implementation("io.github.bruce0203:plugman:4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.forEach { it.outputs.cacheIf { true } }

spigot {
    name = rootProject.name
    this.version = "${project.version}"
    commands {
        create("updateman") {
            permission = "updatemanx.admin"
        }
    }
    permissions {
        create("updatemanx.admin") { defaults = "op" }
    }
}

