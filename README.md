# Continuous integration server

This is an assignment for the course [DD2480 Software Engineering Fundamentals](https://www.kth.se/student/kurser/kurs/DD2480?l=en) that's given at KTH.

The task is to create a continuous integration server, the Canvas page can be found [here](https://canvas.kth.se/courses/37918/assignments/235346).

## Building, testing, running and documentation

### Building

`./gradlew build`

### Testing

`./gradlew test`

### Running

`./gradlew run`

#### Web server

The CI also provides a web interface as a way for users to see the build logs related to different builds. Logs are
automatically placed by the server in the `pipeline/logs/<project_name>/` directory of the project root folder (i.e.
alongside `app/`, `README.md` etc.), and can be viewed by going to `<server-instance>/logs`. For example, running the
server with its default configuration, and accessing its logs through a browser on the same machine, would be done by
going to `http://localhost:8080/logs`.

All log file names are of the format `YYYY-MM-DD.HH:MM:SS.SHA.STATUS.log`, where `SHA` is the head commit's hash (SHA),
and STATUS is the status of the pipeline result.

### Documentation

`./gradlew javadoc`

The generated documentation can be browsed from `./app/build/docs/javadoc/index.html`

## Essence - Team

Our team assessment and documentation in accordance to the Essence standard is [documented here](./Essence.md).

## Statement of contributions

### Daniel Williams

* Sending HTTP POST requests
* Connect commit statuses to web frontend
* Set up an environment that the server could run in 24/7

### Didrik Munther

* Frontend build log
* Command executer
* Pulling repository
* Parse push event from github

### Hannah Burak

### Håvard Alstadheim

* Most of the javadoc and related work for the doc requirement
* Fix correct stages in pipeline, and fix scopes in command executer
* Add external CI server to repository for live comparison with our own
* Bureaucracy: documenting issues, mapping issues to assignment ("stakeholder") requirements, catching typos
* Some inputs on gradle functionality, server administration, github API

### Pontus Söderlund
