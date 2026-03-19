$ErrorActionPreference = "Stop"

function Write-Info {
    param([string]$Message)
    Write-Host "[setup-copilot-template] $Message"
}

$repoRoot = Split-Path -Parent $PSScriptRoot
Push-Location $repoRoot

try {
    if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
        throw "git is required."
    }

    Write-Info "Initializing AG-UI submodule."
    git submodule update --init --remote

    if (-not (Test-Path ".env") -and (Test-Path ".env.example")) {
        Copy-Item ".env.example" ".env"
        Write-Info "Created .env from .env.example."
    }

    if ((Test-Path ".\mvnw.cmd") -and (Test-Path ".\pom.xml")) {
        Write-Info "Building root Maven reactor, including AG-UI community SDK."
        cmd /c "mvnw.cmd clean install -Dgpg.skip=true -Dmaven.javadoc.skip=true -Plocal"
    }
    else {
        Write-Warning "Root Maven wrapper/pom not found yet. Skipping root Maven bootstrap."
    }

    if ((Test-Path ".\frontend\package.json") -and (Get-Command npm -ErrorAction SilentlyContinue)) {
        Write-Info "Installing frontend dependencies."
        Push-Location ".\frontend"
        try {
            npm install
        }
        finally {
            Pop-Location
        }
    }
    elseif (Test-Path ".\frontend\package.json") {
        Write-Warning "npm is not available in PATH. Skipping frontend install."
    }

    Write-Info "Setup finished."
}
finally {
    Pop-Location
}
