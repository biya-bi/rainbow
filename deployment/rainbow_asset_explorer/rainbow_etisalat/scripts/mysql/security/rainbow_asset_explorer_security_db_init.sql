start transaction;
set @applicationId=(select ifnull(max(id),0)+1 from applications);
set @applicationName='Etisalat Asset Management';
set @applicationDescription='Application for managing Etisalat''s assets';

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
set @groupName='Administrators';
set @groupDescription='Group of administrators';

insert into groups(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@groupId,@groupName,@groupDescription,@applicationId,current_timestamp(),current_timestamp(),1);

set @authorityId=(select ifnull(max(id),0)+1 from authorities);

insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId,'ROLE_READ_AUDIT_DETAILS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+1,'ROLE_READ_ALERTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+2,'ROLE_WRITE_ALERTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+3,'ROLE_READ_VENDORS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+4,'ROLE_WRITE_VENDORS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+5,'ROLE_READ_SITES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+6,'ROLE_WRITE_SITES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+7,'ROLE_READ_ASSETS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+8,'ROLE_WRITE_ASSETS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+9,'ROLE_READ_ASSET_TYPES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+10,'ROLE_WRITE_ASSET_TYPES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+11,'ROLE_READ_EMAIL_RECIPIENTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+12,'ROLE_WRITE_EMAIL_RECIPIENTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+13,'ROLE_READ_EMAIL_TEMPLATES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+14,'ROLE_WRITE_EMAIL_TEMPLATES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+15,'ROLE_READ_LOCALES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+16,'ROLE_WRITE_LOCALES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+17,'ROLE_READ_LOCATIONS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+18,'ROLE_WRITE_LOCATIONS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+19,'ROLE_READ_MANUFACTURERS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+20,'ROLE_WRITE_MANUFACTURERS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+21,'ROLE_READ_PRODUCT_ISSUES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+22,'ROLE_WRITE_PRODUCT_ISSUES','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+23,'ROLE_READ_PRODUCT_RECEIPTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+24,'ROLE_WRITE_PRODUCT_RECEIPTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+25,'ROLE_READ_PRODUCTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+26,'ROLE_WRITE_PRODUCTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+27,'ROLE_READ_PURCHASE_ORDERS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+28,'ROLE_WRITE_PURCHASE_ORDERS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+29,'ROLE_READ_SHIP_METHODS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+30,'ROLE_WRITE_SHIP_METHODS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+31,'ROLE_READ_SHIPPING_ORDERS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+32,'ROLE_WRITE_SHIPPING_ORDERS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+33,'ROLE_READ_SITE_DOCUMENTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+34,'ROLE_WRITE_SITE_DOCUMENTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+35,'ROLE_READ_ASSET_DOCUMENTS','',@applicationId,current_timestamp(),current_timestamp(),1);
insert into authorities(ID,NAME,DESCRIPTION,APPLICATION_ID,creation_date,last_update_date,version) VALUES (@authorityId+36,'ROLE_WRITE_ASSET_DOCUMENTS','',@applicationId,current_timestamp(),current_timestamp(),1);

insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+1);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+2);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+3);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+4);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+5);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+6);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+7);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+8);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+9);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+10);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+11);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+12);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+13);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+14);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+15);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+16);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+17);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+18);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+19);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+20);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+21);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+22);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+23);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+24);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+25);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+26);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+27);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+28);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+29);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+30);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+31);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+32);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+33);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+34);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+35);
insert into group_authorities(GROUP_ID,AUTHORITY_ID) VALUES (@groupId,@authorityId+36);

insert into group_users(USER_ID,GROUP_ID) VALUES (@userId,@groupId);
commit;