CREATE KEYSPACE Nutri WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

create table users (email varchar primary key, password varchar, name varchar, weight double, height double, date_of_birth timestamp , gender varchar,
subscription_type varchar, start_subscription timestamp, end_subscription timestamp,
date_of_registration timestamp, update_date timestamp, delete_date timestamp,
exception_products varchar, prefer_number_of_calories int, prefer_number_of_eatings int, desirable_weight int);