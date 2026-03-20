#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$REPO_ROOT/backend"
ENV_FILE="$REPO_ROOT/.env"
WRAPPER_DIR="/tmp/loan-copilot-backend-wrapper"
MAVEN_REPO_LOCAL="$REPO_ROOT/.m2/repository"

if [ -f "$ENV_FILE" ]; then
  set -a
  # shellcheck disable=SC1090
  . <(sed 's/\r//' "$ENV_FILE")
  set +a
fi

mkdir -p "$MAVEN_REPO_LOCAL"
rm -rf "$WRAPPER_DIR"
mkdir -p "$WRAPPER_DIR"
ln -s "$BACKEND_DIR/.mvn" "$WRAPPER_DIR/.mvn"
tr -d '\r' < "$BACKEND_DIR/mvnw" > "$WRAPPER_DIR/mvnw"
chmod +x "$WRAPPER_DIR/mvnw"

cd "$WRAPPER_DIR"
export POSTGRES_HOST=localhost
exec ./mvnw -Dmaven.repo.local="$MAVEN_REPO_LOCAL" -f "$BACKEND_DIR/pom.xml" spring-boot:run
