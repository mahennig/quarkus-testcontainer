-- Flyway owns the schema; the JPA entity com.mahennig.postgres.User is validated against it.
CREATE TABLE users (
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT users_email_key UNIQUE (email)
);
