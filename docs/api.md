# API

Public base: `/api/v1`. JSON responses. Non-public actions require `Authorization: Bearer <token>`.

| Method | Path                                    | Behavior                                                                                               |
| ------ | --------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| GET    | `/events?q=&category=&country=&entity=` | Published events, importance descending; maximum 200                                                   |
| GET    | `/events/{id}`                          | Event with source links and public correction notes                                                    |
| GET    | `/search?q=...`                         | Published archive search                                                                               |
| GET    | `/topics/{category}`                    | Category filter                                                                                        |
| GET    | `/countries/{code}`                     | Country code filter                                                                                    |
| GET    | `/organizations/{entity}`               | Institution/entity filter                                                                              |
| GET    | `/sources`                              | Registry and source health                                                                             |
| GET    | `/briefs/morning`                       | Five highest-ranked approved events                                                                    |
| GET    | `/status`                               | Demo flag, registry/enabled-source/event counts                                                        |
| POST   | `/auth/login`                           | `{ "username": "admin", "password": "..." }` → token (1h)                                              |
| GET    | `/admin/events`                         | Editorial queue, including drafts                                                                      |
| GET    | `/admin/articles`                       | Private normalized source excerpts                                                                     |
| GET    | `/admin/events/{id}/audit`              | Editorial and AI audit history                                                                         |
| POST   | `/admin/events/{id}/edit`               | `title`, `summary`, `whyItMatters`, `uzbekistanImpact`; `correctionNote` required for published events |
| POST   | `/admin/events/{id}/approve`            | Validate gate and publish on website                                                                   |
| POST   | `/admin/events/{id}/reject`             | Withdraw/reject event                                                                                  |
| POST   | `/admin/collect`                        | Collect enabled RSS sources synchronously                                                              |
| PATCH  | `/admin/sources/{id}`                   | `{ "enabled": true }` (requires configured feed URL)                                                   |
| POST   | `/admin/telegram/{id}`                  | Send approved non-demo event to configured channel                                                     |
| GET    | `/admin/publications`                   | Inspect attempts and uncertain delivery states                                                         |

Errors include a `message` where available. Important status codes: 401 unauthenticated, 403 insufficient role, 404 private/missing event, 409 invalid transition/duplicate attempt, 422 unresolved numeric checks, 429 request limit, 503 missing integration configuration.

Actuator health is public at `/actuator/health`; other actuator endpoints require ADMIN scope and are not routed by nginx. Rate limits use the direct peer address (180 requests/minute; 10 login attempts/minute). Behind nginx this currently acts as a shared ingress quota, not a per-client distributed quota.
