-- for working with uuid_generate_v4()
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE machine (
    id              UUID            NOT NULL UNIQUE,
    name            VARCHAR(255)    NOT NULL,
    description     VARCHAR(255),
    PRIMARY KEY(id)
);

CREATE TABLE sensor (
    id              UUID            NOT NULL UNIQUE,
    name            VARCHAR(255)    NOT NULL,
    type            VARCHAR(255)    NOT NULL,
    description     TEXT,
    machine_id      UUID            REFERENCES machine(id) ON UPDATE CASCADE ON DELETE SET NULL,
    PRIMARY KEY(id)
);

CREATE TABLE sensor_journal (
    id              UUID            NOT NULL UNIQUE,
    sensor_id       UUID            NOT NULL REFERENCES sensor(id) ON UPDATE CASCADE ON DELETE CASCADE,
    value           REAL            NOT NULL,
    time            TIMESTAMPTZ     NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE reference (
    id              UUID            NOT NULL UNIQUE,
    name            VARCHAR(255)    NOT NULL,
    value           REAL            NOT NULL,
    sensor_id       UUID            NOT NULL REFERENCES sensor(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY(id)
);

CREATE TABLE reference_journal (
    id              UUID            NOT NULL UNIQUE,
    reference_id    UUID            NOT NULL REFERENCES reference(id) ON UPDATE CASCADE ON DELETE CASCADE,
    old_value       REAL            NOT NULL,
    new_value       REAL            NOT NULL,
    time            TIMESTAMPTZ     NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE role (
    id              UUID            NOT NULL UNIQUE,
    description     VARCHAR(255),
    name            VARCHAR(255)    NOT NULL UNIQUE,
    PRIMARY KEY(id)
);

CREATE TABLE "user" (
    id              UUID            NOT NULL UNIQUE,
    role_id         UUID            NOT NULL REFERENCES role(id) ON UPDATE CASCADE ON DELETE CASCADE,
    first_name      VARCHAR(255)    NOT NULL,
    last_name       VARCHAR(255)    NOT NULL,
    login           VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE user_journal (
    id              UUID            NOT NULL UNIQUE,
    user_id         UUID            NOT NULL REFERENCES "user"(id) ON UPDATE CASCADE ON DELETE CASCADE,
    action          VARCHAR(255)    NOT NULL,
    time            TIMESTAMPTZ     NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE sensor_permission (
    user_id         UUID            NOT NULL REFERENCES "user"(id) ON UPDATE CASCADE ON DELETE CASCADE,
    sensor_id       UUID            NOT NULL REFERENCES sensor(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE sensor_error (
    id              UUID            NOT NULL UNIQUE,
    sensor_id       UUID            NOT NULL REFERENCES sensor(id) ON UPDATE CASCADE ON DELETE CASCADE,
    reference       REAL            NOT NULL,
    value           REAL            NOT NULL,
    time            TIMESTAMPTZ     NOT NULL,
    PRIMARY KEY(id)
);