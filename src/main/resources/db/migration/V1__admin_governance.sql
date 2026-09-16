create table document_policy_versions (
    id uuid primary key,
    document_type varchar(80) not null,
    mandatory boolean not null,
    allowed_mime_types varchar(300) not null,
    max_file_size_bytes bigint not null check (max_file_size_bytes > 0),
    expiry_required boolean not null,
    active boolean not null,
    policy_version integer not null,
    effective_from timestamp with time zone not null,
    created_at timestamp with time zone not null,
    created_by varchar(120) not null,
    row_version bigint not null default 0,
    unique (document_type, policy_version)
);
create index ix_document_policy_active on document_policy_versions(document_type, active);

create table audit_events (
    id uuid primary key,
    source varchar(80) not null,
    event_type varchar(80),
    actor_id varchar(120),
    actor_type varchar(80),
    tenant_id varchar(120),
    action varchar(120) not null,
    outcome varchar(30) not null,
    reason varchar(1000),
    correlation_id varchar(120),
    metadata_json text,
    occurred_at timestamp with time zone not null,
    received_at timestamp with time zone not null,
    unique (source, correlation_id)
);
create index ix_audit_occurred_at on audit_events(occurred_at);
create index ix_audit_tenant on audit_events(tenant_id, occurred_at);
create index ix_audit_correlation on audit_events(correlation_id);

create table notification_deliveries (
    id uuid primary key,
    template varchar(50) not null,
    recipient varchar(320) not null,
    status varchar(30) not null,
    failure_reason varchar(500),
    created_at timestamp with time zone not null,
    completed_at timestamp with time zone
);
create index ix_notification_status on notification_deliveries(status, created_at);

insert into document_policy_versions
(id, document_type, mandatory, allowed_mime_types, max_file_size_bytes, expiry_required,
 active, policy_version, effective_from, created_at, created_by, row_version)
values
('00000000-0000-0000-0000-000000000001','COMMERCIAL_REGISTRATION',true,'application/pdf,image/jpeg,image/png',10485760,true,true,1,current_timestamp,current_timestamp,'SYSTEM',0),
('00000000-0000-0000-0000-000000000002','TRADE_LICENSE',true,'application/pdf,image/jpeg,image/png',10485760,true,true,1,current_timestamp,current_timestamp,'SYSTEM',0),
('00000000-0000-0000-0000-000000000003','ESTABLISHMENT_CARD',true,'application/pdf,image/jpeg,image/png',10485760,true,true,1,current_timestamp,current_timestamp,'SYSTEM',0),
('00000000-0000-0000-0000-000000000004','TAX_CARD',true,'application/pdf,image/jpeg,image/png',10485760,false,true,1,current_timestamp,current_timestamp,'SYSTEM',0),
('00000000-0000-0000-0000-000000000005','AUTHORIZED_SIGNATORY_QID',true,'application/pdf,image/jpeg,image/png',10485760,true,true,1,current_timestamp,current_timestamp,'SYSTEM',0),
('00000000-0000-0000-0000-000000000006','NATIONAL_ADDRESS_CERTIFICATE',false,'application/pdf,image/jpeg,image/png',10485760,false,true,1,current_timestamp,current_timestamp,'SYSTEM',0);
