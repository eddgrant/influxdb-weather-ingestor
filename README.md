# influxdb-weather-ingestor

A simple utility which obtains temperature data, based on UK postcode, and sends it to an InfluxDB endpoint.

# Run the tests

Tests are separated in to 2 suites:

1. Unit tests.
2. Integration tests.

## Run the unit tests

```shell
./gradlew test
```

## Run the integration tests

### Create a Docker Network

```shell
docker network create -d bridge influxdb-weather-ingestor
```

### Run InfluxDB locally

The integration tests require an InfluxDB endpoint, which can be created by running the following command:

```shell
docker run --rm \
  --net=influxdb-weather-ingestor \
  -p 8086:8086 \
  -e DOCKER_INFLUXDB_INIT_MODE="setup" \
  -e DOCKER_INFLUXDB_INIT_USERNAME="my-very-secure-influxdb-username" \
  -e DOCKER_INFLUXDB_INIT_PASSWORD="my-very-secure-influxdb-password" \
  -e DOCKER_INFLUXDB_INIT_ORG="eddgrant" \
  -e DOCKER_INFLUXDB_INIT_BUCKET="temperature" \
  -e DOCKER_INFLUXDB_INIT_RETENTION="1w" \
  -e DOCKER_INFLUXDB_INIT_ADMIN_TOKEN="my-very-secure-influxdb-token" \
  --name influxdb \
  influxdb:2.0
```

### Run the integration test suite

Integration tests are situated within files which match the `*Integration*` naming pattern e.g. `MeteomaticsClientIntegrationSpec.kt`

Due to their nature, integration tests require access to the Meteomatics API, via a real Meteomatics account.

The Meteomatics API credentials are provided by setting the following Micronaut properties:

| Property Name          | Property Value                                            | Notes                                                                                               |
|------------------------|-----------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `meteomatics.username` | A valid username for the meteomatics API                  | Sign up for an account here: https://www.meteomatics.com/en/sign-up-weather-api-free-basic-account/ |
| `meteomatics.password` | The password associated with the meteomatics API username |                                                                                                     |

```shell
METEOMATICS_USERNAME="my-meteomatics-api-username" \
  METEOMATICS_PASSWORD="my-meteomatics-api-password" \
  INFLUXDB_ORG="my-influxdb-org" \
  INFLUXDB_BUCKET="temperature" \
  INFLUXDB_TOKEN="my-very-secure-influxdb-token" \
  INFLUXDB_URL="http://influxdb:8086?connectTimeout=5S&readTimeout=5S&writeTimeout=5S" \
  ./gradlew clean integrationTest
```

# Run influxdb-weather-ingestor

To run the infludb-weather-ingestor Docker image run the following command, being sure to provide meaningful values for the environment variables: 

```shell
docker run --rm \
  --net=influxdb-weather-ingestor \
  --env CHECKS_POSTCODE="my-uk-postcode" \
  --env METEOMATICS_USERNAME="my-meteomatics-api-username" \
  --env METEOMATICS_PASSWORD="my-meteomatics-api-password" \
  --env INFLUXDB_ORG="my-influxdb-org" \
  --env INFLUXDB_BUCKET="temperature" \
  --env INFLUXDB_TOKEN="my-very-secure-influxdb-token" \
  --env INFLUXDB_URL="http://influxdb:8086?connectTimeout=5S&readTimeout=5S&writeTimeout=5S" \
  influxdb-weather-ingestor:latest
```

```shell
docker run --rm \
  --net=influxdb-weather-ingestor \
  eddgrant/influxdb-weather-ingestor:0.1
```

# Pushing images

Docker images are built and managed by the [gradle-docker-plugin][gradle-docker-plugin], which sources its authentication details in `$HOME/.docker/config.json` by default.

To login and have Docker save the credentials in the above file do the following:

```shell
docker login --username eddgrant \
  registry.hub.docker.com/library/eddgrant/influxdb-weather-ingestor
```

[gradle-docker-plugin]: https://github.com/bmuschko/gradle-docker-plugin