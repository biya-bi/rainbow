insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) VALUES (2001,'Test Application','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAXIMUM_INVALID_PASSWORD_ATTEMPTS, MAXIMUM_PASSWORD_AGE, MAXIMUM_PASSWORD_LENGTH, MINIMUM_SPECIAL_CHARACTERS, MINIMUM_PASSWORD_AGE, MINIMUM_PASSWORD_LENGTH, PASSWORD_ATTEMPT_WINDOW, PASSWORD_HISTORY_LENGTH, APPLICATION_ID,MINIMUM_LOWERCASE_CHARACTERS,MINIMUM_UPPERCASE_CHARACTERS,MINIMUM_NUMERIC) VALUES (5, 30, 25, 1, 0, 7, 10, 10, 2001,1,1,1);
insert into token_policies(APPLICATION_ID,MAXIMUM_AGE) VALUES (2001,30);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2001,'Authority 100',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2002,'Authority 101',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2003,'Authority 102',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2004,'Authority 103',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2005,'Authority 104',2001,current_timestamp(),current_timestamp(),1);
commit;