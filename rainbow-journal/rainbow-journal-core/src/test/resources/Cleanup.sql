delete from subscriptions;
delete from publications;
delete from journals;
delete from profiles;
alter table subscriptions auto_increment=1;
alter table publications auto_increment=1;
alter table journals auto_increment=1;
alter table profiles auto_increment=1;
commit;
