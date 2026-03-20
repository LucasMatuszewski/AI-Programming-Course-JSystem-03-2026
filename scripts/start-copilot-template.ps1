$ErrorActionPreference = "Stop"

function Write-Info {
    param([string]$Message)
    Write-Host "[start-copilot-template] $Message"
}

function Convert-ToWslPath {
    param([Parameter(Mandatory = $true)][string]$WindowsPath)

    $normalized = $WindowsPath -replace "\\", "/"
    if ($normalized -match "^([A-Za-z]):/(.*)$") {
        $drive = $matches[1].ToLower()
        $rest = $matches[2]
        return "/mnt/$drive/$rest"
    }

    throw "Cannot convert path to WSL format: $WindowsPath"
}

function Invoke-NativeCommand {
    param(
        [Parameter(Mandatory = $true)][string]$FilePath,
        [Parameter(Mandatory = $true)][string[]]$Arguments,
        [Parameter(Mandatory = $false)][string]$WorkingDirectory
    )

    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $FilePath
    $escapedArgs = $Arguments | ForEach-Object {
        if ($_ -match '[\s"]') { "`"$($_ -replace '"', '\"')`"" } else { $_ }
    }
    $startInfo.Arguments = $escapedArgs -join ' '
    if ($WorkingDirectory) {
        $startInfo.WorkingDirectory = $WorkingDirectory
    }
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true

    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $startInfo

    if (-not $process.Start()) {
        throw "Failed to start command: $FilePath"
    }

    $stdout = $process.StandardOutput.ReadToEnd()
    $stderr = $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    $output = ($stdout, $stderr | Where-Object { $_ }) -join [Environment]::NewLine

    if ($process.ExitCode -ne 0) {
        $message = $output.Trim()
        if (-not $message) {
            $message = "$FilePath exited with code $($process.ExitCode)."
        }
        throw $message
    }

    return $output.Trim()
}

function Get-ComposeCommand {
    if (Get-Command docker-compose -ErrorAction SilentlyContinue) {
        try {
            Invoke-NativeCommand -FilePath "docker-compose" -Arguments @("version") | Out-Null
            return @{
                FilePath = "docker-compose"
                Prefix = @()
            }
        }
        catch {
            # Fall back to docker compose below.
        }
    }

    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        throw "Docker Compose is required to start PostgreSQL for the backend."
    }

    Invoke-NativeCommand -FilePath "docker" -Arguments @("compose", "version") | Out-Null
    return @{
        FilePath = "docker"
        Prefix = @("compose")
    }
}

function Invoke-Compose {
    param(
        [Parameter(Mandatory = $true)][string]$RepoRoot,
        [Parameter(Mandatory = $true)][string[]]$Arguments
    )

    $composeCommand = Get-ComposeCommand
    Push-Location $RepoRoot
    try {
        $composeArguments = @($composeCommand.Prefix) + $Arguments
        return Invoke-NativeCommand -FilePath $composeCommand.FilePath -Arguments $composeArguments
    }
    finally {
        Pop-Location
    }
}

function Get-ContainerHealth {
    param([Parameter(Mandatory = $true)][string]$ContainerId)

    return Invoke-NativeCommand -FilePath "docker" -Arguments @(
        "inspect",
        "--format",
        "{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}",
        $ContainerId
    )
}

function Invoke-PostgresSetupScript {
    param([Parameter(Mandatory = $true)][string]$RepoRoot)

    $setupScript = Join-Path $RepoRoot "scripts\setup-postgres.ps1"
    if (-not (Test-Path $setupScript)) {
        throw "PostgreSQL setup script not found at $setupScript"
    }

    & $setupScript
}

function Ensure-PostgresReady {
    param([Parameter(Mandatory = $true)][string]$RepoRoot)

    Write-Info "Ensuring PostgreSQL container is running."
    try {
        Invoke-Compose -RepoRoot $RepoRoot -Arguments @("up", "-d", "postgres") | Out-Null
    }
    catch {
        Write-Info "Local Docker startup failed. Falling back to the WSL PostgreSQL setup."
        Invoke-PostgresSetupScript -RepoRoot $RepoRoot
        return @{
            BackendRuntime = "wsl"
        }
    }

    for ($attempt = 1; $attempt -le 24; $attempt++) {
        $containerId = Invoke-Compose -RepoRoot $RepoRoot -Arguments @("ps", "-q", "postgres")
        if (-not $containerId) {
            Start-Sleep -Seconds 2
            continue
        }

        $health = Get-ContainerHealth -ContainerId $containerId
        if ($health -eq "healthy" -or $health -eq "running") {
            Write-Info "PostgreSQL is ready."
            return @{
                BackendRuntime = "windows"
            }
        }

        Start-Sleep -Seconds 2
    }

    throw "PostgreSQL did not become ready. Check 'docker compose logs postgres' for details."
}

function Initialize-Java {
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) {
        $env:Path = "$(Join-Path $env:JAVA_HOME 'bin');$env:Path"
        return
    }

    if (Get-Command java -ErrorAction SilentlyContinue) {
        return
    }

    $candidates = @()
    if ($env:USERPROFILE) {
        $candidates += Join-Path $env:USERPROFILE ".jdks"
    }
    $candidates += "D:\lucas\.jdks"

    foreach ($candidateRoot in $candidates | Select-Object -Unique) {
        if (-not (Test-Path $candidateRoot)) {
            continue
        }

        $jdk = Get-ChildItem -Path $candidateRoot -Directory |
            Sort-Object Name -Descending |
            Select-Object -First 1

        if ($jdk -and (Test-Path (Join-Path $jdk.FullName "bin\java.exe"))) {
            $env:JAVA_HOME = $jdk.FullName
            $env:Path = "$(Join-Path $env:JAVA_HOME 'bin');$env:Path"
            Write-Info "Using JDK from $($env:JAVA_HOME)."
            return
        }
    }

    throw "Java 21 is required. Set JAVA_HOME or install a JDK."
}

function Start-ManagedProcess {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$FilePath,
        [Parameter(Mandatory = $true)][string[]]$Arguments,
        [Parameter(Mandatory = $true)][string]$WorkingDirectory
    )

    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $FilePath
    # ArgumentList (StringCollection) is .NET 5+ only; use Arguments string for PS 5.1 / .NET Framework compatibility.
    $escapedArgs = $Arguments | ForEach-Object {
        if ($_ -match '[\s"]') { "`"$($_ -replace '"', '\"')`"" } else { $_ }
    }
    $startInfo.Arguments = $escapedArgs -join ' '
    $startInfo.WorkingDirectory = $WorkingDirectory
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true

    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $startInfo
    $process.EnableRaisingEvents = $true

    $stdoutEvent = Register-ObjectEvent -InputObject $process -EventName OutputDataReceived -Action {
        if ($EventArgs.Data) {
            Write-Host "[$($Event.MessageData)] $($EventArgs.Data)"
        }
    } -MessageData $Name

    $stderrEvent = Register-ObjectEvent -InputObject $process -EventName ErrorDataReceived -Action {
        if ($EventArgs.Data) {
            Write-Host "[$($Event.MessageData)] $($EventArgs.Data)"
        }
    } -MessageData $Name

    if (-not $process.Start()) {
        throw "Failed to start $Name process."
    }

    $process.BeginOutputReadLine()
    $process.BeginErrorReadLine()

    return @{
        Name = $Name
        Process = $process
        StdoutEvent = $stdoutEvent
        StderrEvent = $stderrEvent
    }
}

function Stop-ManagedProcess {
    param($ManagedProcess)

    if (-not $ManagedProcess) {
        return
    }

    foreach ($eventSubscription in @($ManagedProcess.StdoutEvent, $ManagedProcess.StderrEvent)) {
        if ($eventSubscription) {
            Unregister-Event -SourceIdentifier $eventSubscription.Name -ErrorAction SilentlyContinue
            Remove-Job -Id $eventSubscription.Id -Force -ErrorAction SilentlyContinue
        }
    }

    $process = $ManagedProcess.Process
    if ($process -and -not $process.HasExited) {
        Write-Info "Stopping $($ManagedProcess.Name) process tree (PID $($process.Id))."
        taskkill /PID $process.Id /T /F | Out-Null
        $process.WaitForExit(5000) | Out-Null
    }

    if ($process) {
        $process.Dispose()
    }
}

function Start-CopilotTemplate {
    $repoRoot = Split-Path -Parent $PSScriptRoot
    $frontendPort = if ($env:PORT) { $env:PORT } else { "3000" }
    $backendPort = if ($env:SERVER_PORT) { $env:SERVER_PORT } else { "8080" }
    $logsDir = Join-Path $repoRoot "logs"

    $backendProcess = $null
    $frontendProcess = $null

    Push-Location $repoRoot
    try {
        Initialize-Java
        $postgresSetup = Ensure-PostgresReady -RepoRoot $repoRoot
        New-Item -ItemType Directory -Force -Path $logsDir | Out-Null

        Write-Info "Backend URL: http://localhost:$backendPort"
        Write-Info "Frontend URL: http://localhost:$frontendPort"
        Write-Info "Logs directory: $logsDir"
        Write-Info "Press Ctrl+C to stop both processes."

        if ($postgresSetup.BackendRuntime -eq "wsl") {
            $wslRepoRoot = Convert-ToWslPath -WindowsPath $repoRoot
            Write-Info "Starting backend inside WSL so it can use the WSL PostgreSQL runtime."
            $backendProcess = Start-ManagedProcess `
                -Name "BE" `
                -FilePath "wsl.exe" `
                -Arguments @("-d", "Ubuntu", "--", "bash", "-lc", "cd '$wslRepoRoot' && bash './scripts/start-backend-wsl.sh'") `
                -WorkingDirectory $repoRoot
        }
        elseif ((Test-Path ".\mvnw.cmd") -and (Test-Path ".\pom.xml")) {
            Write-Info "Starting backend using root Maven reactor."
            $backendProcess = Start-ManagedProcess `
                -Name "BE" `
                -FilePath "cmd.exe" `
                -Arguments @("/c", "mvnw.cmd", "package", "spring-boot:test-run", "-pl", "langgraph4j-ag-ui-sdk") `
                -WorkingDirectory $repoRoot
        }
        elseif (Test-Path ".\backend\mvnw.cmd") {
            Write-Info "Root Maven reactor not found. Falling back to backend module start."
            $backendProcess = Start-ManagedProcess `
                -Name "BE" `
                -FilePath "cmd.exe" `
                -Arguments @("/c", ".\mvnw.cmd", "clean", "spring-boot:run") `
                -WorkingDirectory (Join-Path $repoRoot "backend")
        }
        else {
            throw "Backend start command not available."
        }

        if ((Test-Path ".\frontend\package.json") -and (Get-Command npm.cmd -ErrorAction SilentlyContinue)) {
            Write-Info "Starting frontend."
            $frontendProcess = Start-ManagedProcess `
                -Name "FE" `
                -FilePath "cmd.exe" `
                -Arguments @("/c", "npm", "run", "dev") `
                -WorkingDirectory (Join-Path $repoRoot "frontend")
        }
        else {
            throw "Frontend package or npm.cmd not available."
        }

        while ($true) {
            if ($backendProcess.Process.HasExited) {
                throw "Backend process exited with code $($backendProcess.Process.ExitCode)."
            }
            if ($frontendProcess.Process.HasExited) {
                throw "Frontend process exited with code $($frontendProcess.Process.ExitCode)."
            }
            Start-Sleep -Milliseconds 500
        }
    }
    finally {
        Stop-ManagedProcess $frontendProcess
        Stop-ManagedProcess $backendProcess
        Pop-Location
    }
}

if ($MyInvocation.InvocationName -ne '.') {
    Start-CopilotTemplate
}
