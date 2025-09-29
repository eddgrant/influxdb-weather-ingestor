import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage
import io.micronaut.gradle.docker.MicronautDockerfile

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.21"
    id("com.google.devtools.ksp") version "2.1.20-1.0.32"
    id("com.gradleup.shadow") version "8.3.9"
    id("io.micronaut.application") version "4.5.5"
    id("io.micronaut.aot") version "4.5.5"
    id("com.bmuschko.docker-remote-api") version "9.4.0" // apply false
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

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")
    implementation("com.influxdb:influxdb-client-kotlin:7.3.0")

    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    implementation("io.micronaut.validation:micronaut-validation")

    testImplementation("io.micronaut:micronaut-http-client")
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

graalvmNative.toolchainDetection.set(true)
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
    // https://github.com/mockk/mockk/issues/681
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
}

tasks.named<MicronautDockerfile>("dockerfile") {
    baseImage.set("eclipse-temurin:${org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21.target}-jre")
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
    images.add("${dockerRegistryHost}/eddgrant/${project.name}:local")
    mustRunAfter(tasks.withType(Test::class.java))
}

tasks.register<DockerPushImage>("dockerPushVersion") {
    dependsOn("dockerBuild")
    images.set(listOf(
        "${dockerRegistryHost}/eddgrant/${project.name}:${project.version}",
    ))
}

tasks.register<DockerPushImage>("dockerPushLatest") {
    dependsOn("dockerBuild")
    images.set(listOf(
        "${dockerRegistryHost}/eddgrant/${project.name}:latest",
    ))
}

/*tasks.register<DockerTagImage>("tagPR") {
    tag.set("${dockerRegistryHost}/eddgrant/${project.name}:${project.pr}")
}*/

/*tasks.named<DockerBuildImage>("dockerBuildNative") {
    images.add("eddgrant/${project.name}-native:$project.version")
    images.add("eddgrant/${project.name}-native:latest")
}*/

/*tasks.named<DockerBuildImage>("dockerBuildCrac") {
    images.add("eddgrant/${project.name}-crac:$project.version")
    images.add("eddgrant/${project.name}-crac:latest")
}*/


