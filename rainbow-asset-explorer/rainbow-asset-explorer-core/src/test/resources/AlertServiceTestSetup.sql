insert into schedule(id,creation_date,last_update_date,version) values (1001,current_timestamp(),current_timestamp(),1);
insert into alert(id,alert_category,alert_type,is_enabled,schedule_id,is_immediate,creation_date,last_update_date,version) values (1001,'STOCK_LEVEL','WARNING',1,1001,1,current_timestamp(),current_timestamp(),1);
commit;