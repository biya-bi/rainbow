USE `rainbow_asset_explorer`;

start transaction;
insert into department(name,creation_date,last_update_date,creator,updater) values ('Operations',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Trade Services',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('IT',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Global Market',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Private Wealth Management',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Human Resources & Corp Comm',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Company Secretariat & Legal',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Corporate Banking',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Investment Banking',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Financial Control',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Internal Audit',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Enterprise Risk Management',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('Admin',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
insert into department(name,creation_date,last_update_date,creator,updater) values ('MD/CEO''s office',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
commit;