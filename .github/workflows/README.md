# GitHub Actions Workflows

This directory contains GitHub Actions workflows for the ToDo-AI project.

## Workflows

### build-and-test.yml
**Trigger:** Push to `main` or `develop` branches, Pull Requests

**Actions:**
- Checks out the code
- Sets up JDK 17
- Runs `./gradlew build` to compile and build
- Runs `./gradlew test` to execute tests
- Uploads test results as artifacts
- Publishes test reports to GitHub
- Uploads build reports
- Runs code quality checks with `./gradlew check`

**Artifacts:**
- `test-results-17`: JUnit XML test results
- `build-reports-17`: Build reports including test summaries

### build-jar.yml
**Trigger:** Push to `main` or `develop` branches, Git tags (v*)

**Actions:**
- Checks out the code
- Sets up JDK 17
- Builds executable JAR with `./gradlew bootJar`
- Uploads JAR artifact

**Artifacts:**
- `application-jar`: Compiled Spring Boot JAR file

### ci-coverage.yml
**Trigger:** Push to `main` branch, Pull Requests to `main`

**Actions:**
- Checks out the code
- Sets up JDK 17
- Builds project with `./gradlew clean build`
- Runs tests with `./gradlew test`
- Uploads test results

**Artifacts:**
- `test-results`: JUnit test results

## Running Workflows Locally

You can test workflows locally using [act](https://github.com/nektos/act):

```bash
# Install act
brew install act

# Run the build-and-test workflow
act push -j build

# Run against pull_request event
act pull_request -j build

# Run specific workflow
act -l  # List all workflows
```

## Requirements

- Java 17
- Gradle (wrapper included)

## Environment Setup

The workflows automatically handle:
- JDK setup with Temurin distribution
- Gradle caching for faster builds
- Required permissions for checks and artifacts

## Status Badges

Add to your README.md:

```markdown
[![Build and Test](https://github.com/USERNAME/ToDo-AI/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/USERNAME/ToDo-AI/actions/workflows/build-and-test.yml)

[![CI with Code Coverage](https://github.com/USERNAME/ToDo-AI/actions/workflows/ci-coverage.yml/badge.svg)](https://github.com/USERNAME/ToDo-AI/actions/workflows/ci-coverage.yml)

[![Build JAR](https://github.com/USERNAME/ToDo-AI/actions/workflows/build-jar.yml/badge.svg)](https://github.com/USERNAME/ToDo-AI/actions/workflows/build-jar.yml)
```

