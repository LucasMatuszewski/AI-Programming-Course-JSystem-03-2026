CREATE TABLE IF NOT EXISTS healthcheck_test (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

INSERT INTO healthcheck_test (name) VALUES ('ok');

SELECT id, name FROM healthcheck_test ORDER BY id;
