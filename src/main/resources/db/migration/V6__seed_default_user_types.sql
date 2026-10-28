-- Required roles for registration and restaurant ownership. IDs are generated.
INSERT INTO user_types (name, created_at, updated_at)
SELECT 'CUSTOMER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM user_types WHERE UPPER(name) = 'CUSTOMER');

INSERT INTO user_types (name, created_at, updated_at)
SELECT 'RESTAURANT_OWNER', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (SELECT 1 FROM user_types WHERE UPPER(name) = 'RESTAURANT_OWNER');
