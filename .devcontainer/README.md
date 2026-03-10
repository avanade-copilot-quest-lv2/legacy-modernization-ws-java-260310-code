# Dev Container

Java 1.5 development environment with Tomcat, MySQL, and phpMyAdmin.

## Setup

1. Download `jdk-1_5_0_22-linux-amd64-rpm.bin` from [Oracle Java Archive](https://www.oracle.com/java/technologies/java-archive-javase5-downloads.html)
2. Place it in this `.devcontainer/` directory
3. Open in VS Code or Codespaces → "Reopen in Container"

The dev container now uses a single Compose file (`compose.dev.yaml`) and starts all required services automatically:
- `java5-dev` (build container)
- `mysql`
- `tomcat`
- `phpmyadmin`

## Services

| Service | URL |
|---------|-----|
| Application | http://localhost:8080/legacy-app/ (or forwarded port in Codespaces) |
| phpMyAdmin | http://localhost:8082/ (or forwarded port in Codespaces) |
| MySQL | localhost:3306 |

## Build

```bash
ant clean build
```
