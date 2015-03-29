drop user 'rahnam'@'localhost';
create user 'rahnam'@'localhost' identified by 'rahnam';
grant all privileges on rahnamdb.* to 'rahnam'@'localhost';
flush privileges;