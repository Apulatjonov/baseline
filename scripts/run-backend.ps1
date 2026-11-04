param([switch]$Test)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
Set-Location $projectRoot
if (Test-Path '.env') {
  Get-Content '.env' | ForEach-Object {
    if ($_ -match '^([A-Z_]+)=(.*)$') { [Environment]::SetEnvironmentVariable($Matches[1],$Matches[2],'Process') }
  }
}
if (!$env:DATABASE_URL) {
  # Compose's PostgreSQL secret must not change the local H2 database password.
  $env:DATABASE_USER = 'sa'
  $env:DATABASE_PASSWORD = $env:LOCAL_DATABASE_PASSWORD
}
if (Test-Path 'C:\Program Files\Java\jdk-25') { $env:JAVA_HOME = 'C:\Program Files\Java\jdk-25' }
$mavenPath = Join-Path $projectRoot '.tools/apache-maven-3.9.11/bin/mvn.cmd'
if (!(Test-Path $mavenPath)) {
  New-Item -ItemType Directory -Force '.tools' | Out-Null
  Invoke-WebRequest 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.11/apache-maven-3.9.11-bin.zip' -OutFile '.tools/maven.zip'
  Expand-Archive '.tools/maven.zip' -DestinationPath '.tools' -Force
}
if ($Test) { & $mavenPath -f backend/pom.xml test } else { & $mavenPath -f backend/pom.xml spring-boot:run }
exit $LASTEXITCODE
