--liquibase formatted sql

--changeset achumachenko:20241008220300

-- Создание таблицы users
create table users (
    id uuid primary key default gen_random_uuid(), -- Генерация UUID
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    username varchar(255) not null unique, -- Уникальное имя пользователя
    password varchar(255) not null, -- Пароль (рекомендуется использовать хэш)
    email varchar(255)
);

-- Добавление колонки user_id в таблицу texts и установка связи с users.id
alter table texts
add column user_id uuid references users(id) on delete set null;
