CREATE TABLE captcha_sitzung (
    id UUID PRIMARY KEY,
    captcha_text VARCHAR(255) NOT NULL,
    datum TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    archiviert BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(255),
    user_agent VARCHAR(255),
    status VARCHAR(255) DEFAULT 'NEU',
    verbraucht_am TIMESTAMP,
    request_count INT DEFAULT 0
);

CREATE INDEX idx_id_archiviert ON captcha_sitzung (id, archiviert);
CREATE INDEX idx_ip ON captcha_sitzung (ip_address);
