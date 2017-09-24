insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) VALUES (10001,'Test Application 1','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) VALUES (30, 25, 1, 0, 7, 10, 10001,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) VALUES (10001,NULL,3,NULL,10);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (10001, '1754-01-01', 'sampleUser1', 10001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 10001, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (10002, '1754-01-01', 'sampleUser2', 10001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 10002, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (10003, '1754-01-01', 'sampleUser3', 10001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 10003, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (10004, '1754-01-01', 'sampleUser4', 10001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 10004, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (10005, '1754-01-01', 'sampleUser5', 10001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 10005, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (10006, '1754-01-01', 'sampleUser6', 10001,current_timestamp(),current_timestamp(),1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (10007, '1754-01-01', 'sampleUser7', 10001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 10007, 1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10001,'Authority 10001','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10002,'Authority 10002','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10003,'Authority 10003','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10004,'Authority 10004','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10005,'Authority 10005','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10006,'Authority 10006','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10007,'Authority 10007','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10008,'Authority 10008','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10009,'Authority 10009','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10010,'Authority 10010','Authority description',10001,current_timestamp(),current_timestamp(),1);
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (10001,'Sample Group 1','Group description',10001,current_timestamp(),current_timestamp(),1);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10002,10003);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10003,10006);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10003,10007);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10003,10008);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10004,10006);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10004,10007);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10005,10009);
insert into user_authorities(USER_ID,AUTHORITY_ID) VALUES (10007,10003);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (10001,10001);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (10001,10002);
insert into group_users(GROUP_ID,USER_ID) VALUES (10001,10007);
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) VALUES (10007,1,current_timestamp());
commit;