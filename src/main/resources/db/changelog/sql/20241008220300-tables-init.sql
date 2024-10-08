--liquibase formatted sql

--changeset achumachenko:20241008220300

create table texts (
    text_id serial primary key,
    file_name varchar(255) not null,
    content bytea not null,
    upload_date timestamp not null default current_timestamp
);

create table sentences (
    sentence_id serial primary key,
    text_id integer references texts(text_id) on delete cascade,
    content text not null,
    sentence_number integer not null
);

create table pos_tags (
    pos_tag_id serial primary key,
    code varchar(100) not null,
    description varchar(100) not null
);

create table syntactic_roles (
    syntactic_role_id serial primary key,
    code varchar(100) not null,
    description varchar(100) not null
);

create table words (
    word_id serial primary key,
    sentence_id integer references sentences(sentence_id) on delete cascade,
    word_text varchar(100) not null,
    lemma varchar(100) not null,
    pos_tag_id integer references pos_tags(pos_tag_id),
    syntactic_role_id integer references syntactic_roles(syntactic_role_id)
);

insert into syntactic_roles (code, description)
values
    ('nsubj', 'Подлежащее'),
    ('nsubj:pass', 'Страдательное подлежащее'),
    ('ROOT', 'Сказуемое'),
    ('amod', 'Определение'),
    ('det', 'Определение'),
    ('obl', 'Обстоятельство'),
    ('obj', 'Дополнение'),
    ('acl', 'Причастный оборот'),
    ('acl:relcl', 'Причастный оборот'),
    ('advcl', 'Деепричастный оборот'),
    ('xcomp', 'Придаточная связь'),
    ('csubj', 'Подчинённая связь'),
    ('csubj:pass', 'Подчинённая страдательная связь'),
    ('advmod', 'Обстоятельство'),
    ('cc', 'Союз'),
    ('advcl:cond', 'Условие/Причина'),
    ('parataxis', 'Связь пояснения/Бессоюзная связь'),
    ('case', 'Предлог'),
    ('punct', 'Пунктуация'),
    ('iobj', 'Косвенное дополнение'),
    ('ccomp', 'Связь подчинённого предложения'),
    ('nummod', 'Количественное определение');
