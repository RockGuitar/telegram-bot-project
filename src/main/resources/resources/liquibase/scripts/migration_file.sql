--liquibase formatted sql

--changeset nikita: 1
CREATE TABLE notification_task(
    id SERIAL PRIMARY KEY,
    chat_id BIGINT,
    notification TEXT,
    schedule TIMESTAMP
);