rootProject.name="influxdb-weather-ingestor"

val micronautVersion: String by settings // from gradle.properties
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("mn") {
            // micronaut-platform doesn't seem to follow the main Micronaut version + release train.
            from("io.micronaut.platform:micronaut-platform:4.10.3")
        }
    }
}
