alter table event_record add column synthesis_attempts integer not null default 0;
alter table event_record add column synthesis_retry_at text;
