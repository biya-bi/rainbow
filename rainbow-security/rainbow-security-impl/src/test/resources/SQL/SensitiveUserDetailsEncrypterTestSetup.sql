insert into applications(id,name,description,creation_date,last_update_date,version) values (5001,'Test Application 1','This application is used for testing.',current_timestamp(),current_timestamp(),1);
insert into password_policies(id,max_age, max_length, min_special_chars_count, min_age, min_length, history_threshold, application_id,min_lowercase_chars_count,min_uppercase_chars_count,min_numeric_count,creation_date,last_update_date,version) values (5001,30, 25, 1, 0, 7, 10, 5001,1,1,1,current_timestamp(),current_timestamp(),1);
insert into lockout_policies(id,application_id,duration,threshold,reset_time,attempt_window,creation_date,last_update_date,version) values (5001,5001,NULL,3,NULL,10,current_timestamp(),current_timestamp(),1);
insert into users(id,last_activity_date,user_name,application_id,creation_date,last_update_date,version) values (5001, '1754-01-01', 'sampleUser1', 5001,current_timestamp(),current_timestamp(),1);
insert into memberships(id,enabled,creation_date,description,failed_revry_atmpt_cnt,failed_revry_atmpt_win_start,failed_pwd_atmpt_cnt,failed_pwd_atmpt_win_start,last_lock_out_date,last_pwd_change_date,locked,password,user_id,encrypted,last_update_date,version) values (5001,1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', current_timestamp(), 0, 'admin', 5001, 0,current_timestamp(),1);
insert into groups(id,name,description,application_id,creation_date,last_update_date,version) values (5001,'Group 1','Group description',5001,current_timestamp(),current_timestamp(),1);
insert into authorities(id,name,description,application_id,creation_date,last_update_date,version) values (5001,'Authority 1','Authority description',5001,current_timestamp(),current_timestamp(),1);
insert into group_authorities(group_id,authority_id) values (5001,5001);
insert into group_users(user_id,group_id) values (5001,5001);
insert into recovery_information(id,membership_id,question,answer,encrypted,creation_date,last_update_date,version) values (5001,5001,'What is your father''s tribe?','SAMPLE FATHER''S TRIBE',0,current_timestamp(),current_timestamp(),1);
insert into recovery_information(id,membership_id,question,answer,encrypted,creation_date,last_update_date,version) values (5002,5001,'What is your dream work?','SAMPLE DREAM WORK',0,current_timestamp(),current_timestamp(),1);
insert into recovery_information(id,membership_id,question,answer,encrypted,creation_date,last_update_date,version) values (5003,5001,'What was the name of your primary school?','SAMPLE PRIMARY SCHOOL NAME',0,current_timestamp(),current_timestamp(),1);
insert into login_histories(membership_id,history_id,login_date) values (5001,1,current_timestamp());
commit;