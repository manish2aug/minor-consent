-- create schema
create schema minor_consent_schema;

-- mrt_consent table creation
create table minor_consent_schema.minor_consent
(
    minor_identifier_type               varchar(20)  not null check (minor_identifier_type in ('ID', 'PASSPORT')),
    minor_identifier_value              varchar(100) not null,
    minor_identifier_issuing_country    char(2)      not null,
    minor_birth_date                    date         not null,
    guardian_identifier_type            varchar(20)  not null check (guardian_identifier_type in ('ID', 'PASSPORT')),
    guardian_identifier_value           varchar(100) not null,
    guardian_identifier_issuing_country char(2)      not null,
    consent_request_date                timestamp    not null default current_timestamp,
    consent_applicable_date             date         not null,
    originating_system                  varchar(100) not null
);

-- unique constraint on mrt_consent
create unique index consent_unique_idx on minor_consent_schema.minor_consent (minor_identifier_type,
                                                                              minor_identifier_value,
                                                                              minor_identifier_issuing_country);
-- index on date field for quicker searches for reports
create index consent_date_idx on minor_consent_schema.minor_consent (consent_applicable_date);

-- mrt_consent withdrawn history
create table minor_consent_schema.minor_consent_withdrawn_history
(
    minor_identifier_type               varchar(20)  not null,
    minor_identifier_value              varchar(100) not null,
    minor_identifier_issuing_country    char(2)      not null,
    minor_birth_date                    date         not null,
    guardian_identifier_type            varchar(20)  not null,
    guardian_identifier_value           varchar(100) not null,
    guardian_identifier_issuing_country char(2)      not null,
    consent_request_date                timestamp    not null,
    consent_applicable_date             date         not null,
    originating_system                  varchar(100) not null,
    consent_withdrawn_date              timestamp    not null,
    edit_userid                         varchar(100) not null
);

-- create a function
create or replace function minor_consent_schema.after_consent_delete()
    returns trigger as
$$
begin
    insert into minor_consent_schema.minor_consent_withdrawn_history
    values (old.minor_identifier_type,
            old.minor_identifier_value,
            old.minor_identifier_issuing_country,
            old.minor_birth_date,
            old.guardian_identifier_type,
            old.guardian_identifier_value,
            old.guardian_identifier_issuing_country,
            old.consent_request_date,
            old.consent_applicable_date,
            old.originating_system,
            current_timestamp,
            user);
    return new;
end;

$$
    language 'plpgsql';

-- create delete trigger
create trigger consent_delete_trigger
    after delete
    on minor_consent_schema.minor_consent
    for each row
execute procedure minor_consent_schema.after_consent_delete();