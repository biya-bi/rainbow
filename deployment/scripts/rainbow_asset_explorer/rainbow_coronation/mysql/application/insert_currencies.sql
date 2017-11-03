USE `rainbow_coronation_stock_management`;

start transaction;
insert into currency(name,symbol,creation_date,last_update_date,creator,updater) values ('Naira','N',current_timestamp(),current_timestamp(),'SYSTEM','SYSTEM');
commit;