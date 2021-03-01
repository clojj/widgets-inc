create database winc;
create schema winc_product;

CREATE TABLE winc_product.product_entity
(
    Id           INT                                     NOT NULL PRIMARY KEY CLUSTERED,
    Code         VARCHAR(50)                             NOT NULL,
    Name         VARCHAR(50)                             NOT NULL,
    SysStartTime DATETIME2 GENERATED ALWAYS AS ROW START HIDDEN NOT NULL,
    SysEndTime   DATETIME2 GENERATED ALWAYS AS ROW END   HIDDEN NOT NULL,
    PERIOD FOR SYSTEM_TIME (SysStartTime, SysEndTime)
)
    WITH (SYSTEM_VERSIONING = ON (HISTORY_TABLE = winc_product.ProductEntityHistory));

