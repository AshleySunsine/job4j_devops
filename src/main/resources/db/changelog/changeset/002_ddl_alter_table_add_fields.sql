--liquibase formatted sql
--changeset parsentev:add_first_arg
ALTER TABLE users ADD COLUMN first_arg VARCHAR(255);

--liquibase formatted sql
--changeset parsentev:add_second_arg
ALTER TABLE users ADD COLUMN second_arg VARCHAR(255);

--liquibase formatted sql
--changeset parsentev:add_result
ALTER TABLE users ADD COLUMN result VARCHAR(255);