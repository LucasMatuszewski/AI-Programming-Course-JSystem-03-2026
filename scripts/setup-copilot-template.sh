#!/usr/bin/env bash
set -euo pipefail

log() {
  printf '[setup-copilot-template] %s\n' "$1"
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$REPO_ROOT"

command -v git >/dev/null 2>&1 || { echo "git is required." >&2; exit 1; }

log "Initializing AG-UI submodule."
git submodule update --init --remote

if [ ! -f .env ] && [ -f .env.example ]; then
  cp .env.example .env
  log "Created .env from .env.example."
fi

if [ -f ./mvnw ] && [ -f ./pom.xml ]; then
  log "Building root Maven reactor, including AG-UI community SDK."
  ./mvnw clean install -Dgpg.skip=true -Dmaven.javadoc.skip=true -Plocal
else
  log "Root Maven wrapper/pom not found yet. Skipping root Maven bootstrap."
fi

if [ -f ./frontend/package.json ] && command -v npm >/dev/null 2>&1; then
  log "Installing frontend dependencies."
  (cd frontend && npm install)
elif [ -f ./frontend/package.json ]; then
  log "npm is not available in PATH. Skipping frontend install."
fi

log "Setup finished."
