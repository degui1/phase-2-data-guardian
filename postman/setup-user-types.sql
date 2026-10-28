-- Executar manualmente no banco definido por DB_NAME, após o Hibernate criar as tabelas.
-- Não é migration. Não fixa IDs e não altera tipos existentes.
INSERT INTO user_types (name, created_at, updated_at)
SELECT 'CUSTOMER', UTC_TIMESTAMP(), UTC_TIMESTAMP()
WHERE NOT EXISTS (SELECT 1 FROM user_types WHERE UPPER(name) = 'CUSTOMER');

INSERT INTO user_types (name, created_at, updated_at)
SELECT 'RESTAURANT_OWNER', UTC_TIMESTAMP(), UTC_TIMESTAMP()
WHERE NOT EXISTS (SELECT 1 FROM user_types WHERE UPPER(name) = 'RESTAURANT_OWNER');