# Run the tests

Tests are separated in to 2 suites:

1. Unit tests.
2. Integration tests.

## Selecting the weather provider

The application supports multiple weather data providers. You select which one to use via the configuration property `weather.provider` or the environment variable `WEATHER_PROVIDER`.

- Default: `weatherapi.com` (no need to set a value when using WeatherAPI).
- To use Meteomatics: set `WEATHER_PROVIDER=meteomatics` and provide Meteomatics credentials.

Notes for tests:
- Unit and integration test environments already set a provider in their respective `application-*-test.yml` files, so you typically do not need to set `WEATHER_PROVIDER` when running tests.
- You still need to provide the relevant credentials (see sections below).

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
  -e DOCKER_INFLUXDB_INIT_BUCKET="weather" \
  -e DOCKER_INFLUXDB_INIT_RETENTION="1w" \
  -e DOCKER_INFLUXDB_INIT_ADMIN_TOKEN="my-very-secure-influxdb-token" \
  --name influxdb \
  influxdb:2.0
```

### Run the integration test suite

Integration tests are situated within files which match the `*Integration*` naming pattern, for example:
- `MeteomaticsClientIntegrationSpec.kt`
- `WeatherApiDotComClientIntegrationSpec.kt`

Provider access notes:
- WeatherAPI (default provider): tests require a valid WeatherAPI key. If the key is not present in the environment, the WeatherAPI integration test will be skipped automatically.
- Meteomatics: tests require valid Meteomatics credentials.

WeatherAPI credentials are provided via the following property/env var:

| Property Name        | Env Var              | Notes                                  |
|----------------------|----------------------|----------------------------------------|
| `weatherapi.apiKey`  | `WEATHERAPI_API_KEY` | Create a key at https://www.weatherapi.com/ |

Meteomatics credentials are provided via the following properties/env vars:

| Property Name           | Env Var                | Notes                                                                                               |
|-------------------------|------------------------|-----------------------------------------------------------------------------------------------------|
| `meteomatics.username`  | `METEOMATICS_USERNAME` | Sign up for an account here: https://www.meteomatics.com/en/sign-up-weather-api-free-basic-account/ |
| `meteomatics.password`  | `METEOMATICS_PASSWORD` | The password associated with the Meteomatics API username                                           |

Example: run integration tests with WeatherAPI

```shell
WEATHERAPI_API_KEY="my-weatherapi-api-key" \
  ./gradlew clean integrationTest
```

Example: run integration tests with Meteomatics plus local InfluxDB

```shell
METEOMATICS_USERNAME="my-meteomatics-api-username" \
METEOMATICS_PASSWORD="my-meteomatics-api-password" \
INFLUXDB_ORG="my-influxdb-org" \
INFLUXDB_BUCKET="weather" \
INFLUXDB_TOKEN="my-very-secure-influxdb-token" \
INFLUXDB_URL="http://influxdb:8086?connectTimeout=5S&readTimeout=5S&writeTimeout=5S" \
./gradlew clean integrationTest
```

# Pushing images

Docker images are built and managed by the [gradle-docker-plugin][gradle-docker-plugin], which sources its authentication details in `$HOME/.docker/config.json` by default.

To login and have Docker save the credentials in the above file do the following:

```shell
docker login --username eddgrant \
  registry.hub.docker.com/library/eddgrant/influxdb-weather-ingestor
```

[gradle-docker-plugin]: https://github.com/bmuschko/gradle-docker-plugin