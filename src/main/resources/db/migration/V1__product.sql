create table product_entity
(
    uuid uuid default gen_random_uuid() not null
        constraint product_entity_pkey
            primary key,
    code varchar,
    name varchar
);

alter table product_entity owner to postgres;
