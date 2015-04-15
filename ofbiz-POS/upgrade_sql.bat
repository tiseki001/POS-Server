set BASE_APP_DIR=%cd%
set OFBIZ_HOME=%cd%

if not exist "upgrade.sql" goto end
cd..  
if not exist "%cd%\mysql" goto end

set MYSQL_HOME="%cd%\mysql"  
cd "%MYSQL_HOME%\bin"  
mysql -uroot -pofbiz -Dquicklms -s -e "source %OFBIZ_HOME%\upgrade.sql"  
del %OFBIZ_HOME%\upgrade.sql /q/f

:end