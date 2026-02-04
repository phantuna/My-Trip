ALTER TABLE locations
DROP
FOREIGN KEY FK2n9b9nwsb15ngqy7ox2l3lhib;

ALTER TABLE locations
DROP
COLUMN created_by_id;

ALTER TABLE locations
DROP
COLUMN status;

ALTER TABLE album
DROP
COLUMN status;

ALTER TABLE album
    ADD status VARCHAR(255) NULL;

ALTER TABLE locations
    ADD status VARCHAR(255) NULL;

ALTER TABLE user
DROP
COLUMN status;

ALTER TABLE user
    ADD status VARCHAR(255) NULL;

ALTER TABLE interactions
DROP
COLUMN target_type;

ALTER TABLE interactions
DROP
COLUMN type;

ALTER TABLE interactions
    ADD target_type VARCHAR(255) NULL;

ALTER TABLE interactions
    ADD CONSTRAINT uc_2addfb183cc366a85c4652067 UNIQUE (target_type);

ALTER TABLE interactions
    ADD type VARCHAR(255) NULL;

ALTER TABLE interactions
    ADD CONSTRAINT uc_2addfb183cc366a85c4652067 UNIQUE (type);