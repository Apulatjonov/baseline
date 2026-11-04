# Baseline · Uzbek Economic Intelligence

A runnable first implementation of the supplied specification: an Uzbek-first React application and a Java 21 / Spring Boot 3 modular monolith. The working brand is **Baseline**; no domain or Telegram handle has been claimed.

## Run locally

Requirements: Node 22+, Java 21–25. Maven is downloaded by the PowerShell helper if absent. The local backend uses a persistent H2 database in PostgreSQL compatibility mode, so Docker is optional for development.

```powershell
npm ci
npm run dev
```

In a second terminal:

```powershell
if (!(Test-Path .env)) { Copy-Item .env.example .env }
# Edit .env if needed: ADMIN_PASSWORD and JWT_SECRET (at least 32 bytes).
.\scripts\run-backend.ps1
```

Open **http://127.0.0.1:5173**. API: **http://127.0.0.1:8080/api/v1**. Health: `/actuator/health`.

This workspace includes a gitignored `.env` with generated local-only secrets. These credentials protect internal editorial API operations only. The public site has no accounts, login, profiles, bookmarks or reader authentication. Never commit this file.

Without the backend, the public frontend shows a service error with a reload button; it never substitutes sample news during an outage. Demo articles appear only when the backend explicitly runs in demo mode. Without `ADMIN_PASSWORD`, internal editorial API authentication is disabled. Demo mode does not automatically collect sources, call an AI service, or publish to Telegram. The local demo persists in `backend/data/`. The helper uses `LOCAL_DATABASE_PASSWORD` (empty by default) for H2; `DATABASE_PASSWORD` is reserved for a configured database or Docker PostgreSQL. Environment variables set in your shell also work; the helper loads `.env`, whereas a direct Maven invocation does not.

On Linux/macOS:

```sh
export ADMIN_PASSWORD='choose-a-local-password'
export JWT_SECRET='replace-with-at-least-32-random-bytes'
cd backend
mvn spring-boot:run
```

## Implemented

- Public Uzbek news site with a minimal black-and-white editorial layout, restrained orange accents, horizontal section navigation, category and relevance filters, historical search, event detail dialogs, source links, morning briefing and methodology. No reader accounts or sign-in UI.
- 34-source registry with 20 configured RSS feeds covering central banks, trade, energy, research, fintech and AI company announcements. Remaining entries are disabled. Feed defaults are stored in `backend/src/main/resources/feeds.json`; existing databases can import them using `scripts/import-feeds.ps1`.
- Scheduled RSS polling, HTML cleanup, source health, per-source SHA-256 deduplication, cross-source title/time clustering, and importance scoring with the specification’s weights.
- A provider abstraction and configurable chat-completions adapter with two attempts per provider and optional fallback. File-versioned Uzbek prompt; generation audit records; unsupported-number checks. Every generated draft requires human approval.
- JWT login, ADMIN authority checks, bounded login/API request rates, private source-body access, optimistic database versioning, edit/reject/approve actions, publication source requirements, and revision history.
- Website publication and explicit Telegram send for approved non-demo events. A persisted attempt prevents blind duplicate sends when delivery is uncertain. No messages have been sent during development.
- Flyway schema migrations, JPA persistence, PostgreSQL deployment configuration, health checks and protected Prometheus metrics.

## Integrations

Set `AI_BASE_URL` (including `/v1`), `AI_API_KEY`, and `AI_MODEL` for a compatible provider. Optional fallback uses the corresponding `AI_FALLBACK_*` variables. The adapter appends `/chat/completions` and expects JSON-object response mode. Provider-specific adapters can implement `LanguageModelProvider`.

Set `TELEGRAM_BOT_TOKEN` and `TELEGRAM_CHAT_ID`; make the bot an administrator of the intended channel. Sending is an explicit authenticated internal API action, separate from web approval. Demo stories cannot be sent.

Set `COLLECTION_ENABLED=true` to enable scheduled collection (the scheduler wakes every minute and honors each source’s interval). The internal `POST /api/v1/admin/collect` endpoint also triggers collection. Verify feed permissions before enabling a source. AI API calls may incur provider charges. When AI is absent or fails, drafts remain review-only; an editor must provide an Uzbek summary.

Switch `DEMO_MODE=false` and use a fresh production database for real operation. Demo records are excluded from public APIs when this flag is false. The public design contains no fabricated market-price widgets.

## Editorial operation and launch checks

The private operator helper reads local credentials without displaying them. Readers never need credentials. These commands use a process-only execution-policy setting and do not change Windows policy:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/check-launch.ps1 -RequireTelegram
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/check-feeds.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/editor.ps1 -Action collect
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/editor.ps1 -Action queue
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/editor.ps1 -Action edit -EventId EVENT_ID -ChangesFile draft.json
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/editor.ps1 -Action approve -EventId EVENT_ID
```

`draft.json` accepts `title`, `summary`, `whyItMatters`, `uzbekistanImpact` and, for already published text, `correctionNote`. Review the private source articles and numbers before approval. `-Action telegram -EventId EVENT_ID` explicitly sends an approved real event to the configured channel; approval alone does not send a message.

Failed initial synthesis is retried from persisted draft state with exponential delay, at most five attempts and ten due drafts per pass. The scheduler runs recovery when collection is enabled; `-Action retry` processes due retries manually. Exhausted drafts require manual editing. This is a single-instance recovery mechanism, not a distributed queue. Rejected and published stories are not regenerated. Failed sources observe the polling interval rather than retrying every minute.

An authenticated `PUT /api/v1/admin/sources/{id}` can add or configure feeds without editing code. See [operator runbook](docs/operations.md) for configuration and deployment details.

## Docker / PostgreSQL

Set the required secrets in `.env`, then:

```sh
docker compose up --build -d
```

Open **http://127.0.0.1:3000**. PostgreSQL and the backend are on the private Compose network. The only published port binds to localhost. Add TLS and a reviewed production ingress before external deployment.

The optional `extended` profile declares Redis, RabbitMQ and MinIO for future integration; current application code does **not** use those services. Configure their passwords before using the profile. PostgreSQL includes pgvector but semantic embeddings are not implemented yet.

## Tests

```powershell
npm test
npm run build
.\scripts\run-backend.ps1 -Test
```

The tests cover filtering, relevance filters, authentication, permissions, draft privacy, approval, correction audit history, search, registry, scoring, numeric validation, deduplication, cross-source provenance and unsafe-draft publication blocking.

## Scope and remaining work

This is a working MVP foundation, not the entire multi-year platform or a production certification. See [implementation status](docs/implementation-status.md) for the exact boundaries and the supplied [technical specification](docs/specification.md) for the full roadmap.

No live AI generation, Telegram delivery, PostgreSQL container deployment, or seven-day unattended acceptance run has been verified without the required credentials/runtime. Source-specific legal access and feed health must be validated before public operation.
