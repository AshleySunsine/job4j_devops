--liquibase formatted sql
--changeset parsentev:add_columns_to_users

ALTER TABLE users
    ADD COLUMN first_arg VARCHAR(255),
    ADD COLUMN second_arg VARCHAR(255),
    ADD COLUMN result VARCHAR(255);