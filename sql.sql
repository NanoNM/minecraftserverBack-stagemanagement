create databases Main;

use main;

create table autologin
(
    id    int auto_increment
        primary key,
    name  varchar(20)  not null,
    token varchar(256) not null
);

create table shop_car
(
    sid          int auto_increment
        primary key,
    uid          int         not null,
    itemid       int         not null,
    num          int         null,
    price        int         null,
    created_user varchar(20) null,
    created_time varchar(20) null,
    modify_user  varchar(20) null,
    modify_time  varchar(20) null
);

create table user
(
    id          int auto_increment
        primary key,
    user_name   varchar(256)                           not null,
    passwd      varchar(256)                           not null,
    email       varchar(256)                           not null,
    vip_level   int          default 1                 null,
    authority   varchar(256) default 'default'         null,
    UUID        text                                   not null,
    create_time datetime     default CURRENT_TIMESTAMP not null,
    modify_time datetime     default CURRENT_TIMESTAMP not null
);

