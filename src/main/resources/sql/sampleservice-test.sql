create user 'sample'@'localhost' identified by 'insecure';
create database sampleservice_test;
grant all privileges on sampleservice_test.* to 'sample'@'localhost';
flush privileges;

use sampleservice_test;
create table foodata (id int primary key, name varchar(100));