set @currentVersion = '1';

create table if not exists chess(
    name varchar(8) primary key,
    value varchar(8)
);
set @version = (select value from chess where name = 'version');
set @update = (select @version is null or @currentVersion != @version);

if @update then
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
                     references user(username) on delete set null,
        foreign key (blackUsername)
                     references user(username) on delete set null
    );

    replace chess(name, value) values('version', @currentVersion);
end if;