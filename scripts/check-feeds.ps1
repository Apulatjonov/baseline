param([string[]]$Feeds)
$ErrorActionPreference = 'Stop'
if (!$Feeds) {
  $registry = Join-Path (Split-Path $PSScriptRoot -Parent) 'backend/src/main/resources/feeds.json'
  $Feeds = @(Get-Content -Raw -LiteralPath $registry | ConvertFrom-Json | ForEach-Object { $_.rssUrl })
}
$failed = $false
foreach ($url in $feeds) {
  try {
    $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 25
    $document = [System.Xml.XmlDocument]::new()
    $document.XmlResolver = $null
    $readerSettings = [System.Xml.XmlReaderSettings]::new()
    $readerSettings.DtdProcessing = [System.Xml.DtdProcessing]::Prohibit
    $response.RawContentStream.Position = 0
    $reader = [System.Xml.XmlReader]::Create($response.RawContentStream, $readerSettings)
    try { $document.Load($reader) } finally { $reader.Dispose() }
    $items = $document.SelectNodes('//*[local-name()="item" or local-name()="entry"]')
    if ($items.Count -eq 0) { throw 'No RSS or Atom entries' }
    [pscustomobject]@{url=$url;http=$response.StatusCode;items=$items.Count;checkedAt=[DateTime]::UtcNow.ToString('o')} | ConvertTo-Json -Compress
  } catch {
    $failed = $true
    [pscustomobject]@{url=$url;error=$_.Exception.Message} | ConvertTo-Json -Compress
  }
}
if ($failed) { exit 1 }
