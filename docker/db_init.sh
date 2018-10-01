#!/bin/bash  
echo "------ start -------"

mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "

CREATE DATABASE IF NOT EXISTS ca DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

USE ca;

CREATE TABLE IF NOT EXISTS user(
  id int(3) auto_increment not null primary key,
  name varchar(50),
  affiliation varchar(500),
  create_dt timestamp,
  create_user varchar(50)
);

CREATE DATABASE IF NOT EXISTS blockchain DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

USE blockchain;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
  createdAt bigint(20) DEFAULT NULL,
  updatedAt bigint(20) DEFAULT NULL,
  id int(11) NOT NULL AUTO_INCREMENT,
  firstName varchar(255) DEFAULT NULL,
  lastName varchar(255) DEFAULT NULL,
  cell varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  address varchar(255) DEFAULT NULL,
  consentId varchar(255) DEFAULT NULL,
  company varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY id (id)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

QUIT"

echo "------ end -------"
