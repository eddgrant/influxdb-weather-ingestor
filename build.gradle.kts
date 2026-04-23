import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import io.micronaut.gradle.docker.MicronautDockerfile

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.3.21"
    id("com.google.devtools.ksp") version "2.3.6"
    id("com.gradleup.shadow") version "9.4.1"
    id("io.micronaut.application") version "4.6.2"
    id("io.micronaut.aot") version "4.6.2"
    id("com.bmuschko.docker-remote-api") version "10.0.0" // apply false
}

group = "com.eddgrant"

val micronautVersion by properties
val kotlinVersion by properties
val kotestVersion by properties
val mockkVersion by properties
val dockerRegistryHost by properties

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("org.yaml:snakeyaml")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    ksp(mn.micronaut.http.validation)
    ksp(mn.micronaut.serde.processor)
    ksp(mn.micronaut.validation.processor)

    implementation(mn.micronaut.serde.jackson)
    implementation(mn.micronaut.http.client)
    implementation(mn.micronaut.jackson.databind)
    implementation(mn.micronaut.kotlin.runtime)
    implementation(mn.micronaut.validation)
    implementation("io.micronaut:micronaut-management")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("com.influxdb:influxdb-client-kotlin:8.0.0")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")

    testImplementation("io.projectreactor:reactor-test")
}

application {
    mainClass.set("com.eddgrant.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
}


/**
 * Important: Mocking of non-interface beans is currently broken and undocumented
 * in Micronaut and requires this configuration in order to work.
 * https://github.com/micronaut-projects/micronaut-core/issues/3972
 */
allOpen {
    annotations("jakarta.inject.Singleton")
}

graalvmNative {
    toolchainDetection = true
    binaries.all {
        // Ensure the image is built for maximum machine type compatibility.
        buildArgs.add("-march=compatibility")
    }
}
micronaut {
    version("$micronautVersion")
    runtime("netty") //TODO: If we remove this will it remove the HTTP server?
    testRuntime("kotest5")
    processing {
        module(project.name)
        group(project.group.toString())
        incremental(true)
        annotations("com.eddgrant.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
        replaceLogbackXml.set(true)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("*Integration*")
    }
    ignoreFailures = true
    // https://github.com/mockk/mockk/issues/681
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
}

tasks.named<MicronautDockerfile>("dockerfile") {
    baseImage.set("eclipse-temurin:${org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21.target}-jre")
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
    filter {
        includeTestsMatching("*Integration*")
    }
    mustRunAfter(tasks.test)
}

tasks.check {
    dependsOn(integrationTestTask)
}

val configureDockerImageNames: (DockerBuildImage) -> Unit = {
    it.images.add("eddgrant/${project.name}:local")
    it.images.add("${dockerRegistryHost}/eddgrant/${project.name}:${project.version}")
    it.images.add("${dockerRegistryHost}/eddgrant/${project.name}:latest")
    it.mustRunAfter(tasks.withType(Test::class.java))
}

tasks.named<DockerBuildImage>("dockerBuild", configureDockerImageNames)
tasks.named<DockerBuildImage>("dockerBuildNative", configureDockerImageNames)

tasks.register<DockerPushImage>("dockerPushVersion") {
    dependsOn("dockerBuildNative")
    images.set(listOf(
        "${dockerRegistryHost}/eddgrant/${project.name}:${project.version}",
    ))
}

tasks.register<DockerPushImage>("dockerPushLatest") {
    dependsOn("dockerBuildNative")
    images.set(listOf(
        "${dockerRegistryHost}/eddgrant/${project.name}:latest",
    ))
}
