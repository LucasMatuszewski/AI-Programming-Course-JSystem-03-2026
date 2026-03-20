$scriptPath = Join-Path $PSScriptRoot "..\start-copilot-template.ps1"
. $scriptPath

Describe "Get-ComposeCommand" {
    It "prefers docker-compose when that command is available" {
        Mock Get-Command {
            param($Name)

            if ($Name -eq "docker-compose") {
                return @{ Name = "docker-compose" }
            }

            return $null
        }

        Mock Invoke-NativeCommand { "Docker Compose version v5.0.1" } -ParameterFilter {
            $FilePath -eq "docker-compose"
        }

        $command = Get-ComposeCommand

        $command.FilePath | Should Be "docker-compose"
        $command.Prefix.Count | Should Be 0
        Assert-MockCalled Invoke-NativeCommand -Times 1 -Exactly -Scope It -ParameterFilter {
            $FilePath -eq "docker-compose"
        }
    }
}

Describe "Ensure-PostgresReady" {
    It "falls back to the repository WSL postgres setup when local docker startup fails" {
        Mock Invoke-Compose { throw "compose failed" }
        Mock Invoke-PostgresSetupScript {}
        Mock Start-Sleep {}

        $result = Ensure-PostgresReady -RepoRoot "C:\repo"

        $result.BackendRuntime | Should Be "wsl"
        Assert-MockCalled Invoke-PostgresSetupScript -Times 1 -Exactly -Scope It
    }

    It "starts postgres through docker compose and waits until the container is healthy" {
        $script:composeCalls = @()
        $script:psCalls = 0

        Mock Invoke-Compose {
            param($RepoRoot, $Arguments)

            $script:composeCalls += ,([string]::Join(" ", $Arguments))
            if ($Arguments[0] -eq "ps") {
                $script:psCalls++
                if ($script:psCalls -eq 1) {
                    return ""
                }
                return "container-123"
            }

            return ""
        }

        Mock Get-ContainerHealth { "healthy" }
        Mock Start-Sleep {}

        $result = Ensure-PostgresReady -RepoRoot "C:\repo"

        $result.BackendRuntime | Should Be "windows"
        $script:composeCalls[0] | Should Be "up -d postgres"
        $script:composeCalls[1] | Should Be "ps -q postgres"
        $script:composeCalls[2] | Should Be "ps -q postgres"
        Assert-MockCalled Get-ContainerHealth -Times 1 -Exactly -Scope It
    }

    It "throws when postgres never becomes healthy" {
        Mock Invoke-Compose {
            param($RepoRoot, $Arguments)

            if ($Arguments[0] -eq "ps") {
                return "container-123"
            }

            return ""
        }

        Mock Get-ContainerHealth { "starting" }
        Mock Start-Sleep {}

        { Ensure-PostgresReady -RepoRoot "C:\repo" } | Should Throw
        Assert-MockCalled Get-ContainerHealth -Times 24 -Exactly -Scope It
    }
}
