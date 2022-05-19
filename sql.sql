use mine;

create table authme
(
    id         mediumint unsigned auto_increment
        primary key,
    username   varchar(255)                   not null,
    realname   varchar(255)                   not null,
    passwd     varchar(255) collate ascii_bin not null,
    ip         varchar(40) collate ascii_bin  null,
    lastlogin  bigint                         null,
    x          double       default 0         not null,
    y          double       default 0         not null,
    z          double       default 0         not null,
    world      varchar(255) default 'world'   not null,
    regdate    bigint       default 0         not null,
    regip      varchar(40) collate ascii_bin  null,
    yaw        float                          null,
    pitch      float                          null,
    email      varchar(255)                   null,
    isLogged   smallint     default 0         not null,
    hasSession smallint     default 0         not null,
    totp       varchar(32)                    null,
    constraint username
        unique (username)
)
    charset = utf8;

create table autologin
(
    id            int auto_increment
        primary key,
    name          varchar(20)                        not null,
    token         varchar(256)                       not null,
    create_time   datetime default CURRENT_TIMESTAMP not null,
    connect_token varchar(256)                       not null
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
    id              int auto_increment
        primary key,
    user_name       varchar(256)                           not null,
    passwd          varchar(256)                           not null,
    email           varchar(256)                           not null,
    vip_level       int          default 1                 null,
    authority       varchar(256) default 'default'         null,
    UUID            text                                   not null,
    create_time     datetime     default CURRENT_TIMESTAMP not null,
    modify_time     datetime     default CURRENT_TIMESTAMP not null,
    last_login_time datetime     default CURRENT_TIMESTAMP null,
    create_by       varchar(30)  default 'system'          null
);



select a.id,a.user_name,a.create_time,a.modify_time,a.create_by,a.last_login_time,b.realname from user a left join authme b on a.authme_id = b.id where authority='admin'