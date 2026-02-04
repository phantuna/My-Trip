
Create table user(
    id VARCHAR(255) NOT NULL PRIMARY KEY ,
    username VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    status ENUM('ACTIVE','BLOCKED'),
    birthday datetime(6),

    created_by varchar(255),
    modified_by varchar(255),
    modified_date date,
    created_date date

    role_id varchar(255),
    FOREIGN KEY (role_id) REFERENCES role(id)

);

Create table role(
    id varchar(255) NOT NULL PRIMARY KEY,
    description varchar(255),
    name varchar(255)
);



Create table permission(
    id varchar(255) NOT NULL PRIMARY KEY,
    permission varchar(255),
    album_id varchar(255),
    user_id varchar(255),

    constraint fk_permisson_album foreign key (album_id ) references album(id),
    constraint fk_permisson_user foreign key (user_id) references  user(id)
);

Create table locations(
    id varchar(255) NOT NULL PRIMARY KEY,
    name varchar(255),
    address varchar(255),
    description varchar(255),
    latitude double,
    longitude double,
    status ENUM('APPROVAL','PENDING','REJECTED'),

    created_by varchar(255),
    modified_by varchar(255),
    modified_date date,
    created_date date

);
Create table album(
    id VARCHAR(255) NOT NULL PRIMARY KEY ,
    title VARCHAR(255),
    description VARCHAR(255),

    status ENUM('PRIVATE','PUBLIC','SHARED'),

    created_by varchar(255),
    modified_by varchar(255),
    modified_date date,
    created_date date,

    location_id VARCHAR(255),
    owner_id VARCHAR(255),

    CONSTRAINT fk_album_location
        FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT fk_album_owner
        FOREIGN KEY (owner_id) REFERENCES user(id)
);
Create table photo(
    id varchar(255) NOT NULL PRIMARY KEY,
    width int,
    height int,
    size int,
    image_url varchar(255),
    thumbnail_url varchar(255),
    album_id varchar(255),
    owner_id varchar(255),

    constraint fk_photo_album foreign key (album_id ) references album(id),
    constraint fk_photo_user foreign key (owner_id) references  user(id)
);
Create table interactions(
    id varchar(255) NOT NULL PRIMARY KEY,
    target_id varchar(255),
    target_type ENUM('ALBUM','PHOTO'),
    type ENUM ('LIKE','SAVE','SHARE','VIEW'),
    user_id varchar(255),
    constraint fk_interactions_user foreign key (user_id) references user(id)

);