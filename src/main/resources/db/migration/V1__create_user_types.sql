CREATE TABLE user_types (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_user_types PRIMARY KEY (id),
    CONSTRAINT uk_user_types_name UNIQUE (name)
);
