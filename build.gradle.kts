import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    `maven-publish`
}

val minecraftVersion = "1.19"
val buildNumber = System.getenv()["BUILD_NUMBER"] ?: "local"

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
    minecraft("com.mojang:minecraft:1.19")
    mappings("net.fabricmc:yarn:1.19+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.6")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.7.4+kotlin.1.6.21")
    arrayOf(
        "fabric-api-base",
        "fabric-resource-loader-v0",
        "fabric-gametest-api-v1",
        "fabric-registry-sync-v0",
    ).forEach { includeModImplementation(fabricApi.module(it, "0.55.2+1.19")) }
}

@Suppress("UnstableApiUsage")
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
    repositories {
        maven {
            url = uri("${project.buildDir}/repo")
        }
    }
}

tasks.withType<Test> {
    dependsOn(tasks.getByName("runAutoGameTest"))
}

val repoDir = "${project.buildDir}/repo"
val githubToken = System.getenv()["TOKEN"]
val repo = System.getenv()["REPO"]
val user = System.getenv()["GIT_USER"]
val email = System.getenv()["GIT_EMAIL"]

tasks.create("cloneRepo") {
    doLast {
        delete(file(repoDir))
        if (repo != null) {
            exec {
                executable("git")
                args("config", "--global", "user.name", user)
            }
            exec {
                executable("git")
                args("config", "--global", "user.email", email)
            }
            exec {
                executable("git")
                args("clone", "https://github.com/$repo.git", repoDir)
            }
        }
    }
}

tasks.create("pushRepo") {
    doLast {
        exec {
            executable("git")
            args(
                "remote",
                "set-url",
                "origin",
                "https://uten2c:$githubToken@github.com/$repo.git",
            )
            workingDir(repoDir)
        }
        exec {
            executable("git")
            args("add", ".")
            workingDir(repoDir)
        }
        exec {
            executable("git")
            args("commit", "-m", "\"${project.name} v${project.version}\"")
            workingDir(repoDir)
        }
        exec {
            executable("git")
            args("push", "origin", "main")
            workingDir(repoDir)
        }
    }
}
