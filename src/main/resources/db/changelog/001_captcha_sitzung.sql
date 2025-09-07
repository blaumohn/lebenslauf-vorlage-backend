--liquibase formatted sql

--changeset du:0001
CREATE EXTENSION IF NOT EXISTS pgcrypto;
--rollback DROP EXTENSION IF EXISTS pgcrypto;

--changeset du:0002
CREATE TABLE public.captcha_sitzung (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  captcha_text varchar(255) NOT NULL,
  erstellt_am timestamptz NOT NULL DEFAULT now(),
  archiviert boolean NOT NULL DEFAULT false,
  ip_address inet,
  user_agent text,
  status varchar(16) NOT NULL DEFAULT 'NEU',
  verbraucht_am timestamptz,
  request_count integer NOT NULL DEFAULT 0,
  CONSTRAINT request_count_nneg CHECK (request_count >= 0),
  CONSTRAINT status_ok CHECK (status IN ('NEU','GELÖST','VERBRAUCHT'))
);

--rollback DROP TABLE IF EXISTS public.captcha_sitzung;
