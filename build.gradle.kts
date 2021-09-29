import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    id("fabric-loom") version "0.9-SNAPSHOT"
    `maven-publish`
}

group = "dev.uten2c"
version = "23"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = "1.17.1")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.17.1+build.61", classifier = "v2")
    modImplementation("net.fabricmc:fabric-loader:0.11.6")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.6.4+kotlin.1.5.30")
}

loom {
    accessWidenerPath.set(file("src/main/resources/strobo.accesswidener"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
    }
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar")
val remapSourcesJar = tasks.getByName<RemapSourcesJarTask>("remapSourcesJar")

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            artifact(remapJar) {
                builtBy(remapJar)
            }
            artifact(sourcesJar) {
                builtBy(remapSourcesJar)
            }
        }
    }
    repositories {
        maven {
            url = uri("${System.getProperty("user.home")}/private-repo")
            println(uri("${System.getProperty("user.home")}/private-repo"))
        }
    }
}