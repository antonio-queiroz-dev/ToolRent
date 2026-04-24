create table tenant (
    id          uuid primary key,
    name        varchar(120) not null,
    active      boolean not null default true,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
);

create table app_user (
    id          uuid primary key,
    tenant_id   uuid not null references tenant(id),
    code        integer not null,
    name        varchar(120) not null,
    email       varchar(180) not null,
    password    varchar(100) not null,
    role        varchar(20) not null,
    active      boolean not null default true,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),
    constraint uk_app_user_email unique (email),
    constraint uk_app_user_tenant_code unique (tenant_id, code)
);
