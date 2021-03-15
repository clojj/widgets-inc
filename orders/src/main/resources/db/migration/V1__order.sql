create table order_entity
(
    id   int IDENTITY (1,1) NOT NULL,
    uuid UNIQUEIDENTIFIER NOT NULL DEFAULT NEWSEQUENTIALID(),
    code VARCHAR(50) NOT NULL,
    CONSTRAINT PK_ORDER_ID PRIMARY KEY CLUSTERED (id)
);