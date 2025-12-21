CREATE DATABASE db_tenant1;
CREATE DATABASE db_tenant2;

\c db_tenant1
\i /docker-entrypoint-initdb.d/db_tenant_schema.sql

\c db_tenant2
\i /docker-entrypoint-initdb.d/db_tenant_schema.sql