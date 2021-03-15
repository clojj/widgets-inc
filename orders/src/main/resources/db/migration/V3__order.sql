
alter table order_entity
    drop constraint DF__order_enti__uuid__414EAC47
go

alter table order_entity
    drop column uuid
go

