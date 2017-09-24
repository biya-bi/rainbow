insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) VALUES (8001,'Test Application 1','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) VALUES (8002,'Test Application 2','This application is also used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) VALUES (30, 25, 1, 0, 7, 10, 8001,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) VALUES (8001,NULL,3,NULL,10);
insert into password_policies(MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT) VALUES (30, 25, 1, 2, 7, 10, 8002,1,1,1);
insert into lockout_policies(APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW) VALUES (8002,NULL,3,NULL,10);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8001, '1754-01-01', 'sampleUser1', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8001, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8003, '1754-01-01', 'sampleUser2', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8003, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8004, '1754-01-01', 'sampleUser3', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8004, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8005, '1754-01-01', 'sampleUser4', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8005, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8006, '1754-01-01', 'sampleUser5', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8006, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8007, '1754-01-01', 'sampleUser6', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8007, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8008, '1754-01-01', 'sampleUser7', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8008, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8009, '1754-01-01', 'sampleUser8', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8009, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8010, '1754-01-01', 'sampleUser9', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8010, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8011, '1754-01-01', 'sampleUser10', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8011, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8012, '1754-01-01', 'sampleUser11', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 1, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8012, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8013, '1754-01-01', 'sampleUser12', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8013, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8014, '1754-01-01', 'sampleUser13', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (0, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8014, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8015, '1754-01-01', 'sampleUser14', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8015, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8016, '1754-01-01', 'sampleUser15', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8016, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8017, '1754-01-01', 'sampleUser16', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8017, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8018, '1754-01-01', 'sampleUser17', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 1, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8018, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8019, '1754-01-01', 'sampleUser18', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (0, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8019, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8020, '1754-01-01', 'sampleUser19', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 1, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8020, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8021, '1754-01-01', 'sampleUser20', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8021, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8022, '1754-01-01', 'sampleUser21', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8022, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8023, '1754-01-01', 'sampleUser22', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8023, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8024, '1754-01-01', 'sampleUser23', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8024, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8025, '1754-01-01', 'sampleUser24', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8025, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8026, date_add(current_timestamp(),INTERVAL -1 DAY), 'sampleUser25', 8001,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, date_add(current_timestamp(),INTERVAL -2 DAY), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', date_add(current_timestamp(),INTERVAL -1 DAY), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8026, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8027, '1754-01-01', 'sampleUser26', 8002,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8027, 1);
insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (8028, date_add(current_timestamp(),INTERVAL -1 DAY), 'sampleUser27', 8002,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED_OUT,PASSWORD,USER_ID,ENCRYPTED) VALUES (1, date_add(current_timestamp(),INTERVAL -2 DAY), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', date_add(current_timestamp(),INTERVAL -1 DAY), 0, '$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe', 8028, 1);
insert into password_histories(id,change_date,password,membership_id,CREATION_DATE,LAST_UPDATE_DATE) values (8001,date_add(current_timestamp(),INTERVAL -10 DAY),'$2a$04$TTSabYo9subetYbdCh7zaujjq/mkCjnMex132CHOl3XoL83MK.FJe',8007,current_timestamp(),current_timestamp());
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (8001,'Group 1','Group description',8001,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (8001,'Authority 1','Authority description',8001,current_timestamp(),current_timestamp(),1);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (8001,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8001,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8003,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8004,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8005,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8006,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8007,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8008,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8009,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8010,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8014,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8015,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8018,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8019,8001);
insert into group_users(USER_ID,GROUP_ID) VALUES (8020,8001);
insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (8002,'Group 2','Group description',8002,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (8002,'Authority 2','Authority description',8002,current_timestamp(),current_timestamp(),1);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (8002,8002);
insert into group_users(USER_ID,GROUP_ID) VALUES (8026,8002);
insert into group_users(USER_ID,GROUP_ID) VALUES (8027,8002);
insert into group_users(USER_ID,GROUP_ID) VALUES (8028,8002);
insert into login_histories(user_id,history_id,login_date) values (8015,1,current_timestamp());
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) values (8020,1,current_timestamp());
insert into login_histories(USER_ID,HISTORY_ID,LOGIN_DATE) values (8026,1,date_add(current_timestamp(),INTERVAL -1 DAY));
commit;