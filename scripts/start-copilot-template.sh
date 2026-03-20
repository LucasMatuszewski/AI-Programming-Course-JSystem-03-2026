#!/usr/bin/env bash
set -euo pipefail

log() {
  printf '[start-copilot-template] %s\n' "$1"
}

ensure_docker_compose() {
  if command -v docker-compose >/dev/null 2>&1; then
    COMPOSE_CMD=(docker-compose)
    return
  fi

  if ! command -v docker >/dev/null 2>&1; then
    echo "Docker Compose is required to start PostgreSQL for the backend." >&2
    exit 1
  fi

  docker compose version >/dev/null 2>&1
  COMPOSE_CMD=(docker compose)
}

ensure_postgres() {
  log "Ensuring PostgreSQL container is running."
  (
    cd "$REPO_ROOT"
    "${COMPOSE_CMD[@]}" up -d postgres >/dev/null
  )

  local attempt container_id health
  for attempt in $(seq 1 24); do
    container_id="$(cd "$REPO_ROOT" && "${COMPOSE_CMD[@]}" ps -q postgres)"
    if [ -z "$container_id" ]; then
      sleep 2
      continue
    fi

    health="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$container_id")"
    if [ "$health" = "healthy" ] || [ "$health" = "running" ]; then
      log "PostgreSQL is ready."
      return
    fi

    sleep 2
  done

  echo "PostgreSQL did not become ready. Check the PostgreSQL container logs for details." >&2
  exit 1
}

ensure_java() {
  if command -v java >/dev/null 2>&1; then
    return
  fi

  local candidates=()
  [ -n "${JAVA_HOME:-}" ] && candidates+=("$JAVA_HOME")
  [ -n "${HOME:-}" ] && candidates+=("$HOME/.jdks")
  [ -n "${USERPROFILE:-}" ] && candidates+=("$USERPROFILE/.jdks")
  candidates+=("/d/lucas/.jdks")

  local candidate root
  for root in "${candidates[@]}"; do
    [ -e "$root" ] || continue

    if [ -x "$root/bin/java" ]; then
      export JAVA_HOME="$root"
      export PATH="$JAVA_HOME/bin:$PATH"
      log "Using JDK from $JAVA_HOME."
      return
    fi

    candidate="$(find "$root" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | sort -r | head -n 1 || true)"
    if [ -n "$candidate" ] && [ -x "$candidate/bin/java" ]; then
      export JAVA_HOME="$candidate"
      export PATH="$JAVA_HOME/bin:$PATH"
      log "Using JDK from $JAVA_HOME."
      return
    fi
  done

  echo "Java 21 is required. Set JAVA_HOME or install a JDK." >&2
  exit 1
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
FRONTEND_PORT="${PORT:-3000}"
BACKEND_PORT="${SERVER_PORT:-8080}"
LOGS_DIR="$REPO_ROOT/logs"

backend_pid=""
frontend_pid=""
COMPOSE_CMD=()

cleanup() {
  local exit_code=$?
  trap - EXIT INT TERM

  if [ -n "$frontend_pid" ] && kill -0 "$frontend_pid" 2>/dev/null; then
    log "Stopping frontend process tree."
    kill "$frontend_pid" 2>/dev/null || true
    wait "$frontend_pid" 2>/dev/null || true
  fi

  if [ -n "$backend_pid" ] && kill -0 "$backend_pid" 2>/dev/null; then
    log "Stopping backend process tree."
    kill "$backend_pid" 2>/dev/null || true
    wait "$backend_pid" 2>/dev/null || true
  fi

  exit "$exit_code"
}

trap cleanup EXIT INT TERM

cd "$REPO_ROOT"
ensure_java
ensure_docker_compose
ensure_postgres
mkdir -p "$LOGS_DIR"

log "Backend URL: http://localhost:$BACKEND_PORT"
log "Frontend URL: http://localhost:$FRONTEND_PORT"
log "Logs directory: $LOGS_DIR"
log "Press Ctrl+C to stop both processes."

if [ -f ./mvnw ] && [ -f ./pom.xml ]; then
  log "Starting backend using root Maven reactor."
  (
    cd "$REPO_ROOT"
    exec ./mvnw package spring-boot:test-run -pl langgraph4j-ag-ui-sdk \
      > >(sed -u 's/^/[BE] /') \
      2> >(sed -u 's/^/[BE] /' >&2)
  ) &
  backend_pid=$!
elif [ -f ./backend/mvnw ]; then
  log "Root Maven reactor not found. Falling back to backend module start."
  (
    cd "$REPO_ROOT/backend"
    exec ./mvnw clean spring-boot:run \
      > >(sed -u 's/^/[BE] /') \
      2> >(sed -u 's/^/[BE] /' >&2)
  ) &
  backend_pid=$!
else
  echo "Backend start command not available." >&2
  exit 1
fi

if [ -f ./frontend/package.json ] && command -v npm >/dev/null 2>&1; then
  log "Starting frontend."
  (
    cd "$REPO_ROOT/frontend"
    exec npm run dev \
      > >(sed -u 's/^/[FE] /') \
      2> >(sed -u 's/^/[FE] /' >&2)
  ) &
  frontend_pid=$!
else
  echo "Frontend package or npm not available." >&2
  exit 1
fi

wait -n "$backend_pid" "$frontend_pid"
