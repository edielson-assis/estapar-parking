CREATE TABLE spot (
  spot_id BIGINT AUTO_INCREMENT,
  sector_id BIGINT NOT NULL,
  lat DOUBLE NOT NULL,
  lng DOUBLE NOT NULL,
  is_occupied BOOLEAN NOT NULL DEFAULT FALSE,

  CONSTRAINT pk_spot PRIMARY KEY (spot_id),
  CONSTRAINT fk_spot_sector FOREIGN KEY (sector_id) REFERENCES sector(sector_id)
);