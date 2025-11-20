CREATE TABLE sector (
  sector_id BIGINT AUTO_INCREMENT,
  sector_name VARCHAR(10) NOT NULL,
  base_price DECIMAL(10,2) NOT NULL,
  max_capacity INT NOT NULL,

  CONSTRAINT pk_sector PRIMARY KEY (sector_id),
  CONSTRAINT uq_sector_name UNIQUE (sector_name)  
);