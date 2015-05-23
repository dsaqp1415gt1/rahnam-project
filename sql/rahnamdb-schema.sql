drop database if exists rahnamdb;
create database rahnamdb;
use rahnamdb;

create table users (
	username 	varchar(20) not null primary key,
	userpass 	varchar(80) not null,
	name 		varchar(20) not null,
	avatar 		int null,
	email 		varchar(20),
	birth 		date,
	gender 		varchar(20)
);

create table user_roles (
	username				varchar(20) not null,
	rolename 				varchar(20) not null,
	foreign key(username) 	references users(username) on delete cascade,
	unique key (username, rolename)
);

create table photos (
	photoid					varchar(50) not null primary key,
	username 				varchar (20) not null,
	title 					varchar (20),
	description 			varchar (500),
	last_modified			timestamp default current_timestamp on update current_timestamp,
	creationTimestamp 		datetime not null default current_timestamp,
	foreign key (username) 	references users(username)
);

create table categories (
	categoryid				int not null auto_increment primary key,
	name varchar(50)
);

create table photoscategories (
	photoid 					varchar(50) not null,
	categoryid 					int not null,
	primary key (photoid, categoryid),
	foreign key (photoid) 		references photos(photoid) on delete cascade,
	foreign key (categoryid) 	references categories(categoryid) on delete cascade
);

create table comments (
	commentid 				int not null auto_increment primary key,
	username				varchar (50) not null,
	photoid 				varchar(50) not null,
	content 				varchar(500) not null,
	last_modified			timestamp default current_timestamp on update current_timestamp,
	creationTimestamp 		datetime not null default current_timestamp,
	foreign key (username)	references users(username) on delete cascade,
	foreign key (photoid) 	references photos(photoid) on delete cascade
);


create table usersfollows (
	username 		varchar (50) not null,
	followed 		varchar (50) not null,
	primary key (followed, username),
	foreign key (followed) references users(username),
	foreign key  (username) references users(username)
);

create table userscategories (
	username 					varchar (50) not null,
	categoryid 					int not null,
	foreign key (username) 		references users(username),
	foreign key (categoryid)	references categories(categoryid)
);
