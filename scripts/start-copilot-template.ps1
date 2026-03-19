$ErrorActionPreference = "Stop"

function Write-Info {
    param([string]$Message)
    Write-Host "[start-copilot-template] $Message"
}

$repoRoot = Split-Path -Parent $PSScriptRoot
$backendLog = Join-Path $repoRoot "backend\backend.log"
$frontendLog = Join-Path $repoRoot "frontend\frontend.log"
$frontendErrLog = Join-Path $repoRoot "frontend\frontend.err.log"

Push-Location $repoRoot

try {
    if (Test-Path $backendLog) { Remove-Item $backendLog -Force }
    if (Test-Path $frontendLog) { Remove-Item $frontendLog -Force }
    if (Test-Path $frontendErrLog) { Remove-Item $frontendErrLog -Force }

    if ((Test-Path ".\mvnw.cmd") -and (Test-Path ".\pom.xml")) {
        Write-Info "Starting backend using root Maven reactor."
        Start-Process -FilePath "cmd.exe" `
            -ArgumentList "/c", "mvnw.cmd package spring-boot:test-run -pl langgraph4j-ag-ui-sdk" `
            -WorkingDirectory $repoRoot `
            -RedirectStandardOutput $backendLog `
            -RedirectStandardError $backendLog `
            -WindowStyle Hidden | Out-Null
    }
    elseif (Test-Path ".\backend\mvnw.cmd") {
        Write-Info "Root Maven reactor not found. Falling back to backend module start."
        Start-Process -FilePath "cmd.exe" `
            -ArgumentList "/c", ".\mvnw.cmd spring-boot:run" `
            -WorkingDirectory (Join-Path $repoRoot "backend") `
            -RedirectStandardOutput $backendLog `
            -RedirectStandardError $backendLog `
            -WindowStyle Hidden | Out-Null
    }
    else {
        Write-Warning "Backend start command not available."
    }

    if ((Test-Path ".\frontend\package.json") -and (Get-Command npm -ErrorAction SilentlyContinue)) {
        Write-Info "Starting frontend."
        Start-Process -FilePath "npm" `
            -ArgumentList "run", "dev" `
            -WorkingDirectory (Join-Path $repoRoot "frontend") `
            -RedirectStandardOutput $frontendLog `
            -RedirectStandardError $frontendErrLog `
            -WindowStyle Hidden | Out-Null
    }
    else {
        Write-Warning "Frontend package or npm not available."
    }

    Write-Info "Started processes. Backend log: $backendLog"
    Write-Info "Started processes. Frontend log: $frontendLog"
    Write-Info "App URL: http://localhost:3000"
}
finally {
    Pop-Location
}
