CREATE TABLE restaurant_opening_periods (
    id BIGINT NOT NULL AUTO_INCREMENT,
    restaurant_id BIGINT NOT NULL,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    opens_at TIME(6) NOT NULL,
    closes_at TIME(6) NOT NULL,
    CONSTRAINT pk_restaurant_opening_periods PRIMARY KEY (id),
    CONSTRAINT uk_restaurant_opening_period UNIQUE (restaurant_id, day_of_week, opens_at, closes_at),
    CONSTRAINT fk_opening_periods_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
);
