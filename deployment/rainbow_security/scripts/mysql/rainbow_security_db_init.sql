start transaction;
set @applicationId=(select ifnull(max(id),0)+1 from applications);
set @applicationName='Rainbow Security';
set @applicationDescription='Application for managing users, groups, applications, authorities, and roles.';

insert into applications(ID,NAME,DESCRIPTION,creation_date,last_update_date,version) VALUES (@applicationId,@applicationName,@applicationDescription,current_timestamp(),current_timestamp(),1);

set @passwordPolicyId=(select ifnull(max(id),0)+1 from password_policies);

insert into password_policies(ID,MAX_AGE, MAX_LENGTH, MIN_SPECIAL_CHARS_COUNT, MIN_AGE, MIN_LENGTH, HISTORY_THRESHOLD, APPLICATION_ID,MIN_LOWERCASE_CHARS_COUNT,MIN_UPPERCASE_CHARS_COUNT,MIN_NUMERIC_COUNT,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (@passwordPolicyId, 30, 25, 1, 0, 7, 10, @applicationId, 1, 1, 1,current_timestamp(),current_timestamp(),1);

set @lockoutPolicyId=(select ifnull(max(id),0)+1 from lockout_policies);

insert into lockout_policies(ID,APPLICATION_ID,DURATION,THRESHOLD,RESET_TIME,ATTEMPT_WINDOW,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (@lockoutPolicyId,@applicationId,NULL,3,NULL,10,current_timestamp(),current_timestamp(),1);

set @loginPolicyId=(select ifnull(max(id),0)+1 from login_policies);

insert into login_policies(ID,APPLICATION_ID,THRESHOLD,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (@loginPolicyId,@applicationId,5,current_timestamp(),current_timestamp(),1);

set @userId=(select ifnull(max(id),0)+1 from users);
set @userName='admin';
set @password='admin';

insert into users(ID,LAST_ACTIVITY_DATE,USER_NAME,APPLICATION_ID,CREATION_DATE,LAST_UPDATE_DATE,VERSION) VALUES (@userId, '1754-01-01', @userName, @applicationId,current_timestamp(),current_timestamp(),1);
insert into memberships(ENABLED,CREATION_DATE,DESCRIPTION,FAILED_REVRY_ATMPT_CNT,FAILED_REVRY_ATMPT_WIN_START,FAILED_PWD_ATMPT_CNT,FAILED_PWD_ATMPT_WIN_START,LAST_LOCK_OUT_DATE,LAST_PWD_CHANGE_DATE,LOCKED,PASSWORD,USER_ID,ENCRYPTED,LAST_UPDATE_DATE,VERSION) VALUES (1, current_timestamp(), 'Description of membership', 0, '1754-01-01', 0, '1754-01-01', '1754-01-01', '1754-01-01', 0, @password, @userId, 0,current_time(),1);

set @groupId=(select ifnull(max(id),0)+1 from groups);

insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@groupId,'Administrators','Group of administrators',@applicationId,current_timestamp(),current_timestamp(),1);

insert into group_users(USER_ID,GROUP_ID) VALUES (@userId,@groupId);

set @authorityId=(select ifnull(max(id),0)+1 from authorities);

insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId,'ROLE_ADMIN','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId);

commit;