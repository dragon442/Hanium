show databases;

create table play1(
	id varchar(50) not null primary key,
	reg_time datetime not null,
	rid_time datetime not null,
	nomality varchar(1) not null,
	accom_num int not null
);

create table play2(
	id varchar(50) not null primary key,
	reg_time datetime not null,
	rid_time datetime not null,
	nomality varchar(1) not null,
	accom_num int not null
);

create table play3(
	id varchar(50) not null primary key,
	reg_time datetime not null,
	rid_time datetime not null,
	nomality varchar(1) not null,
	accom_num int not null
);

create table play1(
	id varchar(50) not null primary key,
	reg_time datetime not null,
	rid_time datetime not null
);

create table play2(
	id varchar(50) not null primary key,
	reg_time datetime not null,
	rid_time datetime not null
);

create table play3(
	id varchar(50) not null primary key,
	reg_time datetime not null,
	rid_time datetime not null
);

insert into play1(id, reg_time, rid_time)values('이재용', now(), now());
insert into play2(id, reg_time, rid_time, nomality, accom_num)
	values('이재용', now(), now(), true, 0);
insert into play3(id, reg_time, rid_time, nomality, accom_num)
	values('이재용', now(), now(), true, 0);	
	
insert into play1(id, reg_time, rid_time, nomality, accom_num)
	values('이재용', now(), now(), true, 0);
insert into play1(id, reg_time, rid_time, nomality, accom_num)
	values('이영서', now(), now(), true, 0);
	
select * from play1;
select * from play2;
select * from play3;

delete from play1 where id = 357677063299527;
delete from play1 where id = 352454070701183;
delete from play2 where id = 357677063299527;
delete from play3 where id = 357677063299527;
delete from play1;
delete from play2;
delete from play3;

update play1 set num_id=num_id-1;

drop table play1;
drop table play2;
drop table play3;


