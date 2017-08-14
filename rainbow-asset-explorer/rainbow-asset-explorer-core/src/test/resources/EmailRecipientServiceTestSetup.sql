insert into locale(id,name,language_code,lcid,creation_date,last_update_date,version) values (4001,'English - United States','en','en-us',current_timestamp(),current_timestamp(),1);
insert into email_recipient(id,name,email,locale_id,creation_date,last_update_date,version) values (4001,'Recipient 4001','email4001@optimum.org',4001,current_timestamp(),current_timestamp(),1);
insert into email_recipient(id,name,email,locale_id,creation_date,last_update_date,version) values (4002,'Recipient 4002','email4002@optimum.org',4001,current_timestamp(),current_timestamp(),1);
insert into email_recipient(id,name,email,locale_id,creation_date,last_update_date,version) values (4003,'Recipient 4003','email4003@optimum.org',4001,current_timestamp(),current_timestamp(),1);
insert into email_recipient(id,name,email,locale_id,creation_date,last_update_date,version) values (4004,'Recipient 4004','email4004@optimum.org',4001,current_timestamp(),current_timestamp(),1);
insert into email_recipient(id,name,email,locale_id,creation_date,last_update_date,version) values (4005,'Recipient 4005','email4005@optimum.org',4001,current_timestamp(),current_timestamp(),1);
insert into email_recipient(id,name,email,locale_id,creation_date,last_update_date,version) values (4006,'Recipient 4006','email4006@optimum.org',4001,current_timestamp(),current_timestamp(),1);
commit;