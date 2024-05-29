# Load Generator Adapter
Service for providing convenient API for working with several load-generation engines, such as Yandex-Tank, etc.

## Setup
To run server on local machine it is recommended to use [docker compose](docker-compose.yaml)

```Bash
docker-compose up
```

To build new docker image of load-generator-adapter:
```Bash
gradle -DproductionMode=true vaadinBuildFrontend build
docker build --tag edikgoose/load-generator-adapter:latest .
```

Services:
1. **Yandex tank core**
   
    Web server of [Yandex tank](https://github.com/yandex/yandex-tank) for load generation.
    The docker image generated using this [fork](https://github.com/edikgoose/yandex-tank-api)

2. **Influx**

   Time series database. Used by Yandex tank to write metrics of load test

3. **Grafana**

    Used to visualize load test results. For each test service push new dashboard 
    that takes data from InfluxDB

4. **PostgreSQL**
    
    Used to store meta information about load tests (such as grafana dashboard url, title, etc.)

5. **Load Generation Adapter**

    The service itself

6. **Web-server**
    
    Simple hello-world web-server with one endpoint: `/get-hello`

    Used to test service functionality

## Use
To check API of the service go to `http://localhost:8087/swagger-ui/index.html` endpoint

For method /run:

| Param Name              | Description                     | Example                              |
|-------------------------|---------------------------------|--------------------------------------|
| **title**               | Name of the test                | Test my awesome service with 15k rps |
| **hostName**            | Host name of the target service | web-server                           |
| **port**                | Port of the target service      | 8080                                 |
| **uris**                | URIs of the target endpoints    | ["get-hello", "/get-hi"]             |
| **loadGeneratorEngine** | Type of the load generator      | YANDEX_TANK                          |
| **loadScheme**          | Load generation scheme          | line(1, 15, 1m)                      |

For load scheme it's available following type:
1. **step (a,b,step,dur)** makes stepped load, where a,b are start/end load values, step - increment value, dur - step duration.
Examples:
    - step(25, 5, 5, 60) - stepped load from 25 to 5 rps, with 5 rps steps, step duration 60s.
    - step(5, 25, 5, 60) - stepped load from 5 to 25 rps, with 5 rps steps, step duration 60s

2. **line (a,b,dur)** makes linear load, where a,b are start/end load, dur - the time for linear load increase from a to b.
Examples:
    - line(10, 1, 10m) - linear load from 10 to 1 rps, duration - 10 minutes
    - line(1, 10, 10m) - linear load from 1 to 10 rps, duration - 10 minutes

3. **const (load,dur)** makes constant load. load - rps amount, dur - load duration.
Examples:
    - const(10,10m) - constant load for 10 rps for 10 minutes.
    - const(0, 10) - 0 rps for 10 seconds, in fact 10s pause in a test.

## Service env variables:

| Param Name               | Description                       | Example                                                               |
|--------------------------|-----------------------------------|-----------------------------------------------------------------------|
| **DB_URL**               | URL to Psql DB                    | jdbc:postgresql://load-generator-postgres:5432/load_generator_adapter |
| **DB_USERNAME**          | Username of the Psql              | postgres                                                              |
| **DB_PASSWORD**          | Password of the Psql              | password                                                              |
| **YANDEX_TANK_BASE_URL** | URL to yandex tank load generator | http://tank-server:8888                                               |
| **GRAFANA_BASE_URL**     | URL to Grafana                    | http://grafana:3000                                                   |
| **GRAFANA_USERNAME**     | Grafana admin username            | admin                                                                 |
| **GRAFANA_PASSWORD**     | Grafana admin passowrd            | admin                                                                 |