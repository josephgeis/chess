create table if not exists user(
    username varchar(32) primary key,
    email varchar(128) not null,
    password varchar(128) not null
);

create table if not exists session(
    username varchar(32) not null,
    token varchar(36) primary key,
    foreign key (username) references user(username) on delete cascade
);

create table if not exists game(
    id int primary key auto_increment,
    whiteUsername varchar(32),
    blackUsername varchar(32),
    gameName varchar(32) not null,
    game text,
    foreign key (whiteUsername)
                 references user(username) on delete set null,
    foreign key (blackUsername)
                 references user(username) on delete set null
);