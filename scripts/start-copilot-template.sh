#!/usr/bin/env bash
set -euo pipefail

log() {
  printf '[start-copilot-template] %s\n' "$1"
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

BACKEND_LOG="$REPO_ROOT/backend/backend.log"
FRONTEND_LOG="$REPO_ROOT/frontend/frontend.log"
FRONTEND_ERR_LOG="$REPO_ROOT/frontend/frontend.err.log"

rm -f "$BACKEND_LOG" "$FRONTEND_LOG" "$FRONTEND_ERR_LOG"

cd "$REPO_ROOT"

if [ -f ./mvnw ] && [ -f ./pom.xml ]; then
  log "Starting backend using root Maven reactor."
  nohup ./mvnw package spring-boot:test-run -pl langgraph4j-ag-ui-sdk >"$BACKEND_LOG" 2>&1 &
elif [ -f ./backend/mvnw ]; then
  log "Root Maven reactor not found. Falling back to backend module start."
  (
    cd backend
    nohup ./mvnw spring-boot:run >"$BACKEND_LOG" 2>&1 &
  )
else
  log "Backend start command not available."
fi

if [ -f ./frontend/package.json ] && command -v npm >/dev/null 2>&1; then
  log "Starting frontend."
  (
    cd frontend
    nohup npm run dev >"$FRONTEND_LOG" 2>"$FRONTEND_ERR_LOG" &
  )
else
  log "Frontend package or npm not available."
fi

log "Started processes. Backend log: $BACKEND_LOG"
log "Started processes. Frontend log: $FRONTEND_LOG"
log "App URL: http://localhost:3000"
