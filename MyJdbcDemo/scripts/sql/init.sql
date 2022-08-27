CREATE DATABASE myjdbcdemo DEFAULT CHARACTER SET utf8;

USE myjdbcdemo;

DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `singer`;
DROP TABLE IF EXISTS `batch`;

CREATE TABLE user
(
    id       BIGINT,
    name     VARCHAR(30),
    password VARCHAR(30)
);
CREATE TABLE singer
(
    id       BIGINT,
    name     VARCHAR(30),
    bestsong VARCHAR(30),
    image    MEDIUMBLOB
);
CREATE TABLE batch
(
    id       BIGINT,
    name     VARCHAR(30),
    password VARCHAR(30)
);

INSERT INTO singer (id, name, bestsong, image)
VALUES (1, 'JayChou', '七里香', NULL);
INSERT INTO singer (id, name, bestsong, image)
VALUES (2, '林俊杰', '可惜没如果', NULL);
INSERT INTO singer (id, name, bestsong, image)
VALUES (2, 'Jolin Cai', '布拉格广场', NULL);

