param(
  [ValidateSet('queue','collect','retry','articles','audit','edit','approve','reject','telegram')]
  [string]$Action = 'queue',
  [string]$EventId,
  [string]$ChangesFile,
  [string]$BaseUrl = 'http://127.0.0.1:8080'
)
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$settings = @{}
Get-Content -LiteralPath (Join-Path (Split-Path $PSScriptRoot -Parent) '.env') | ForEach-Object {
  if ($_ -match '^([A-Z_]+)=(.*)$') { $settings[$Matches[1]] = $Matches[2] }
}
$uri = [uri]$BaseUrl
if ($uri.Scheme -ne 'https' -and !($uri.Scheme -eq 'http' -and $uri.IsLoopback)) {
  throw 'Use HTTPS for a remote server.'
}
$base = $BaseUrl.TrimEnd('/') + '/api/v1'
$loginBody = @{username=$settings['ADMIN_USER'];password=$settings['ADMIN_PASSWORD']} | ConvertTo-Json
$login = Invoke-RestMethod "$base/auth/login" -Method Post -ContentType 'application/json' -Body $loginBody
$headers = @{Authorization="Bearer $($login.token)"}
$method = 'Get'
$body = $null
switch ($Action) {
  'queue' { $path = '/admin/events' }
  'articles' { $path = '/admin/articles' }
  'collect' { $path = '/admin/collect'; $method = 'Post' }
  'retry' { $path = '/admin/synthesis/retry'; $method = 'Post' }
  default {
    if (!$EventId -or $EventId -notmatch '^[a-zA-Z0-9-]+$') { throw 'A valid EventId is required.' }
    $path = "/admin/events/$EventId/$Action"
    if ($Action -ne 'audit') { $method = 'Post' }
    if ($Action -eq 'telegram') { $path = "/admin/telegram/$EventId" }
    if ($Action -eq 'edit') {
      if (!$ChangesFile) { throw 'Provide ChangesFile containing the edited fields as a JSON object.' }
      $body = Get-Content -Raw -LiteralPath $ChangesFile
      $null = $body | ConvertFrom-Json
    }
  }
}
$request = @{Uri="$base$path";Method=$method;Headers=$headers}
if ($null -ne $body) { $request.ContentType='application/json; charset=utf-8'; $request.Body=[System.Text.Encoding]::UTF8.GetBytes($body) }
$request.UseBasicParsing = $true
$response = Invoke-WebRequest @request
[System.Text.Encoding]::UTF8.GetString($response.RawContentStream.ToArray())
