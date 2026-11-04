param([string]$BaseUrl = 'http://127.0.0.1:8080')
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$uri = [uri]$BaseUrl
if ($uri.Scheme -ne 'https' -and !($uri.Scheme -eq 'http' -and $uri.IsLoopback)) { throw 'Use HTTPS for a remote server.' }
$settings = @{}
Get-Content -LiteralPath (Join-Path $projectRoot '.env') | ForEach-Object {
  if ($_ -match '^([A-Z_]+)=(.*)$') { $settings[$Matches[1]] = $Matches[2] }
}
$base = $BaseUrl.TrimEnd('/') + '/api/v1'
$login = Invoke-RestMethod "$base/auth/login" -Method Post -ContentType 'application/json' -Body (@{username=$settings['ADMIN_USER'];password=$settings['ADMIN_PASSWORD']} | ConvertTo-Json)
$headers = @{Authorization="Bearer $($login.token)"}
$feeds = Get-Content -Raw -Encoding UTF8 -LiteralPath (Join-Path $projectRoot 'backend/src/main/resources/feeds.json') | ConvertFrom-Json
foreach ($feed in $feeds) {
  $kind = 'PRIMARY'
  if ($feed.sourceType) { $kind = $feed.sourceType }
  $reliability = 0.95
  if ($null -ne $feed.reliability) { $reliability = $feed.reliability }
  $config = @{
    name=$feed.name; domain=([uri]$feed.rssUrl).Host; country=$feed.country; language=$feed.language
    rssUrl=$feed.rssUrl; sourceType=$kind; reliability=$reliability; enabled=$true; pollMinutes=30
    termsNotes='RSS metadata and short excerpts for editorial review; retain attribution and source links. Company announcements are issuer claims, not independent verification.'
  } | ConvertTo-Json
  $null = Invoke-RestMethod "$base/admin/sources/$($feed.id)" -Method Put -Headers $headers -ContentType 'application/json; charset=utf-8' -Body ([Text.Encoding]::UTF8.GetBytes($config))
  Write-Output "Configured $($feed.id)"
}
Write-Output 'Registry imported. Scheduled collection still follows COLLECTION_ENABLED; no stories were published.'
