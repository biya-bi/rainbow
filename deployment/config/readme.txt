1. Deploying the Security Application
       The default schema name in the deployment script files for the Security Application is rainbow_security. If a different schema name is to be used, 
       the first (and possibly second) lines of all the deployment script files should be changed accordingly.
	a) Creating the Security Database
	       - Run deployment/scripts/rainbow_security/mysql/application/create_database.sql
	       - Run deployment/scripts/rainbow_security/mysql/application/create_tables.sql
	       - Run deployment/scripts/rainbow_security/mysql/security/rainbow_security_db_init.sql. This will among other things create a user whose username and password
	      are both admin. This user can be used to login to the application once the application has been deployed. The application will prompt the user to change the
	      password on first logon.
	b) Deploy the deployment/wars/rainbow-security-ui-0.0.1-SNAPSHOT.war file on Tomcat 8.
2. Deploying the Rainbow Asset Explorer Application
       The default schema name in the deployment script files for the Rainbow Asset Explorer Application is rainbow_asset_explorer. If a different schema name is to be used, 
       the first (and possibly second) lines of all the deployment script files should be changed accordingly.
	a) Creating the Rainbow Asset Explorer Database
	       - Run deployment/scripts/rainbow_asset_explorer/rainbow_asset_explorer_ui/mysql/application/create_database.sql
	       - Run deployment/scripts/rainbow_asset_explorer/rainbow_asset_explorer_ui/mysql/application/create_quartz-2.2.3_tables.sql
	       - Run deployment/scripts/rainbow_asset_explorer/rainbow_asset_explorer_ui/mysql/application/create_tables.sql
	       - Run deployment/scripts/rainbow_asset_explorer/rainbow_asset_explorer_ui/mysql/application/insert_currencies.sql
	       - Run deployment/scripts/rainbow_asset_explorer/rainbow_asset_explorer_ui/mysql/application/insert_departments.sql
	       - Run deployment/scripts/rainbow_asset_explorer/rainbow_asset_explorer_ui/mysql/security/rainbow_asset_explorer_security_db_init.sql on the Security Database.
	      If the name of the Security schema was changed, the first name of this file will have to be adjusted accordingly. This will among other things create a user 
	      whose username and password are both admin. This user can be used to login to the application once the application has been deployed. The application will 
	      prompt the user to change the password on first logon.
	b) Deploy the deployment/wars/rainbow-asset-explorer-ui-0.0.1-SNAPSHOT.war file on Tomcat 8.
3. Deploying the Rainbow Journal Application
       The default schema name in the deployment script files for the Rainbow Asset Explorer Application is rainbow_journal. If a different schema name is to be used, 
       the first (and possibly second) lines of all the deployment script files should be changed accordingly.
	a) Creating the Rainbow Asset Explorer Database
	       - Run deployment/scripts/rainbow_journal/mysql/application/create_database.sql
	       - Run deployment/scripts/rainbow_journal/mysql/application/create_tables.sql
	       - Run deployment/scripts/rainbow_journal/mysql/application/insert_profiles.sql
	       - Run deployment/scripts/rainbow_journal/mysql/security/rainbow_journal_security_db_init.sql on the Security Database.
	      If the name of the Security schema was changed, the first name of this file will have to be adjusted accordingly. This will among other things create a user 
	      whose username and password are both admin. This user can be used to login to the application once the application has been deployed. The application will 
	      prompt the user to change the password on first logon.
	b) Deploy the deployment/wars/rainbow-journal-ui-0.0.1-SNAPSHOT.war file on Tomcat 8.
4. Tomcat 8 configurations
	a) Create resources declared in the deployment/config/tomcat8/context.xml file on Tomcat 8, changing the passwords where necessary.
5. Restart Tomcat 8.
6. Running automated tests
       - The projects rainbow-asset-explorer-impl, rainbow-security-impl and rainbow-journal-impl have automated tests. Those tests make 
         use of a MySQL database whose connection settings are defined in the src/test/resources/META-INF/application.properties files of the respective projects.
         The src/test/resources/META-INF/application.properties file of each project should be modified accordingly to be able to run the automated tests on a new
         environment.
