# Tasks

What needs doing?

1. ~~Fix TemperatureEmitterSpec~~
2. ~~Fix capitalisation of measurement (should be "Temperature" and tags, should be "postcode").~~
3. ~~Set up an automated CI/CD process using GitHub actions~~
4. ~~CI/CD Setup https://github.com/marketplace/actions/git-semantic-version for versioning.~~
5. ~~CI/CD Push Docker image.~~
6. ~~Publish JUnit test results.~~
7. ~~Set up PR actions.~~
8. ~~Write a README for Dockerhub. Can this be pulled automatically from somewhere?~~
9. Get Docker native image working
10. ~~Make sure the GitHub repo gets tagged each time we publish a new release number.~~
11. Remove Micronaut's HTTP server? Or is this meaningfully useful for a healthcheck?
12. Fix issue whereby the app silently does nothing when unable to resolve DNS names
    * This happens in Docker when run in an internal network. For some reason no exception is thrown...
13. ~~Add a "provider" property, to identify which provider provided the temperature data.~~