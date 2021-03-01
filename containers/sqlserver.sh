
# docker run -p 1433:1433 --env ACCEPT_EULA=Y --env SA_PASSWORD=sqlserver2019! --name sqlserver -h sqlserver mcr.microsoft.com/mssql/server:2019-latest
# commited image with empty schema winc_product
docker run -p 1433:1433 --env ACCEPT_EULA=Y --env SA_PASSWORD=sqlserver2019! --name sqlserver -h sqlserver jw/sqlserver-winc:latest
