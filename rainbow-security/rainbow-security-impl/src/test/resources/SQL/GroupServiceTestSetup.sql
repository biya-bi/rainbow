insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) values (4001,'Test Application 1','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 4001,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (4001,NULL,3,NULL,10);
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version)VALUES (4001,'Sample Group 1','Group description',4001,current_timestamp(),current_timestamp(),1);
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version)VALUES (4002,'Sample Group 2','Group description',4001,current_timestamp(),current_timestamp(),1);
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version)VALUES (4003,'Sample Group 3','Group description',4001,current_timestamp(),current_timestamp(),1);
commit;