import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.22"
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.1"
    id("io.micronaut.aot") version "4.2.1"
    id("com.bmuschko.docker-remote-api") version "9.4.0" // apply false
}

version = "0.1"
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

    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")

    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Replace Jackson with micronaut-serialization
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("com.influxdb:influxdb-client-kotlin:6.10.0")

    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    implementation("io.micronaut.validation:micronaut-validation")

    testImplementation("io.micronaut:micronaut-http-client")
}

application {
    mainClass.set("com.eddgrant.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

graalvmNative.toolchainDetection.set(false)
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
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("*Integration*")
    }
    ignoreFailures = true
}

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    useJUnitPlatform()
    filter {
        includeTestsMatching("*Integration*")
    }
    mustRunAfter(tasks.test)
}

tasks.check {
    dependsOn(integrationTestTask)
}

tasks.named<DockerBuildImage>("dockerBuild") {
    imageId.set("eddgrant/${project.name}")
    images.add("${dockerRegistryHost}/eddgrant/${project.name}:${project.version}")
    images.add("${dockerRegistryHost}/eddgrant/${project.name}:latest")
}

tasks.named<DockerPushImage>("dockerPush") {
    images.set(listOf(
        "${dockerRegistryHost}/eddgrant/${project.name}:${project.version}",
        "${dockerRegistryHost}/eddgrant/${project.name}:latest"
    ))
}

/*tasks.named<DockerBuildImage>("dockerBuildNative") {
    images.add("eddgrant/${project.name}-native:$project.version")
    images.add("eddgrant/${project.name}-native:latest")
}*/

/*tasks.named<DockerBuildImage>("dockerBuildCrac") {
    images.add("eddgrant/${project.name}-crac:$project.version")
    images.add("eddgrant/${project.name}-crac:latest")
}*/


