insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) values (2001,'Test Application 1','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 2001,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (2001,NULL,3,NULL,10);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2001,'Authority 100',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2002,'Authority 101',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2003,'Authority 102',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2004,'Authority 103',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2005,'Authority 104',2001,current_timestamp(),current_timestamp(),1);
commit;