![Unit Tests](https://github.com/eddgrant/influxdb-weather-ingestor/actions/workflows/unit-tests.yaml/badge.svg)

# influxdb-weather-ingestor

A simple utility which obtains temperature data, based on UK postcode, and sends it to an InfluxDB endpoint.

# How to use it

## Sign up for a Meteomatics account

`influxdb-weather-ingestor` sources its temperature data from Meteomatics. 

Meteomatics offer free basic accounts which can be used. [Sign up](https://www.meteomatics.com/en/sign-up-weather-api-free-basic-account/) for a free basic Meteomatics account.

Make a note of your Meteomatics username and password. You will need them later.

## Run InfluxDB locally

An InfluxDB endpoint is required to collect temperature data. You might have an InfluxDB setup already, but if not one can be easily created by running the following commands:

```shell
docker network create -d bridge influxdb-weather-ingestor
```

```shell
docker run --rm \
  --net=influxdb-weather-ingestor \
  -p 8086:8086 \
  -e DOCKER_INFLUXDB_INIT_MODE="setup" \
  -e DOCKER_INFLUXDB_INIT_USERNAME="my-very-secure-influxdb-username" \
  -e DOCKER_INFLUXDB_INIT_PASSWORD="my-very-secure-influxdb-password" \
  -e DOCKER_INFLUXDB_INIT_ORG="my-influxdb-org" \
  -e DOCKER_INFLUXDB_INIT_BUCKET="weather" \
  -e DOCKER_INFLUXDB_INIT_RETENTION="1w" \
  -e DOCKER_INFLUXDB_INIT_ADMIN_TOKEN="my-very-secure-influxdb-token" \
  --name influxdb \
  influxdb:2.0
```

## Run influxdb-weather-ingestor

To run the influxdb-weather-ingestor Docker image run the following command:

```shell
docker run --rm \
  --net=influxdb-weather-ingestor \
  --env CHECKS_POSTCODE="my-uk-postcode" \
  --env METEOMATICS_USERNAME="my-meteomatics-api-username" \
  --env METEOMATICS_PASSWORD="my-meteomatics-api-password" \
  --env INFLUXDB_ORG="my-influxdb-org" \
  --env INFLUXDB_BUCKET="weather" \
  --env INFLUXDB_TOKEN="my-very-secure-influxdb-token" \
  --env INFLUXDB_URL="http://influxdb:8086?connectTimeout=5S&readTimeout=5S&writeTimeout=5S" \
  eddgrant/influxdb-weather-ingestor:latest
```

Ensure that the InfluxDB variables match the ones used when setting up InfluxDB.

Ensure that you set a valid UK postcode for the `CHECKS_POSTCODE` environment variable.

Ensure that you set your Meteomatics username and password for the `METEOMATICS_USERNAME` and `METEOMATICS_PASSWORD` environment variables.

## Check the logs

influxdb-weather-ingestor should start and begin to log its output to the console:

```
 __  __ _                                  _   
|  \/  (_) ___ _ __ ___  _ __   __ _ _   _| |_ 
| |\/| | |/ __| '__/ _ \| '_ \ / _` | | | | __|
| |  | | | (__| | | (_) | | | | (_| | |_| | |_ 
|_|  |_|_|\___|_|  \___/|_| |_|\__,_|\__,_|\__|
18:09:08.145 [main] INFO  i.m.l.PropertiesLoggingLevelsConfigurer - Setting log level 'INFO' for logger: 'io.http.client'
18:09:08.147 [main] INFO  i.m.l.PropertiesLoggingLevelsConfigurer - Setting log level 'DEBUG' for logger: 'io.retry'
18:09:08.147 [main] INFO  i.m.l.PropertiesLoggingLevelsConfigurer - Setting log level 'INFO' for logger: 'com.eddgrant'
18:09:08.147 [main] INFO  i.m.l.PropertiesLoggingLevelsConfigurer - Setting log level 'INFO' for logger: 'io.micronaut'
18:09:08.556 [main] INFO  c.e.i.checks.RegisterChecksAction - Temperature checks scheduled to run on schedule: * * * * *
18:09:08.560 [main] INFO  io.micronaut.runtime.Micronaut - Startup completed in 606ms. Server Running: http://4294397d97fa:8080
```

By default influxdb-weather-ingestor will check the temperature every minute. 

This can be altered by setting the `CHECKS_SCHEDULE_EXPRESSION` environment variable e.g.

```shell
CHECKS_SCHEDULE_EXPRESSION=*/10 * * * *`
````

Each time a temperature measurement is sent to InfluxDB an `INFO` level log entry is written e.g.

```shell
17:23:01.780 [scheduled-executor-thread-1] INFO  c.e.i.checks.TemperatureEmitter - Temperature measurement sent: Postcode: AB12 3CD, Temperature: 13.6
```

Development related information can be found in [docs/development.md](docs/development.md)