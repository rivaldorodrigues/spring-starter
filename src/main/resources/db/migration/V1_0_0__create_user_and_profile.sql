CREATE SCHEMA IF NOT EXISTS audit;

CREATE SEQUENCE IF NOT EXISTS profile_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE profile
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    CONSTRAINT pk_profile PRIMARY KEY (id)
);

CREATE TABLE profile_user
(
    profile_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    CONSTRAINT pk_profile_user PRIMARY KEY (profile_id, user_id)
);

CREATE TABLE "user"
(
    id                        BIGINT      NOT NULL,
    name                      VARCHAR(255),
    email                     VARCHAR(254),
    login                     VARCHAR(20),
    password                  VARCHAR(60) NOT NULL,
    password_reset_code       VARCHAR(255),
    password_reset_expiration TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE profile
    ADD CONSTRAINT uc_profile_name UNIQUE (name);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_email UNIQUE (email);

ALTER TABLE "user"
    ADD CONSTRAINT uc_user_login UNIQUE (login);

ALTER TABLE profile_user
    ADD CONSTRAINT fk_prouse_on_profile FOREIGN KEY (profile_id) REFERENCES profile (id);

ALTER TABLE profile_user
    ADD CONSTRAINT fk_prouse_on_user FOREIGN KEY (user_id) REFERENCES "user" (id);

INSERT INTO profile(id, name) values (1, 'ADMINISTRATOR');
INSERT INTO profile(id, name) values (2, 'VIEWER');

ALTER SEQUENCE profile_seq restart with 3;

INSERT INTO "user"(id, email, name, login, password) VALUES (1, 'root@mail.com', 'Root', 'root', '$2a$10$JuYhmMbJSsOxCcrhzhJ/auK0RgOiWraVu1N4TFYFnmyqTovtzpddq');

INSERT INTO profile_user(user_id, profile_id) VALUES (1, 1);

alter sequence user_seq restart with 2;
