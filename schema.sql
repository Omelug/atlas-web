
    create table image (
       id  serial not null,
        file varchar(255),
        create_time timestamp,
        update_time timestamp,
        Item_id int4,
        primary key (id)
    );

    create table Item (
       id  serial not null,
        author varchar(250),
        color varchar(250),
        create_time timestamp,
        update_time timestamp,
        name varchar(250),
        name2 varchar(250),
        text varchar(2500),
        typ int4,
        parent_group_id int4,
        primary key (id)
    );

    alter table image 
       add constraint FKkn5p1goa9wbjdgl8065awce55 
       foreign key (Item_id) 
       references Item;

    alter table Item 
       add constraint FK59u490xlr5kh712mrlav698pf 
       foreign key (parent_group_id) 
       references Item;

    alter table Item 
       add constraint FK5b1bc9i50segrenlaema9adxw 
       foreign key (id) 
       references Item;

    create table image (
       id  serial not null,
        file varchar(255),
        create_time timestamp,
        update_time timestamp,
        Item_id int4,
        primary key (id)
    );

    create table Item (
       id  serial not null,
        author varchar(250),
        color varchar(250),
        create_time timestamp,
        update_time timestamp,
        name varchar(250),
        name2 varchar(250),
        text varchar(2500),
        typ int4,
        parent_group_id int4,
        primary key (id)
    );

    alter table image 
       add constraint FKkn5p1goa9wbjdgl8065awce55 
       foreign key (Item_id) 
       references Item;

    alter table Item 
       add constraint FK59u490xlr5kh712mrlav698pf 
       foreign key (parent_group_id) 
       references Item;

    alter table Item 
       add constraint FK5b1bc9i50segrenlaema9adxw 
       foreign key (id) 
       references Item;

    create table image (
       id  serial not null,
        file varchar(255),
        create_time timestamp,
        update_time timestamp,
        Item_id int4,
        primary key (id)
    );

    create table Item (
       id  serial not null,
        author varchar(250),
        color varchar(250),
        create_time timestamp,
        update_time timestamp,
        name varchar(250),
        name2 varchar(250),
        text varchar(2500),
        typ int4,
        parent_group_id int4,
        primary key (id)
    );

    alter table image 
       add constraint FKkn5p1goa9wbjdgl8065awce55 
       foreign key (Item_id) 
       references Item;

    alter table Item 
       add constraint FK59u490xlr5kh712mrlav698pf 
       foreign key (parent_group_id) 
       references Item;

    alter table Item 
       add constraint FK5b1bc9i50segrenlaema9adxw 
       foreign key (id) 
       references Item;
