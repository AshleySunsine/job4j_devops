--liquibase formatted sql
--changeset parsentev:add_user_update_date

ALTER TABLE users ADD COLUMN update_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now();

--rollback ALTER TABLE users DROP COLUMN update_date;
