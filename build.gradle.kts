import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    `maven-publish`
}

val minecraftVersion = "1.19.2"
val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local-SNAPSHOT"
val publishPath = System.getenv()["PUBLISH_PATH"]

group = "dev.uten2c"
version = "$minecraftVersion+build.$buildNumber"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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

@Suppress("UnstableApiUsage")
tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to project.version,
                "minecraftVersion" to minecraftVersion,
            ),
        )
    }
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

fun DependencyHandlerScope.includeModImplementation(dep: Any) {
    include(dep)
    modImplementation(dep)
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.2")
    mappings("net.fabricmc:yarn:1.19.2+build.4:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.10")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.8.5+kotlin.1.7.20")
    arrayOf(
        "fabric-api-base",
        "fabric-resource-loader-v0",
        "fabric-gametest-api-v1",
        "fabric-registry-sync-v0",
    ).forEach { includeModImplementation(fabricApi.module(it, "0.64.0+1.19.2")) }
}

loom {
    accessWidenerPath.set(file("src/main/resources/strobo.accesswidener"))
    runtimeOnlyLog4j.set(true)
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

configure<KtlintExtension> {
    version.set("0.45.2")
    additionalEditorconfigFile.set(file(".editorconfig"))
    enableExperimentalRules.set(true)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
    if (publishPath != null) {
        repositories {
            maven {
                url = uri(publishPath)
            }
        }
    }
}

tasks.withType<Test> {
    dependsOn(tasks.getByName("runAutoGameTest"))
}
