source rahnamdb-schema.sql;

insert into users (username, name, userpass, avatar) values ('pau','Pau',MD5('pau'), null);
insert into user_roles values ('pau', 'registered');
insert into users (username, name, userpass, avatar) values ('cris','Cris',MD5('cris'), null);
insert into user_roles values ('cris', 'registered');
insert into users (username, name, userpass, avatar) values ('ruben','Ruben',MD5('ruben'), null);
insert into user_roles values ('ruben', 'registered');

insert into photos (photoid, username, title, description) values ('1','pau', 'atardecer', 'maria maria');
insert into photos (photoid, username, title, description) values ('2','cris', 'amanecer', 'pepipon');
insert into photos (photoid, username, title, description) values ('3','ruben', 'hola', 'cardenton');
insert into photos (photoid, username, title, description) values ('4','pau', 'qwd', 'maria maria');
insert into photos (photoid, username, title, description) values ('5','pau', 'athrgj', 'maria maria');
insert into photos (photoid, username, title, description) values ('6','pau', 'gg', 'maria maria');
insert into photos (photoid, username, title, description) values ('7','ruben', 'wfeefwwe', 'maria maria');
insert into photos (photoid, username, title, description) values ('8','pau', 'weweg', 'maria maria');


update users set avatar = 1 where username = 'pau';
update users set avatar = 2 where username = 'cris';
update users set avatar = 3 where username = 'ruben';

 insert into categories (name) values ('videojuegos');
 insert into categories (name) values ('Anime');
 insert into categories (name) values ('Alimentacion');
 insert into categories (name) values ('Bebidas');
 insert into categories (name) values ('Arte');
 insert into categories (name) values ('Arquitectura');
 insert into categories (name) values ('Escalada');
 insert into categories (name) values ('Baseball');
 insert into categories (name) values ('Futbol');
 insert into categories (name) values ('Coches y motos');
 insert into categories (name) values ('Moda');
 insert into categories (name) values ('Viajes');
 insert into categories (name) values ('Animales');
 insert into categories (name) values ('Naturaleza');
 insert into categories (name) values ('Fotografia');
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