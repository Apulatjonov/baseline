create table source_record (
 id varchar(64) primary key,
 name text,
 domain text,
 source_type text,
 country text,
 language text,
 rss_url text,
 reliability double precision,
 enabled boolean,
 poll_minutes integer,
 terms_notes text,
 last_success text,
 last_failure text,
 articles_collected bigint
);
create table event_record (
 id varchar(64) primary key,
 title text,
 category text,
 summary text,
 why_it_matters text,
 uzbekistan_impact text,
 countries text,
 entities text,
 status text,
 confidence text,
 editorial_label text,
 importance double precision,
 uzbekistan_score double precision,
 published_at text,
 updated_at text,
 demo boolean,
 validation_notes text,
 version bigint
);
create table article_record (
 id varchar(64) primary key,
 source_id text,
 event_id text,
 url text,
 title text,
 body text,
 content_hash text,
 published_at text,
 retrieved_at text
);
create table audit_record (
 id varchar(64) primary key,
 event_id text,
 action text,
 actor text,
 payload text,
 created_at text
);
create table publication_record (
 id varchar(64) primary key,
 event_id text,
 channel text,
 status text,
 external_id text,
 body text,
 created_at text
);
create unique index article_hash_idx on article_record(content_hash);
create index article_event_idx on article_record(event_id);
create index event_status_idx on event_record(status,published_at);
create unique index publication_channel_idx on publication_record(event_id,channel);