ALTER TABLE sector
  ADD COLUMN open_hour VARCHAR(5),
  ADD COLUMN close_hour VARCHAR(5),
  ADD COLUMN duration_limit_minutes INT;
