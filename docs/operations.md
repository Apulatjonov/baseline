# Baseline operations

## Local runtime

Run `npm run dev` and `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/run-backend.ps1` in separate terminals. The latter loads `.env`. Restart the backend after changing provider settings. Keep one backend instance per database; collector scheduling and Telegram locking are process-local.

## Credentials and launch

Set `AI_BASE_URL`, `AI_API_KEY`, and `AI_MODEL` to a compatible chat-completions provider supporting JSON-object responses. Optional fallback settings are independent. No provider or key is bundled. Credentials stay in `.env`, which is ignored by version control.

For Telegram, set `TELEGRAM_BOT_TOKEN` and `TELEGRAM_CHAT_ID` for your intended channel. Explicit sending is separate from web publication. An uncertain delivery is retained for operator inspection; never delete the record and resend without checking the channel. Telegram corrections are not synchronized automatically.

Set `DEMO_MODE=false` and `COLLECTION_ENABLED=true` for real operation. The launch-check script reports missing settings without printing their values. A passing configuration check does not prove provider connectivity or authorize publication.

Use PostgreSQL in Compose for deployment. Docker Desktop must be running for local Compose verification. Keep port 3000 behind an HTTPS reverse proxy on your chosen host. Do not expose PostgreSQL. Configure secrets on that host independently of local demo secrets. The current operator helper defaults to localhost; remote requests require HTTPS.

## Sources

Fed, ECB and BIS feeds were fetched and parsed successfully on 2026-10-04 (Asia/Tashkent): 20, 15 and 10 entries respectively. This is a point-in-time health check, not a guarantee of continued availability or a grant of republication rights. `scripts/check-feeds.ps1` repeats the check without publishing anything.

The registry now includes 20 configured feeds listed in `backend/src/main/resources/feeds.json`, each checked for parseable entries on October 4. The public source directory reports current collector health. Official discovery pages include [EIA feeds](https://www.eia.gov/tools/rssfeeds/), [Bank of Canada feeds](https://www.bankofcanada.ca/rss-feeds/), [Swiss National Bank feeds](https://www.snb.ch/en/services-events/digital-services/rss-calendar-feeds) and [Czech National Bank feeds](https://www.cnb.cz/en/general/rss/). The 14 remaining registry entries stay disabled. BLS and RBA returned HTTP 403 during local checks and were not enabled.

Fresh databases receive the configured feed defaults automatically. For an existing database, `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/import-feeds.ps1` explicitly applies and enables those defaults through the protected API; it replaces settings for matching IDs and audits the changes. Normal startup preserves operator settings. OpenAI and NVIDIA feeds are company announcements: they are evidence of issuer statements, not independent validation of commercial claims.

To add or replace a source, send authenticated `PUT /api/v1/admin/sources/{id}` with a full configuration:

```json
{
  "name": "Federal Reserve",
  "domain": "www.federalreserve.gov",
  "sourceType": "PRIMARY",
  "country": "US",
  "language": "en",
  "rssUrl": "https://www.federalreserve.gov/feeds/press_all.xml",
  "reliability": 0.99,
  "enabled": true,
  "pollMinutes": 15,
  "termsNotes": "Official release feed; preserve attribution and source links."
}
```

Source IDs use lowercase letters, digits and hyphens. Feed URLs must use HTTPS on the exact configured domain, without credentials or custom ports. Collection blocks private network addresses and redirects. Polling interval is 5–1440 minutes. Verify terms, entry dates, canonical links and topic coverage before enabling. Health counters are preserved through registry edits; edits are audited.

## Draft recovery

Failed initial AI drafts remain REVIEW and cannot be approved until synthesized or manually edited. Retry state survives a restart in `event_record.synthesis_attempts` and `synthesis_retry_at`. Up to ten due drafts are retried per scheduler pass, stopping after five failed attempts. Manual edits are the recovery path after exhaustion. A new source article can trigger synthesis for a review draft; do not treat this as a distributed worker or an immutable editorial lock.

Use `scripts/editor.ps1` for queue, private articles, audit, edit, approve, reject and explicit Telegram send. Check the event's sources and unsupported-number warnings before approving it. Numeric validation checks tokens, not semantic truth.

## Backup and acceptance

For local H2, stop the backend before copying `backend/data/signal.mv.db`; do not copy a live database file. A local pre-update backup is stored in the ignored `backups/` directory.

For PostgreSQL, use `pg_dump` with a custom-format archive and verify it by restoring into a separate database. Store backups outside the application host. A production restore drill has not yet been performed.

Record daily collected articles, failed sources, rejected drafts, approved publications, AI errors, editorial time and Telegram discrepancies for seven consecutive days. The specification requires 5–10 strong items daily and under 30 minutes/day of manual intervention. Passing unit tests does not satisfy that acceptance criterion.
