# Obtain Temperature Data from weatherapi.com

Originally, influxdb-weather-ingestor obtained weather temperature data from Meteomatics via `WeatherService` using `MeteomaticsClient`.

Because Meteomatics are shutting down their free API access, we integrated an alternative provider: https://www.weatherapi.com/ (free up to 1000 requests/day), while keeping Meteomatics support. The application can now use either provider based on configuration.

## Desired Outcomes

It should be possible for a user of influxdb-weather-ingestor to obtain temperature data from either Meteomatics or weatherapi.com.

The temperature API used should depend on application configuration, with the user providing the appropriate credentials. Configuration determines which Micronaut beans get instantiated and used.

## Implementation summary (as built)

- Introduced provider-agnostic WeatherClient interface that abstracts temperature retrieval.
- Implemented two adapters behind this interface:
  - MeteomaticsWeatherClient: wraps existing MeteomaticsClient and parses the nested JSON response to a Double.
  - WeatherApiWeatherClient: uses a new WeatherApiDotComClient HTTP client to call WeatherAPI current weather endpoint and parse `current.temp_c`.
- Conditional bean activation via configuration:
  - Set the property `weather.provider` to either `meteomatics` or `weatherapi.com`.
  - Meteomatics beans (MeteomaticsWeatherClient, MeteomaticsConfiguration, auth filter) are only loaded when `weather.provider=meteomatics`.
  - WeatherAPI beans (WeatherApiWeatherClient, WeatherApiConfiguration) are only loaded when `weather.provider=weatherapi.com`.
- WeatherService now depends on WeatherClient (not on Meteomatics). On startup it logs an INFO message indicating which provider is configured and which client bean is in use.
- No factory class is required; Micronaut DI and `@Requires` conditions select the correct implementation automatically based on configuration.

## Configuration

- Core selector: `weather.provider`
  - Default is `weatherapi.com` (set in `src/main/resources/application.yml`).
  - Set to `meteomatics` to use Meteomatics.

- WeatherAPI
  - Property: `weatherapi.apiKey`
  - Typical environment variable: `WEATHERAPI_API_KEY` (Micronaut maps env vars to properties). Some tooling and tests also accept `WEATHERAPI_APIKEY`.
  - HTTP service id: `weatherapi` with base URL `https://api.weatherapi.com` (configured under `micronaut.http.services.weatherapi.urls`).

- Meteomatics
  - Properties: `meteomatics.username`, `meteomatics.password`
  - Environment variables: `METEOMATICS_USERNAME`, `METEOMATICS_PASSWORD`
  - HTTP service id: `meteomatics` with base URL `https://api.meteomatics.com` (configured under `micronaut.http.services.meteomatics.urls`).

Refer to README for full docker run examples and environment variable setup.

## Endpoints used

- WeatherAPI: `GET /v1/current.json?key={apiKey}&q={lat},{lon}`
  - Response field parsed: `current.temp_c` -> Double
- Meteomatics: `GET /{dateTime}/t_2m:C/{lat},{lon}/json?model=mix`
  - Response field parsed: deeply nested `data[0].coordinates[0].dates[0].value` -> Double

Note: WeatherAPI free tier provides current weather; the date/time argument is ignored by the WeatherAPI adapter.

## Logging

On startup, WeatherService logs at INFO level which weather provider is configured and which client bean implementation is active, for example:

Weather provider configured: weatherapi.com (client bean: WeatherApiWeatherClient)

## Testing (added)

- Unit tests
  - WeatherApiWeatherClientSpec: verifies `temp_c` parsing and adapter logic.
  - MeteomaticsWeatherClientSpec: verifies nested JSON parsing to Double.
  - RegisterChecksActionSpec: verifies the recurring temperature check is scheduled using the configured cron expression.

- Integration tests
  - WeatherApiDotComClientIntegrationSpec: exercises the raw WeatherAPI HTTP client against the live API (skips when no API key present).
  - MeteomaticsClientIntegrationSpec: existing integration test retained.

How to run
- Unit tests: ./gradlew test
- Integration tests: ./gradlew integrationTest (requires environment variables as per README)

## Expected outcome

- It is possible to run the application and obtain temperature data from weatherapi.com or Meteomatics.
- All tests are passing.
- Usage documentation matches the implementation (README updated to document provider selection, defaults, and environment variables).

## Notes

- Use `.envrc` to store environment variables for local development if desired.
