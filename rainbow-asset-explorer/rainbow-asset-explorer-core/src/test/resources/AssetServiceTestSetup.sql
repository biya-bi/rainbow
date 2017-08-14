insert into site(id,name,status,creation_date,last_update_date,version) values (2001,'Site 100','NEW',current_timestamp(),current_timestamp(),1);
insert into product(id,number,name,safety_stock_level,reorder_point,stock_cover,description,creation_date,last_update_date,version) values (2001,'PDT-100','Sample Product 1',1000,750,3,'Product description',current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2001,'ASS-2001','Sample Asset 1','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2002,'ASS-2002','Sample Asset 2','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2003,'ASS-2003','Sample Asset 3','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2004,'ASS-2004','Sample Asset 4','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2005,'ASS-2005','Sample Asset 5','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2006,'ASS-2006','Sample Asset 6','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2007,'ASS-2007','Sample Asset 7','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,creation_date,last_update_date,version) values (2008,'ASS-2008','Sample Asset 8','Asset description',2001,current_timestamp(),current_timestamp(),1);
insert into asset(id,serial_number,name,description,product_id,site_id,creation_date,last_update_date,version) values (2009,'ASS-2009108','Sample Asset 9','Asset description',2001,2001,current_timestamp(),current_timestamp(),1);
commit;