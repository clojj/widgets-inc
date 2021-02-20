create table order_entity
(
    uuid   uuid default gen_random_uuid() not null
        constraint order_entity_pkey
            primary key,
    code   varchar,
    amount integer
);

alter table order_entity owner to postgres;
