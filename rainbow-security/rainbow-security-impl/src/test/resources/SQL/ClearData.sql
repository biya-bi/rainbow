delete from group_authorities;
delete from user_authorities;
delete from authorities;
delete from group_users;
delete from groups;
delete from password_histories;
delete from recovery_information;
delete from login_histories;
delete from memberships;
delete from users;
delete from password_policies;
delete from lockout_policies;
delete from login_policies;
delete from applications;
delete from applications_audit;
delete from authorities_audit;
delete from groups_audit;
delete from lockout_policies_audit;
delete from login_policies_audit;
delete from memberships_audit;
delete from password_histories_audit;
delete from password_policies_audit;
delete from recovery_information_audit;
delete from users_audit;
commit;