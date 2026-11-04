param([switch]$RequireTelegram)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$settings = @{}
$envPath = Join-Path $projectRoot '.env'
if (Test-Path -LiteralPath $envPath) {
  Get-Content -LiteralPath $envPath | ForEach-Object {
    if ($_ -match '^([A-Z_]+)=(.*)$') { $settings[$Matches[1]] = $Matches[2] }
  }
}
$problems = [System.Collections.Generic.List[string]]::new()
foreach ($key in @('ADMIN_PASSWORD', 'JWT_SECRET', 'DATABASE_PASSWORD', 'AI_BASE_URL', 'AI_API_KEY', 'AI_MODEL')) {
  if ([string]::IsNullOrWhiteSpace($settings[$key])) { $problems.Add("Missing $key") }
}
if ($settings['JWT_SECRET'] -and $settings['JWT_SECRET'].Length -lt 32) { $problems.Add('JWT_SECRET must contain at least 32 characters') }
if ($settings['DEMO_MODE'] -ne 'false') { $problems.Add('Set DEMO_MODE=false for a real publication') }
if ($settings['COLLECTION_ENABLED'] -ne 'true') { $problems.Add('Set COLLECTION_ENABLED=true for scheduled ingestion') }
if ($RequireTelegram) {
  foreach ($key in @('TELEGRAM_BOT_TOKEN', 'TELEGRAM_CHAT_ID')) {
    if ([string]::IsNullOrWhiteSpace($settings[$key])) { $problems.Add("Missing $key") }
  }
}
if ($problems.Count) {
  Write-Output 'Launch configuration is incomplete:'
  $problems | ForEach-Object { Write-Output " - $_" }
  exit 1
}
Write-Output 'Configuration checks passed. Verify provider connectivity, PostgreSQL restore, HTTPS and editorial review before launch.'
Write-Output 'No external requests or publishing were performed.'
