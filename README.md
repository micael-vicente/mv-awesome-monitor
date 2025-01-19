# 🤖 mv-monitor
Configure monitoring jobs and check their results via RESTful APIs.
<br>
The jobs are scheduled using `spring-quartz` and the monitoring is triggered 
based on a cron expression.
<br><br>
Currently, only supports `HTTP_AVAILABILITY` (checks if given URL responds in the 2xx range).

## 🧾 Table of contents
1. [Requirements](#-requirements)
2. [How to build](#-how-to-build)
3. [How to run](#-how-to-run)
4. [How to use](#-how-to-use)
   1. [Create job](#create-a-new-job)
   2. [View jobs](#view-all-jobs)
   3. [Delete job](#delete-a-job)
   4. [View results](#view-a-jobs-results)
5. [How to extend](#-how-to-extend)
6. [Future iterations](#-future-iterations)

## 📚 Requirements
- Maven (mvn wrapper provided)
- Docker
- Java 21 (preferably [temurin](https://adoptium.net/))

## 🏗️ How to build
All the commands are to be executed at the **root of the project**.
### Application
#### On Windows
```shell
.\mvnw clean install
```

#### On Linux
```shell
./mvnw clean install
```

### Docker image
```shell
docker build -t mv-monitor-app:v1 .
```

## ⏯️ How to run
### Docker compose - infra + app
Starts required infrastructure and the application according to the config inside the docker compose. 
```shell
docker-compose -f docker/docker-compose.yml --profile full-app up -d
```

### Docker compose - infra only
Starts required infrastructure ony. Useful when debugging or running through IDE.
```shell
docker-compose -f docker/docker-compose.yml --profile infra up -d
```
## 🧑‍💻 How to use
Endpoints can be operated through a swagger ui. Usually available at http://localhost:8085/swagger-ui/index.html#/

### Create a new Job

If the job is created with `enabled` set as true, the job won't be scheduled until it is updated to true.

`POST /api/v1/jobs`

```json
{
  "address": "https://google.com",
  "cronExpression": "*/1 * * ? * *",
  "enabled": true,
  "monitoringType": "HTTP_AVAILABILITY"
}
```
### View all jobs

Accepts parameters `size` and `page`, regarding pagination.

`GET /api/v1/jobs`

### Update a job
Currently, only the field `enabled` of the job can be updated.
This allows the job to be enabled and disabled.

`PATCH /api/v1/jobs/:id`

```json
{
    "enabled": false
}
```
### Delete a job
A hard delete that deletes both the job and any result of its execution. 

`DELETE /api/v1/jobs/:id`

### View a job's results

`GET /api/v1/jobs/:id/results`

Accepts parameters `size` and `page`, regarding pagination.

## 👽 How to extend

### New types of MonitorJob

Quartz provides an interface `Job` to allow creating new Job types.
**mv-monitor** extends `Job` with `MonitorJob` which uses `MonitoringType` as the type discriminator.
<br>
The proper way to extend is by adding the new monitoring type to the `MonitoringType` enum
and then implement the `MonitorJob` interface, returning the new `MonitoringType` with `monitorType()` method.
<br>
If the monitoring you want to do is on top of HTTP, you can extend `HttpMonitorJob` which already offers the tools to request and measure time.

### New types of ResultSaver

Means are also provided to abstract how/where the results of a monitoring job are saved.
<br>
This extension of functionality can be done by implementing `MonitoringResultSaver` and, optionally if you want to maintain the 
Job Result API, `MonitoringJobResultService`.
<br>
These beans need to either be discriminated and selected during job creation or they can use a `@ConditionalOnProperty`.

## 🔮 Future iterations
- Add new types of `MonitorJob` such as the `HttpReadinessMonitorJob`
- Add support for using both cron expressions and intervals in seconds when scheduling
- Add support for new `ResultSaver` types
- Decouple Results from Jobs