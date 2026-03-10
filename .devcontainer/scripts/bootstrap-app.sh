#!/usr/bin/env bash

set -euo pipefail

echo "=== Codespaces Bootstrap: legacy-app ==="

APP_URL="http://tomcat:8080/legacy-app/login.do"

wait_for_http() {
  local name="$1"
  local url="$2"
  local retries="$3"

  echo "Waiting for ${name} at ${url} ..."
  for i in $(seq 1 "${retries}"); do
    if curl -fsS --max-time 3 "${url}" > /dev/null 2>&1; then
      echo "${name} is reachable."
      return 0
    fi
    sleep 3
  done

  echo "${name} did not become reachable in time."
  return 1
}

wait_for_mysql() {
  local retries=60
  echo "Waiting for MySQL (legacy-mysql:3306) ..."

  for i in $(seq 1 "${retries}"); do
    if timeout 2 bash -c "cat < /dev/null > /dev/tcp/legacy-mysql/3306" 2>/dev/null; then
      echo "MySQL port is reachable."
      return 0
    fi
    sleep 2
  done

  echo "MySQL port did not become reachable in time."
  return 1
}

echo "Java version:"
java -version || true
echo "Ant version:"
ant -version || true

wait_for_mysql

echo "Building WAR for Tomcat auto-deploy ..."
ant clean war

wait_for_http "Tomcat app" "${APP_URL}" 80

echo
echo "Bootstrap completed successfully."
echo "Application URL: http://localhost:8080/legacy-app/"
echo "Login URL:       http://localhost:8080/legacy-app/login.do"
echo "Default users:   admin/admin123, manager/manager123, clerk/clerk123"
