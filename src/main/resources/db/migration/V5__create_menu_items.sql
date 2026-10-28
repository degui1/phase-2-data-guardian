CREATE TABLE menu_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    restaurant_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500) NOT NULL,
    price_in_cents BIGINT NOT NULL,
    dine_in_only BIT NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_menu_items PRIMARY KEY (id),
    CONSTRAINT uk_menu_items_restaurant_name UNIQUE (restaurant_id, name),
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);
