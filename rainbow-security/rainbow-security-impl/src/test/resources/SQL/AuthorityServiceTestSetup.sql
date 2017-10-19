insert into applications(id,name,description,creation_date,last_update_date,version) values (2001,'Test Application 1','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(id,max_age, max_length, min_special_chars_count, min_age, min_length, history_threshold, application_id,min_lowercase_chars_count,min_uppercase_chars_count,min_numeric_count,creation_date,last_update_date,version) values (2001,30, 25, 1, 0, 7, 10, 2001,1,1,1,current_timestamp(),current_timestamp(),1);
insert into lockout_policies(id,application_id,duration,threshold,reset_time,attempt_window,creation_date,last_update_date,version) values (2001,2001,NULL,3,NULL,10,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2001,'Authority 100',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2002,'Authority 101',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2003,'Authority 102',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2004,'Authority 103',2001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,application_id,creation_date,last_update_date,version) values (2005,'Authority 104',2001,current_timestamp(),current_timestamp(),1);
commit;