# Implementation status

## Working vertical slice

RSS metadata → sanitized short source excerpt → persisted article → exact per-source deduplication → cross-source title/time cluster → weighted score → configured AI synthesis or manual-review draft → numeric check → human edit/approval → public searchable event → explicit Telegram attempt.

The implementation uses Spring MVC and blocking JPA, rather than mixing WebFlux with blocking database operations. Collection runs as a single-process synchronized scheduled task. Local development uses H2; deployment targets PostgreSQL. The source snapshot is private, and public APIs expose source names and links only.

## Deliberate first-version limits

| Area                   | Current behavior                                                                     | Remaining work                                                                                    |
| ---------------------- | ------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------- |
| Sources                | 34 registry entries; 20 configured feeds; protected add/edit API and audited import | Ongoing feed health and terms review; regional source expansion |
| Clustering             | Title-token Jaccard similarity > 0.65, publication proximity under 24h               | Embeddings, named-entity overlap, merge/split review, pgvector indexing                           |
| Scoring                | Specification weights; source reliability plus initial heuristic values              | Model-derived factor scores, configurable weights, novelty calibration                            |
| Relevance              | Editorially supplied or AI-generated conditional explanation                         | Structured transmission ontology and calibrated relevance scoring                                 |
| AI                     | Compatible provider interface, retry/fallback, prompt file, audit output and latency | Native provider adapters, token/cost accounting, provider-specific pricing, prompt management UI  |
| Validation             | Exact numeric token check and human gate                                             | Passage-level claim attribution, contradiction detection, multilingual decimal/unit normalization |
| Collection reliability | Single-instance scheduling and persistent article records                            | Durable retry jobs, distributed locks, dead-letter queues, automated recovery                     |
| Search                 | Case-insensitive substring match over published events, up to 200 results            | PostgreSQL full-text search, pagination and semantic search                                       |
| Editorial              | Internal API only: environment-configured admin, JWT, edit/approve/reject and audits | User lifecycle and distinct EDITOR/REVIEWER/ANALYST permissions, rich comparison view             |
| Telegram               | Explicit send, recorded attempt, demo prevention                                     | Correction edits, delivery reconciliation, scheduled briefs, chart attachments                    |
| Briefs                 | Top-five approved events in a readable briefing                                      | Scheduled immutable daily/weekly editions, research synthesis                                     |
| Markets                | Clearly labeled sample data; blank in live mode                                      | Licensed feeds, historical indicator storage, real charts and bank decisions                      |
| Localization           | Uzbek Latin interface and summaries                                                  | English/Russian interface and translated publication variants                                     |
| Monitoring             | Health, source counters, generation counters, protected metrics endpoint             | Grafana dashboards, cost alerts, admin Telegram alerts, operational runbooks                      |
| Infrastructure         | Compose PostgreSQL/backend/nginx frontend                                            | Backups, restore drills, TLS, deployment automation; optional services are not integrated         |

Numeric checks detect new numeral tokens, not semantic truth. Human review is mandatory. The system cannot claim to prevent all hallucinations. Sample source links lead to institutional homepages and are labeled as contextual references, not evidence of a current news claim.

Events are identified by UUID in live operation. Original source text is never exposed through public endpoints. Corrections preserve prior content in audit records and take the event out of public view until reapproval. Telegram correction synchronization is a documented remaining task.

## October 4 implementation update

- Added persisted retry timing and bounded recovery for failed initial AI synthesis. This remains a single-instance worker; distributed queueing is not implemented.
- Added source attempt timestamps so failed feeds respect their polling interval.
- Added authenticated source configuration with URL/domain validation, polling bounds and before/after audit records.
- Added private editorial, launch-preflight and live-feed-check PowerShell helpers. No reader account UI was introduced.
- Removed automatic demo fallback during backend outages; the public site now displays a service error and reload action.
- Added provider fallback HTTP tests, draft-recovery tests and source-configuration integration coverage. All 14 backend and four frontend tests pass; the production frontend build passes.
- Verified all three configured feed URLs. The actual collector ingested 40 articles with zero source failures; a repeat collection ingested zero duplicates. These remain private drafts. Public demo publication is still explicitly labeled.
- Expanded the feed defaults to 20 parse-checked RSS endpoints in a 34-source registry, with an audited import helper for existing databases.
- AI credentials and Telegram destination are still missing. Docker's Linux engine is unavailable. Production rollout and seven-day acceptance remain outstanding.

## Verification in this workspace

- Production frontend build and unit tests.
- Spring integration tests against an isolated H2 database with Flyway migrations.
- Actual local backend health and proxied frontend API responses.
- Browser checks for desktop/mobile layout, public navigation, search and event source details.
- Docker engine was unavailable; PostgreSQL Compose execution was not tested.
- Live AI and Telegram calls were not made. No external content was published.

The seven-day acceptance criterion requires a configured deployment, live provider credentials, a verified source set and daily editorial observation. It cannot be inferred from unit tests.

## Baseline public-site redesign

The reader-facing website is entirely public. It contains no login, account, profile, avatar, bookmark, subscription or editorial/admin controls. Internal write APIs remain protected so anonymous readers cannot edit or publish. The layout uses a horizontal masthead, white background, dark text, thin dividers and restrained orange accents. Sample market widgets were removed.
