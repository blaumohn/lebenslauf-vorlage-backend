--liquibase formatted sql

--changeset du:0006 context:@dev
INSERT INTO captcha_sitzung (captcha_text, ip_address, user_agent)
VALUES ('AB12CD','127.0.0.1','TestRunner');

--rollback
DELETE FROM captcha_sitzung WHERE user_agent='TestRunner';
