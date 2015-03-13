DROP TABLE IF EXISTS resource_fingerprints;

CREATE TABLE resource_fingerprints
(
  url VARCHAR(256) PRIMARY KEY NOT NULL UNIQUE,
  hash BYTEA NOT NULL,
  content_size BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  verification_strength SMALLINT NOT NULL
);

CREATE INDEX resource_fingerprints_ix on resource_fingerprints(url);
