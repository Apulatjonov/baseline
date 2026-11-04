# Uzbek Economic Intelligence Platform  
## Technical Specification / TZ  
**Version:** 1.0  
**Date:** September 2026  
**Working title:** UzNewsAI / alternative brand TBD  
**Primary language:** Uzbek, Latin script  
**Secondary languages:** English, Russian  
**Primary distribution:** Telegram + web  
**Core positioning:** AI-assisted economic intelligence for Uzbek-speaking professionals

---

# 1. Project Vision

Build an automated economic intelligence platform that continuously monitors high-quality international and regional sources, detects materially important developments, groups duplicate coverage into single events, summarizes and translates the events into natural Uzbek, explains their relevance, and distributes the resulting intelligence through Telegram, web, and later email/API channels.

The system must not operate as a simple news translator.

Its core value proposition is:

> Monitor hundreds of authoritative sources, identify what actually matters, explain it concisely in Uzbek, show why it matters, preserve source provenance, and create a searchable historical intelligence layer.

The platform should eventually answer:

- What important economic events happened today?
- Which developments matter specifically for Uzbekistan?
- What changed since yesterday?
- What are global central banks doing?
- What is happening with currencies, commodities, fintech, AI, payments, trade, and macroeconomics?
- Which events are being reported by multiple credible sources?
- What trends are developing over time?
- What should a banker, economist, entrepreneur, policymaker, researcher, or student know this morning?

---

# 2. Strategic Objective

The project should become a trusted Uzbek-language economic intelligence distribution layer.

It should create three assets simultaneously:

### Distribution asset

A growing audience of:

- bankers;
- fintech professionals;
- economists;
- researchers;
- students;
- executives;
- investors;
- policymakers;
- entrepreneurs;
- technically sophisticated readers.

### Information asset

A continuously growing structured database of:

- events;
- sources;
- topics;
- entities;
- countries;
- economic indicators;
- market reactions;
- policy decisions;
- historical relationships.

### Reputation asset

Over time the publication should become known for:

- speed;
- source transparency;
- concise explanations;
- good Uzbek language;
- low noise;
- economic relevance;
- strong source selection;
- clear distinction between reported facts and interpretation.

---

# 3. Non-Goals

The first versions should explicitly NOT attempt to become:

- a general breaking-news channel;
- a political propaganda channel;
- an opinion publication;
- a social media gossip feed;
- a stock-trading signals service;
- a financial-advice service;
- an unverified AI-generated news feed;
- a scraper that republishes entire copyrighted articles;
- a replacement for original journalism.

The system should summarize and synthesize rather than reproduce.

---

# 4. Core Editorial Domains

The platform should initially cover six major categories.

## 4.1 Macroeconomics

Topics:

- inflation;
- GDP;
- employment;
- productivity;
- trade;
- industrial production;
- consumer demand;
- fiscal policy;
- sovereign debt;
- recession risks.

Priority institutions:

- IMF;
- World Bank;
- OECD;
- national statistical agencies;
- major central banks.

---

# 5. Monetary Policy and Markets

Topics:

- central-bank decisions;
- interest rates;
- quantitative tightening/easing;
- bond markets;
- FX;
- gold;
- oil;
- gas;
- major equity-market movements;
- sovereign-risk developments.

Priority central banks:

- Federal Reserve;
- ECB;
- Bank of England;
- Bank of Japan;
- People's Bank of China;
- Central Bank of Russia;
- Central Bank of Uzbekistan;
- National Bank of Kazakhstan.

---

# 6. Banking and Fintech

Topics:

- digital banks;
- payment infrastructure;
- remittances;
- cross-border payments;
- payment cards;
- QR payments;
- open banking;
- fraud;
- KYC;
- AML;
- CBDCs;
- financial infrastructure;
- banking regulation;
- major fintech companies;
- Visa;
- Mastercard;
- SWIFT;
- payment networks.

This should be one of the platform's strongest categories.

---

# 7. Technology and AI

Only technology with economic or institutional significance.

Examples:

- major AI-model releases;
- enterprise AI;
- AI regulation;
- semiconductor markets;
- cloud infrastructure;
- technology investment;
- automation;
- cybersecurity;
- AI adoption in banking;
- AI productivity research.

Avoid:

- gadget news;
- consumer-tech gossip;
- celebrity technology coverage.

---

# 8. Geoeconomics

Coverage should focus on economic consequences rather than political drama.

Topics:

- sanctions;
- tariffs;
- trade restrictions;
- supply chains;
- shipping;
- energy markets;
- China-US economic relations;
- Russia-related economic developments;
- Central Asia;
- transport corridors;
- commodity dependencies.

---

# 9. Uzbekistan Relevance

The system should continuously identify global developments that may affect Uzbekistan through:

- remittances;
- RUB movements;
- USD movements;
- commodity prices;
- gold;
- energy;
- imports;
- exports;
- China;
- Russia;
- Kazakhstan;
- Turkey;
- foreign investment;
- borrowing costs;
- global monetary conditions;
- payment systems;
- international fintech.

---

# 10. Content Types

The platform should generate several distinct products.

## 10.1 Breaking Intelligence Brief

Short format.

Structure:

**Headline**

What happened.

Why it matters.

Uzbekistan relevance, if material.

Sources.

Target length:

80–180 words.

---

# 11. Morning Macro Brief

Published every morning.

Example structure:

> **Bugun bilishingiz kerak bo'lgan 5 voqea**

1. Federal Reserve
2. Oil
3. China
4. Russia/RUB
5. Global fintech

Each item:

- 2–4 sentences;
- source;
- relevance.

Target reading time:

3–5 minutes.

---

# 12. Evening Recap

Summary of major developments during the day.

Sections:

- Markets
- Central banks
- Global economy
- Fintech
- Uzbekistan relevance

---

# 13. Why It Matters for Uzbekistan

This should become a signature format.

Example:

> **Neft narxi 8% oshdi. Bu O'zbekistonga qanday ta'sir qilishi mumkin?**

Structure:

1. What happened.
2. Direct channel.
3. Secondary channel.
4. Risks.
5. What to monitor.

Important:

The system must distinguish between:

- documented facts;
- likely transmission mechanisms;
- uncertainty.

---

# 14. One Chart

Daily or several times per week.

Possible charts:

- interest rates;
- exchange rates;
- inflation;
- oil;
- gold;
- remittances;
- central-bank balance sheets;
- trade;
- fintech adoption.

Every chart must contain:

- source;
- date;
- units;
- short interpretation.

---

# 15. Central Bank Watch

Track:

- policy decisions;
- statements;
- rate changes;
- inflation projections;
- forward guidance.

Store historical decisions.

Generate:

> Fed: unchanged  
> ECB: -25bp  
> CBU: unchanged  
> BoE: unchanged

---

# 16. Weekly Economic Intelligence

One larger summary.

Sections:

- week in numbers;
- macro;
- monetary policy;
- markets;
- fintech;
- Central Asia;
- Uzbekistan implications;
- next week's events.

---

# 17. Research Brief

One important research paper per week.

Format:

- research question;
- method;
- sample/data;
- major result;
- limitations;
- why it matters.

---

# 18. Source Architecture

Sources should be divided into tiers.

## Tier 1 — Primary Sources

Highest authority.

Examples:

- central banks;
- IMF;
- World Bank;
- OECD;
- UN;
- national statistics agencies;
- regulators;
- stock exchanges;
- government agencies;
- company investor-relations pages.

Primary sources should be preferred whenever available.

---

## Tier 2 — Major News Agencies

Examples:

- Reuters;
- AP;
- Bloomberg where legally accessible;
- AFP.

Used primarily for rapid event detection.

---

## Tier 3 — High-Quality Financial Publications

Examples:

- Financial Times;
- Wall Street Journal;
- CNBC;
- The Economist;
- MarketWatch;
- Nikkei Asia.

---

## Tier 4 — Specialist Sources

Examples:

- Finextra;
- The Paypers;
- central-bank research blogs;
- BIS;
- VoxEU;
- Peterson Institute;
- Brookings;
- NBER;
- CEPR.

---

## Tier 5 — Regional Sources

Uzbekistan, Kazakhstan, Russia, Central Asia.

Use only sources meeting editorial-quality requirements.

---

# 19. Source Registry

Every source should have metadata.

Example:

```yaml
id: federal_reserve_press
name: Federal Reserve Press Releases
type: PRIMARY
country: US
language: EN
category:
  - monetary_policy

feed_type: RSS
base_url: https://...
polling_frequency_minutes: 10

reliability_score: 1.0
requires_cross_check: false

enabled: true
```

Database fields:

```text
source_id
name
domain
source_type
country
language
rss_url
api_url
scraper_class
reliability_score
priority
poll_interval
enabled
terms_notes
last_success
last_failure
```

---

# 20. High-Level System Architecture

```text
                SOURCES

 RSS
 APIs
 Websites
 Central Banks
 Financial Institutions
 Research Institutions
 Public Data
        │
        ▼

┌─────────────────────────┐
│     COLLECTION LAYER    │
│                         │
│ RSS collectors          │
│ API clients             │
│ HTML collectors         │
│ document collectors     │
└────────────┬────────────┘
             │
             ▼

┌─────────────────────────┐
│      RAW INGESTION      │
│                         │
│ raw content             │
│ metadata                │
│ timestamp               │
│ source provenance       │
│ content hash            │
└────────────┬────────────┘
             │
             ▼

┌─────────────────────────┐
│     NORMALIZATION       │
│                         │
│ language detection      │
│ HTML cleanup            │
│ metadata extraction     │
│ entity extraction       │
└────────────┬────────────┘
             │
             ▼

┌─────────────────────────┐
│     EVENT DETECTION     │
│                         │
│ embeddings              │
│ similarity              │
│ duplicate detection     │
│ story clustering        │
└────────────┬────────────┘
             │
             ▼

┌─────────────────────────┐
│   IMPORTANCE ENGINE     │
│                         │
│ credibility             │
│ novelty                 │
│ economic importance     │
│ Uzbekistan relevance    │
│ cross-source confidence │
└────────────┬────────────┘
             │
             ▼

┌─────────────────────────┐
│       AI EDITOR         │
│                         │
│ summarize               │
│ translate               │
│ synthesize              │
│ contextualize           │
│ classify                │
└────────────┬────────────┘
             │
             ▼

┌─────────────────────────┐
│   VALIDATION ENGINE     │
│                         │
│ factual consistency     │
│ number verification     │
│ attribution checking    │
│ hallucination checks    │
│ editorial rules         │
└────────────┬────────────┘
             │
             ▼

┌─────────────────────────┐
│      PUBLICATION        │
│                         │
│ Telegram                │
│ Website                 │
│ Newsletter              │
│ API                     │
└─────────────────────────┘
```

---

# 21. Recommended Technology Stack

## Backend

Java 21

Spring Boot 3.x

Modules:

- Spring WebFlux;
- Spring Data JPA;
- Spring Scheduling;
- Spring Security;
- Spring Actuator;
- Spring Retry.

---

# 22. Database

PostgreSQL.

Use:

- JSONB;
- full-text search;
- pgvector.

Potential later addition:

TimescaleDB.

---

# 23. Message Processing

MVP:

PostgreSQL job queue or RabbitMQ.

Later:

Kafka if volume actually requires it.

Recommended initially:

RabbitMQ.

Queues:

```text
article.raw
article.normalized
article.embedded
event.detected
event.scored
event.editorial
publication.ready
publication.failed
```

---

# 24. Cache

Redis.

Use cases:

- deduplication;
- rate limiting;
- source locks;
- recently processed URL cache;
- Telegram publication locks;
- temporary AI-output cache.

---

# 25. Object Storage

MinIO for local deployment.

S3-compatible storage in production.

Store:

- raw HTML;
- source documents;
- screenshots where legally allowed;
- generated charts;
- export files.

---

# 26. AI Providers

Architecture must be provider-independent.

Interface:

```java
public interface LanguageModelProvider {

    SummaryResult summarize(...);

    TranslationResult translate(...);

    ClassificationResult classify(...);

    SynthesisResult synthesize(...);
}
```

Potential providers:

- OpenAI;
- Anthropic;
- Gemini;
- local models.

System must support fallback.

Example:

```text
Primary model fails
        ↓
retry
        ↓
secondary provider
        ↓
failure queue
```

---

# 27. AI Pipeline

For each normalized article:

### Step 1 — Classification

Return:

```json
{
  "category": "MONETARY_POLICY",
  "subcategories": [
    "FED",
    "INTEREST_RATES"
  ],
  "countries": [
    "US"
  ],
  "entities": [
    "Federal Reserve"
  ]
}
```

---

# 28. Importance Scoring

Each article receives several scores.

Example:

```text
source_score           0–1
economic_impact        0–1
novelty                0–1
uzbekistan_relevance   0–1
market_relevance       0–1
cross_source_strength  0–1
```

Final score:

```text
importance =
0.20 source_score
+
0.25 economic_impact
+
0.15 novelty
+
0.20 uzbekistan_relevance
+
0.10 market_relevance
+
0.10 cross_source_strength
```

Weights should later become configurable.

---

# 29. Event Clustering

Critical requirement.

The platform must NOT produce:

```text
Reuters: Fed cuts rates
Bloomberg: Fed cuts rates
CNBC: Fed cuts rates
FT: Fed cuts rates
```

as four separate stories.

Instead:

```text
EVENT

Federal Reserve cuts policy rate by 25bp

Sources:
Reuters
Federal Reserve
CNBC
FT
```

---

# 30. Similarity Detection

Use:

- title embeddings;
- body embeddings;
- entity overlap;
- publication-time proximity;
- keyword overlap.

Possible logic:

```text
embedding similarity > 0.85
AND
published within 24h
AND
entity overlap > threshold

→ same event
```

---

# 31. Event Entity Model

```text
event_id
canonical_title
event_type
first_seen
last_updated
importance_score
uzbekistan_score
status
summary
uzbek_summary
why_it_matters
uzbekistan_impact
confidence_score
```

---

# 32. Event Sources

```text
event_source
------------
event_id
article_id
source_id
is_primary_source
supporting_role
```

Possible roles:

```text
ORIGINAL
CONFIRMATION
BACKGROUND
DATA
REACTION
```

---

# 33. AI Synthesis

The system should summarize events from all credible sources in the cluster.

Prompt principles:

- never invent facts;
- prioritize primary source;
- preserve exact numbers;
- mention uncertainty;
- distinguish facts from interpretation;
- avoid sensationalism;
- write natural Uzbek;
- preserve names and institutional terminology.

---

# 34. Uzbek Language Requirements

Output language:

formal but readable modern Uzbek Latin.

Avoid:

- unnatural literal translations;
- overly academic syntax;
- unnecessary Russianisms;
- unnecessary English terminology;
- excessive bureaucratic Uzbek.

Preferred style:

short paragraphs.

Example:

Bad:

> Federal rezerv tizimi monetar siyosat bo'yicha tegishli qaror qabul qilganligini ma'lum qildi.

Better:

> AQSH Federal rezerv tizimi asosiy foiz stavkasini o'zgartirmadi.

---

# 35. Translation Glossary

Maintain a controlled terminology database.

Examples:

```text
interest rate
→ foiz stavkasi

policy rate
→ asosiy foiz stavkasi

quantitative easing
→ miqdoriy yumshatish

bond yield
→ obligatsiya daromadliligi

current account
→ joriy operatsiyalar hisobi

remittance
→ pul o'tkazmasi
```

Table:

```text
term
source_language
preferred_uzbek
alternative
category
notes
```

---

# 36. Editorial Labels

Every post must be classified.

Possible labels:

```text
NEWS SUMMARY
MULTI-SOURCE SYNTHESIS
PRIMARY SOURCE
DATA UPDATE
RESEARCH SUMMARY
ORIGINAL ANALYSIS
INTERVIEW
```

AI-generated content must never be labeled as original reporting.

---

# 37. Attribution

Every article must retain sources.

Example:

```text
Manbalar:
Federal Reserve
Reuters
```

Web version should link directly.

Telegram:

use compact clickable source attribution.

---

# 38. AI Transparency

Website editorial policy should state:

> The platform uses artificial intelligence to assist with source monitoring, classification, translation, summarization, and synthesis.

> AI-generated material is not presented as original reporting.

> Material claims should be traceable to cited sources.

---

# 39. Human Review Modes

Three publication modes.

## AUTO

Safe for:

- market prices;
- scheduled statistics;
- primary-source central-bank decisions;
- standardized updates.

## REVIEW

Human approval required for:

- geopolitical analysis;
- controversial claims;
- complex economic interpretation;
- high-impact Uzbekistan implications.

## MANUAL

Required for:

- original analysis;
- interviews;
- investigations;
- highly sensitive content.

---

# 40. Factual Validation

Before publication, automatically check:

- numbers;
- percentages;
- currency;
- dates;
- names;
- countries;
- direction of change;
- quote attribution.

Example:

Source:

```text
inflation increased from 2.1% to 2.6%
```

Generated:

```text
2.1% → 2.8%
```

Must fail validation.

---

# 41. Numeric Fact Model

Extract structured claims:

```json
{
  "entity": "Federal Reserve",
  "metric": "policy_rate",
  "value": 4.25,
  "unit": "%",
  "previous_value": 4.50
}
```

Compare generated text against claims.

---

# 42. Hallucination Guard

System should compare AI-generated statements against retrieved source passages.

Unsupported statements:

```text
flag
↓
remove automatically
OR
send to editorial review
```

---

# 43. Uzbekistan Relevance Engine

This should become one of the most valuable components.

Map external developments to transmission channels.

Example ontology:

```text
Russia
 ├── remittances
 ├── RUB
 ├── migration
 └── trade

China
 ├── commodity demand
 ├── imports
 ├── investment
 └── logistics

Federal Reserve
 ├── USD
 ├── global capital
 ├── borrowing costs
 └── emerging markets

Oil
 ├── regional economies
 ├── Russia
 ├── transport
 └── inflation
```

---

# 44. Relevance Score

Example:

```text
Uzbekistan relevance =
direct_country_mention
+
trade_partner_weight
+
financial_transmission
+
remittance_link
+
commodity_link
+
banking_link
+
regional_link
```

---

# 45. Confidence Labels

Possible internal levels:

```text
HIGH
MEDIUM
LOW
```

Public impact explanations should avoid false certainty.

Example:

Better:

> Bu o'zgarish O'zbekistonga asosan rubl kursi va pul o'tkazmalari orqali ta'sir qilishi mumkin.

Not:

> Bu O'zbekistonda inflyatsiyani oshiradi.

---

# 46. Database Model

Core entities:

```text
Source
Article
RawDocument
Event
EventSource
Topic
Country
Organization
Person
EconomicIndicator
EventEntity
Publication
PromptVersion
AIExecution
EditorialReview
Subscription
```

---

# 47. Article Table

```text
article
-------
id
source_id
external_id
url
canonical_url
title
body
language
published_at
retrieved_at
content_hash
embedding
status
```

---

# 48. Publication Table

```text
publication
-----------
id
event_id
channel
language
headline
body
source_text
publication_type
generated_by_model
reviewed_by
published_at
external_post_id
```

---

# 49. AI Audit Table

```text
ai_execution
------------
id
task_type
model
prompt_version
input_hash
output
token_usage
cost
latency
created_at
status
```

Every generated piece should be reproducible.

---

# 50. Prompt Versioning

Never hardcode prompts randomly.

Use:

```text
prompt
------
id
name
version
system_prompt
template
created_at
active
```

Examples:

```text
UZ_SUMMARY_v4
EVENT_SYNTHESIS_v3
UZ_RELEVANCE_v2
HEADLINE_GENERATOR_v5
```

---

# 51. Telegram Channel

Telegram is the first public surface.

Bot responsibilities:

- publish;
- edit corrected posts;
- attach charts;
- include source links;
- scheduled briefs;
- breaking alerts.

---

# 52. Telegram Post Template

Example:

```text
🇺🇸 Fed asosiy stavkani o'zgartirmadi

AQSH Federal rezerv tizimi asosiy foiz
stavkasini amaldagi darajada saqlab qoldi.

Qaror inflyatsiyaning hali maqsadli
darajadan yuqori ekanligi bilan izohlandi.

Nega muhim?

Yuqori AQSH stavkalari dollar va global
moliyalashtirish narxiga ta'sir qiladi.

🇺🇿 O'zbekiston uchun:

Bu tashqi qarz narxi, dollar kursi va
kapital oqimlari orqali bilvosita ta'sir
ko'rsatishi mumkin.

Manbalar:
Federal Reserve · Reuters

#AQSH #Fed #Makro
```

---

# 53. Website

Primary sections:

```text
/
today
macro
markets
fintech
ai
central-asia
uzbekistan
research
charts
events
sources
about
methodology
```

---

# 54. Homepage

Top section:

```text
BUGUN

7 important developments
from 186 monitored sources
```

Then:

```text
TOP STORIES
```

Then:

```text
UZBEKISTAN RELEVANCE
```

Then:

```text
MARKETS
USD
RUB
Gold
Oil
Bitcoin
```

Then:

```text
CENTRAL BANK WATCH
```

---

# 55. Search

Users should be able to search:

```text
Federal Reserve
Uzbekistan inflation
remittances
Russia
Visa
Mastercard
AI
oil
```

Results must include historical events.

---

# 56. Event Timeline

Example:

```text
FED RATE CYCLE

2026 Sep
rate unchanged

2026 Jul
-25 bp

2026 Jun
unchanged

2026 May
unchanged
```

---

# 57. Entity Pages

Automatically create pages for:

- countries;
- central banks;
- companies;
- institutions;
- topics.

Example:

```text
/organizations/federal-reserve
/topics/remittances
/countries/china
```

---

# 58. Market Data Layer

Potential assets:

```text
USD/UZS
RUB/UZS
EUR/USD
Brent
WTI
Gold
Bitcoin
S&P 500
US Treasury yields
```

Market data licensing must be handled carefully.

Prefer public or licensed APIs.

---

# 59. Scheduler

Suggested jobs.

Every 5–10 minutes:

```text
breaking sources
```

Every hour:

```text
general RSS sources
```

Every morning:

```text
Morning Brief
```

Every evening:

```text
Daily Recap
```

Weekly:

```text
Weekly Intelligence
Research Brief
```

---

# 60. Source Health Monitoring

Dashboard:

```text
Reuters RSS              ✓
IMF                      ✓
World Bank               ✓
Federal Reserve          ✓
ECB                      ✓
Finextra                 ✓

Source X                  ⚠ schema changed
Source Y                  ✕ unavailable
```

Track:

```text
last_success
average_latency
error_rate
articles_collected
duplicates
```

---

# 61. Observability

Prometheus metrics:

```text
source_fetch_success_total
source_fetch_failure_total
articles_ingested_total
events_created_total
ai_calls_total
ai_errors_total
publication_total
telegram_errors_total
```

Grafana dashboard.

---

# 62. Alerting

Send admin Telegram alerts for:

```text
source unavailable > 6h
publication failed
AI provider unavailable
duplicate surge
cost threshold exceeded
parser failure
```

---

# 63. Admin Panel

Must support:

- source management;
- source health;
- article queue;
- event clusters;
- editorial review;
- publication scheduling;
- AI prompt versions;
- cost monitoring;
- corrections;
- terminology management.

---

# 64. Editorial Review Screen

Reviewer sees:

```text
EVENT

Sources
[1]
[2]
[3]

AI summary

Uzbek summary

Uzbekistan relevance

Confidence

[APPROVE]
[EDIT]
[REJECT]
```

---

# 65. Correction System

If a factual error is discovered:

- correct website article;
- edit Telegram post where possible;
- log revision;
- store previous version.

Public correction note when material.

---

# 66. Copyright Rules

The platform must:

- avoid copying full articles;
- avoid substantial verbatim reproduction;
- generate original summaries;
- quote minimally;
- link to original source;
- respect robots/terms where applicable;
- prefer RSS/API/licensed feeds.

Raw stored source content should not automatically become publicly accessible.

---

# 67. Business Model — Later

Do not monetize immediately.

Possible future tiers:

## Free

Telegram + basic website.

## Professional

- custom alerts;
- searchable archive;
- sector feeds;
- dashboards;
- email briefing;
- PDF export.

## Institutional

- API;
- custom monitoring;
- industry intelligence;
- white-label feeds;
- internal dashboards.

---

# 68. Professional Alert Example

User:

```text
Topics:
cross-border payments
Uzbekistan
Kazakhstan
Visa
Mastercard
remittances
```

System sends only relevant events.

---

# 69. Future Intelligence Features

## Narrative Detection

Detect recurring themes:

```text
"central banks delaying cuts"
"AI capex increasing"
"China demand weakening"
"remittance regulation tightening"
```

---

# 70. Trend Detection

Count event frequency.

Example:

```text
Cross-border payments

Jan     12
Feb     15
Mar     19
Apr     31
May     44
```

Then:

> Coverage and institutional activity related to cross-border payment infrastructure has accelerated.

---

# 71. Contradiction Detection

Compare sources.

Example:

```text
Source A:
inflation expected to fall

Source B:
inflation risks increasing
```

Generate:

> Analysts disagree on inflation outlook.

---

# 72. Forecast Tracking

Store predictions made by:

- IMF;
- World Bank;
- central banks;
- economists.

Later compare against actual outcomes.

Example:

```text
IMF forecast
Uzbekistan GDP 2027
5.8%

Actual:
...
```

---

# 73. Research Database

Automatically ingest:

- NBER;
- CEPR;
- IMF papers;
- World Bank papers;
- BIS;
- SSRN where permissible.

Classify by relevance.

---

# 74. Personalization — Later

Users choose:

```text
banking
AI
Uzbekistan
central banks
fintech
markets
```

Feed adjusts accordingly.

---

# 75. API

Possible endpoints:

```text
GET /api/v1/events
GET /api/v1/events/{id}

GET /api/v1/topics/{topic}

GET /api/v1/countries/{country}

GET /api/v1/sources

GET /api/v1/briefs/morning

GET /api/v1/search?q=...
```

---

# 76. Security

Requirements:

- JWT admin authentication;
- RBAC;
- secrets via environment/vault;
- rate limiting;
- request auditing;
- encrypted credentials;
- no API keys committed to Git.

Roles:

```text
ADMIN
EDITOR
REVIEWER
ANALYST
```

---

# 77. Deployment

Initial infrastructure:

```text
Docker Compose

nginx
backend
frontend
postgres
redis
rabbitmq
minio
prometheus
grafana
```

Later:

Kubernetes only if actually necessary.

---

# 78. Repository Structure

```text
uz-economic-intelligence/

backend/
 ├── collector/
 ├── ingestion/
 ├── normalization/
 ├── clustering/
 ├── scoring/
 ├── ai/
 ├── editorial/
 ├── publishing/
 ├── telegram/
 ├── search/
 ├── analytics/
 └── admin/

frontend/

infrastructure/
 ├── docker/
 ├── monitoring/
 └── nginx/

database/
 └── migrations/

prompts/

docs/
```

---

# 79. Suggested Backend Modules

```text
source-service
collector-engine
article-service
event-engine
embedding-service
classification-service
scoring-engine
editorial-ai-service
validation-service
publication-service
telegram-service
analytics-service
admin-service
```

These can initially exist as modules within a single Spring Boot monolith.

Do NOT start with microservices.

---

# 80. MVP Scope

The first release should NOT attempt all features.

MVP:

### Sources

20–30 reliable sources.

### Categories

- macro;
- monetary policy;
- fintech;
- AI/economics;
- Uzbekistan relevance.

### Features

- automated RSS ingestion;
- deduplication;
- event clustering;
- AI Uzbek summary;
- source attribution;
- importance scoring;
- Telegram publishing;
- admin review;
- searchable web archive.

---

# 81. MVP Success Criterion

The system should run for seven consecutive days and:

- collect automatically;
- detect duplicates;
- produce coherent Uzbek;
- avoid obvious factual hallucinations;
- publish 5–10 strong items/day;
- require less than 30 minutes/day of manual intervention.

---

# 82. Phase 2

Add:

- Uzbekistan relevance engine;
- Morning Brief;
- weekly summary;
- market data;
- charts;
- topic pages;
- entity extraction;
- research papers.

---

# 83. Phase 3

Add:

- newsletter;
- personalized subscriptions;
- trend detection;
- event timelines;
- contradiction detection;
- API;
- professional alerts.

---

# 84. Phase 4

Add original intelligence products:

- interviews;
- proprietary indices;
- original datasets;
- sector reports;
- Uzbekistan fintech reports;
- periodic analytical publications.

This is where the platform begins developing its own authority rather than relying predominantly on external reporting.

---

# 85. Critical Quality KPIs

Do not optimize primarily for number of posts.

Track:

```text
source reliability
factual error rate
duplicate rate
manual correction rate
average reading time
returning users
forward/share rate
Telegram view/subscriber ratio
newsletter open rate
source diversity
percentage of primary-source stories
```

---

# 86. AI Quality KPIs

```text
unsupported claim rate
incorrect-number rate
translation correction rate
human rejection rate
hallucination rate
average editor modifications
```

Target over time:

```text
human rejection < 5%
numeric error < 0.5%
unsupported factual claims ≈ 0%
```

---

# 87. Strategic KPIs

Because the platform is also intended to create leverage:

Track:

```text
professionals subscribed
banking/fintech readers
academics subscribed
executives subscribed
media citations
inbound interview requests
inbound collaboration requests
institutions referencing platform
people contacting platform proactively
```

These matter more than raw subscriber count.

---

# 88. Brand Principles

Brand should communicate:

- intelligence;
- clarity;
- seriousness;
- speed;
- modernity.

Avoid:

- flashy crypto aesthetic;
- generic AI robot imagery;
- excessive nationalism;
- sensational headlines;
- clickbait.

---

# 89. Possible Brand Directions

Not final names:

```text
Brief.uz

Signal.uz

IQTISOD

Macro.uz

Insight Central Asia

Uzbek Economic Brief

Pul & Siyosat

Vector

Signal CA
```

A stronger name should eventually be selected based on available domains and Telegram handles.

---

# 90. Editorial Voice

Voice:

- calm;
- confident;
- concise;
- analytical;
- non-sensational.

Never:

> SHOK! Dollar qulashi boshlandi!!!

Instead:

> Dollar asosiy valyutalarga nisbatan pasaydi. Bozor ishtirokchilari Fed stavkalari bo'yicha kutishlarini qayta baholamoqda.

---

# 91. Article Scoring Example

Incoming articles:

```text
Celebrity tech announcement
score: 0.21

Minor company earnings
score: 0.39

ECB rate decision
score: 0.91

Russia remittance regulation affecting Central Asia
score: 0.94
```

Only high-value material should reach the channel.

The value of the product is partly what it refuses to publish.

---

# 92. Core Competitive Difference

A normal news channel does:

```text
article
→ translation
→ Telegram
```

This platform must do:

```text
200 sources
      ↓
2,000 items
      ↓
deduplication
      ↓
150 events
      ↓
economic scoring
      ↓
20 relevant events
      ↓
Uzbekistan relevance
      ↓
5–10 important developments
      ↓
Uzbek intelligence
```

That filtering layer is the actual product.

---

# 93. Long-Term Moat

The moat should not be the AI model.

Everyone can use an LLM.

The moat becomes:

### Historical event database

Years of structured economic events.

### Source infrastructure

Hundreds of maintained collectors.

### Uzbek economic terminology

High-quality terminology and style corpus.

### Relevance graph

Understanding how global events transmit into Uzbekistan.

### Audience

Direct distribution to economically sophisticated Uzbek readers.

### Editorial reputation

Trust built over time.

### Proprietary datasets

Derived indices and trends.

---

# 94. Five-Year Vision

The platform should evolve from:

### 2026

AI-assisted Telegram economic brief.

to:

### 2027

Uzbek economic intelligence website + newsletter.

to:

### 2028

Professional search, alerts, data and research products.

to:

### 2029+

A recognized Central Asian economic-information organization capable of producing:

- proprietary analysis;
- original interviews;
- datasets;
- market intelligence;
- reports;
- conferences;
- professional subscriptions;
- institutional services.

The objective is not to remain an AI Telegram bot.

The Telegram bot is the **entry point**.

The long-term asset is the **institution around the information flow**.

---

# 95. Final Product Definition

The simplest accurate description is:

> An AI-assisted Uzbek-language economic intelligence system that continuously monitors authoritative global and regional sources, detects important economic events, consolidates duplicate reporting, produces transparent source-backed Uzbek summaries, explains relevant transmission channels to Uzbekistan, and distributes intelligence through Telegram, web, email, and eventually professional data products.

Its fundamental pipeline is:

```text
COLLECT
   ↓
VERIFY
   ↓
CLUSTER
   ↓
RANK
   ↓
SYNTHESIZE
   ↓
LOCALIZE
   ↓
REVIEW
   ↓
PUBLISH
   ↓
ARCHIVE
   ↓
LEARN
```

The most important design principle is:

> **AI does the repetitive information-processing work. Human judgment defines what deserves attention, what counts as credible, and where interpretation requires restraint.**

If executed correctly, this is not a Telegram translation channel.

It is the first layer of an **Uzbek economic intelligence media institution**.