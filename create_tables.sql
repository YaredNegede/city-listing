create DATABASE  cities;
 \connect cities;
create table city (id bigint not null, name varchar(255), primary key (id));
create table photo (id bigint not null, photo_name varchar(255), photo_url varchar(255), city_id bigint not null, primary key (id));
create sequence city_seq start with 1 increment by 50;
create sequence photo_seq start with 1 increment by 50;
alter table if exists photo add constraint FKq2eud890ctngv65muw2tmtar foreign key (city_id) references city;