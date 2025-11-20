CREATE TABLE parking (
  parking_id BIGINT AUTO_INCREMENT,
  license_plate VARCHAR(20) NOT NULL,
  spot_id BIGINT,
  sector VARCHAR(10) NOT NULL,
  entry_time TIMESTAMP NOT NULL,
  exit_time TIMESTAMP NULL,
  base_price_at_entry DECIMAL(10,2),
  dynamic_factor DOUBLE,
  total_price DECIMAL(10,2),

  CONSTRAINT pk_parking PRIMARY KEY(parking_id),
  CONSTRAINT fk_parking_spot FOREIGN KEY (spot_id) REFERENCES spot(spot_id)
);