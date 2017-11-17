use `rainbow_journal`;

start transaction;

set @profileId=(select ifnull(max(id),0)+1 from profile);
set @userName='admin';
set @lastName='Admin';

insert into profile(id,user_name,first_name,last_name,creation_date,last_update_date,version) values (@profileId,@userName,'',@lastName,current_timestamp(),current_timestamp(),1);

commit;
