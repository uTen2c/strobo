plugins {
    kotlin("jvm") version "1.5.21"
    id("fabric-loom") version "0.9-SNAPSHOT"
}

group = "dev.uten2c"
version = "1.0-SNAPSHOT"

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

fun DependencyHandlerScope.includeAndExpose(dep: Any) {
    modApi(dep)
    include(dep)
    
}

dependencies {
    minecraft("com.mojang:minecraft:1.17.1")
    mappings("net.fabricmc:yarn:1.17.1+build.43:v2")
    modImplementation("net.fabricmc:fabric-loader:0.11.6")
    includeAndExpose("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
}

loom {
    accessWidenerPath.set(file("src/main/resources/strobo.accesswidener"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
    }
}
