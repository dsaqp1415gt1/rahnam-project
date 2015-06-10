source rahnamdb-schema.sql;

insert into users (username, name, userpass, avatar) values ('pau','Pau',MD5('pau'), null);
insert into user_roles values ('pau', 'registered');
insert into users (username, name, userpass, avatar) values ('cris','Cris',MD5('cris'), null);
insert into user_roles values ('cris', 'registered');
insert into users (username, name, userpass, avatar) values ('ruben','Ruben',MD5('ruben'), null);
insert into user_roles values ('ruben', 'registered');

insert into photos (photoid, username) values ('1','pau');
insert into photos (photoid, username) values ('2','cris');
insert into photos (photoid, username) values ('3','ruben');
insert into photos (photoid, username) values ('4','pau');
insert into photos (photoid, username) values ('5','pau');
insert into photos (photoid, username) values ('6','pau');
insert into photos (photoid, username) values ('7','ruben');
insert into photos (photoid, username) values ('8','pau');


update users set avatar = 1 where username = 'pau';
update users set avatar = 2 where username = 'cris';
update users set avatar = 3 where username = 'ruben';

insert into categories (name) values ('videojuegos');
insert into categories (name) values ('Anime');
insert into categories (name) values ('Alimentación');
insert into categories (name) values ('Bebidas');
insert into categories (name) values ('Arte');
insert into categories (name) values ('Arquitectura');
insert into categories (name) values ('Escalada');
insert into categories (name) values ('Baseball');
insert into categories (name) values ('Fútbol');
insert into categories (name) values ('Coches y motos');
insert into categories (name) values ('Moda');
insert into categories (name) values ('Viajes');
insert into categories (name) values ('Animales');
insert into categories (name) values ('Naturaleza');
insert into categories (name) values ('Fotografía');
insert into categories (name) values ('Peinados y maquillaje');

insert into photoscategories values ('1',1);
insert into photoscategories values ('1',2);
insert into photoscategories values ('2',3);
insert into photoscategories values ('2',4);
insert into photoscategories values ('3',5);
insert into photoscategories values ('4',1);
insert into photoscategories values ('5',1);
insert into photoscategories values ('6',1);
insert into photoscategories values ('7',1);
insert into photoscategories values ('8',1);


insert into comments (username, photoid, content) values ('pau','1','pau habla en foto pau');
insert into comments (username, photoid, content) values ('cris','1','cris habla en foto pau');
insert into comments (username, photoid, content) values ('ruben','3','ruben habla en foto ruben');

insert into usersfollows (username, followed) values ('pau', 'cris');
insert into usersfollows (username, followed) values ('cris', 'pau');
insert into usersfollows (username, followed) values ('pau', 'ruben');
insert into usersfollows (username, followed) values ('ruben', 'cris');

insert into userscategories (username, categoryid) values ('pau', 1);
insert into userscategories (username, categoryid) values ('pau', 2);
insert into userscategories (username, categoryid) values ('pau', 3);