ALTER DATABASE cgunning CHARACTER SET utf8 COLLATE utf8_unicode_ci;

use cgunning; # Byt till eget användarnamn

drop table bostader; # Om det finns en tidigare databas

create table bostader (
lan varchar(64),
objekttyp varchar(64),
adress varchar(64),
area float,
rum int,
pris float,
avgift float
);

ALTER TABLE bostader CHARACTER SET utf8 COLLATE utf8_unicode_ci;

insert into bostader values ('Stockholm','Bostadsrätt','Polhemsgatan 1',30,1,1000000,1234);

insert into bostader values ('Stockholm','Bostadsrätt','Polhemsgatan 2',60,2,2000000,2345);

insert into bostader values ('Stockholm','Villa','Storgatan 1',130,5,1000000,3456);

insert into bostader values ('Stockholm','Villa','Storgatan 2',160,6,1000000,3456);

insert into bostader values ('Uppsala','Bostadsrätt','Gröna gatan 1',30,1,500000,1234);

insert into bostader values ('Uppsala','Bostadsrätt','Gröna gatan 2',60,2,1000000,2345);

insert into bostader values ('Uppsala','Villa','Kungsängsvägen 1',130,5,1000000,3456);

insert into bostader values ('Uppsala','Villa','Kungsängsvägen 2',160,6,1000000,3456);


SELECT * FROM bostader;