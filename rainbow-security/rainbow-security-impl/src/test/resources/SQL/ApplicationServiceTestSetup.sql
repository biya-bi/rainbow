insert into applications(id,name,creation_date,last_update_date,version) values (1001,'Application 100',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 1001,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (1001,NULL,3,NULL,10);
insert into applications(id,name,creation_date,last_update_date,version) values (1002,'Application 101',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 1002,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (1002,NULL,3,NULL,10);
insert into applications(id,name,creation_date,last_update_date,version) values (1003,'Application 102',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 1003,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (1003,NULL,3,NULL,10);
insert into applications(id,name,creation_date,last_update_date,version) values (1004,'Application 103',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 1004,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (1004,NULL,3,NULL,10);
insert into applications(id,name,creation_date,last_update_date,version) values (1005,'Application 104',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 1005,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (1005,NULL,3,NULL,10);
insert into applications(id,name,creation_date,last_update_date,version) values (1006,'Rainbow Optimum',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 1006,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (1006,NULL,3,NULL,10);
insert into applications(id,name,creation_date,last_update_date,version) values (1007,'Spark Optimum',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) values (30, 25, 1, 0, 7, 10, 1007,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) values (1007,NULL,3,NULL,10);
commit;
