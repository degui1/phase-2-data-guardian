CREATE TABLE restaurants (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    street VARCHAR(150) NOT NULL,
    address_number VARCHAR(20) NOT NULL,
    address_complement VARCHAR(100),
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zip_code VARCHAR(9) NOT NULL,
    cuisine_type VARCHAR(80) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_restaurants PRIMARY KEY (id),
    CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users (id)
);
