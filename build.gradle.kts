import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    id("fabric-loom") version "0.9-SNAPSHOT"
    `maven-publish`
}

group = "dev.uten2c"
version = "41"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

sourceSets {
    val main = getByName("main")
    create("gametest") {
        compileClasspath += main.compileClasspath
        compileClasspath += main.output
        runtimeClasspath += main.runtimeClasspath
        runtimeClasspath += main.output
    }
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf("version" to project.version))
    }
}

repositories {
    mavenCentral()
}

fun DependencyHandlerScope.modIncludeImplementation(dep: Any) {
    include(dep)
    modImplementation(dep)
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = "1.17.1")
    mappings(group = "net.fabricmc", name = "yarn", version = "1.17.1+build.61", classifier = "v2")
    modImplementation("net.fabricmc:fabric-loader:0.11.6")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.6.5+kotlin.1.5.31")
    modIncludeImplementation(fabricApi.module("fabric-api-base", "0.41.0+1.17"))
    modIncludeImplementation(fabricApi.module("fabric-resource-loader-v0", "0.41.0+1.17"))
    modIncludeImplementation(fabricApi.module("fabric-gametest-api-v1", "0.41.0+1.17"))
    modIncludeImplementation(fabricApi.module("fabric-registry-sync-v0", "0.41.0+1.17"))
}

loom {
    accessWidenerPath.set(file("src/main/resources/strobo.accesswidener"))
    runs {
        create("gameTest") {
            server()
            name("Game Test")
            runDir("build/gametest")
            source(sourceSets.getByName("gametest"))
        }
        create("autoGameTest") {
            server()
            name("Auto Game Test")
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${project.buildDir}/junit.xml")
            runDir("build/gametest")
            source(sourceSets.getByName("gametest"))
        }
    }
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

tasks.withType<Test> {
    dependsOn(tasks.getByName("runAutoGameTest"))
}
