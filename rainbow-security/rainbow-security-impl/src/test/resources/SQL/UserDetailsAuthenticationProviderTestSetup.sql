insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) VALUES (7001,'Test Application 1','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) VALUES (30, 25, 1, 0, 7, 10, 7001,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) VALUES (7001,NULL,3,NULL,10);
insert into login_policies(APPLICATION_ID,THRESHOLD) VALUES (7001,5);
insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) VALUES (7002,'Test Application 2','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) VALUES (0, 25, 1, 0, 7, 10, 7002,1,1,1);
insert into login_policies(APPLICATION_ID,THRESHOLD) VALUES (7002,5);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) VALUES (7002,NULL,3,NULL,10);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7001, '1754-01-01', 'sampleUser1', 7001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7001, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7002, '1754-01-01', 'sampleUser2', 7001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7002, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7003, '1754-01-01', 'sampleUser3', 7001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7003, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7004, '1754-01-01', 'sampleUser4', 7001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7004, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7005, '1754-01-01', 'sampleUser5', 7001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (0, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7005, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7006, '1754-01-01', 'sampleUser6', 7001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7006, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7007, date_add(current_timestamp(),INTERVAL -1 DAY), 'sampleUser7', 7001,date_add(current_timestamp(),INTERVAL -2 DAY),date_add(current_timestamp(),INTERVAL -2 DAY),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, date_add(current_timestamp(),INTERVAL -2 DAY), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', date_add(current_timestamp(),INTERVAL -1 DAY), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7007, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (7008, '1754-01-01', 'sampleUser8', 7002,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 7008, 1);
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (7001,'Group 1','Group description',7001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (7001,'Authority 1','Authority description',7001,current_timestamp(),current_timestamp(),1);
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (7002,'Group 2','Group description',7002,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (7002,'Authority 2','Authority description',7002,current_timestamp(),current_timestamp(),1);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (7001,7001);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (7002,7002);
insert into group_users(USER_ID,GROUP_ID) VALUES (7001,7001);
insert into group_users(USER_ID,GROUP_ID) VALUES (7002,7001);
insert into group_users(USER_ID,GROUP_ID) VALUES (7003,7001);
insert into group_users(USER_ID,GROUP_ID) VALUES (7004,7001);
insert into group_users(USER_ID,GROUP_ID) VALUES (7005,7001);
insert into group_users(USER_ID,GROUP_ID) VALUES (7006,7001);
insert into group_users(USER_ID,GROUP_ID) VALUES (7007,7001);
insert into group_users(USER_ID,GROUP_ID) VALUES (7008,7001);
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) VALUES (7001,1,current_timestamp());
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) VALUES (7002,1,current_timestamp());
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) VALUES (7003,1,current_timestamp());
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) VALUES (7004,1,current_timestamp());
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) VALUES (7005,1,current_timestamp());
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) VALUES (7007,1,date_add(current_timestamp(),INTERVAL -1 DAY));
commit;