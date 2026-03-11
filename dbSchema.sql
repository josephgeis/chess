drop table if exists game;
drop table if exists session;
drop table if exists user;

create table user(
    username varchar(32) primary key,
    email varchar(128) not null,
    password varchar(128) not null
);

create table session(
    username varchar(32) not null,
    token varchar(36) primary key,
    foreign key (username) references user(username) on delete cascade
);

create table game(
    id int primary key auto_increment,
    whiteUsername varchar(32),
    blackUsername varchar(32),
    gameName varchar(32) not null,
    game text,
    foreign key (whiteUsername)
                 references user(username),
    foreign key (blackUsername)
                 references user(username)
);