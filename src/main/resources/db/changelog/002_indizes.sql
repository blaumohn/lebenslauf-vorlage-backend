--liquibase formatted sql

--changeset du:0003
CREATE INDEX idx_captcha_sitzung_archiviert ON captcha_sitzung (archiviert);

--changeset du:0004
CREATE INDEX idx_captcha_sitzung_offen ON captcha_sitzung (status) WHERE archiviert = false;

--changeset du:0005
CREATE INDEX idx_captcha_sitzung_ip ON captcha_sitzung (ip_address);
