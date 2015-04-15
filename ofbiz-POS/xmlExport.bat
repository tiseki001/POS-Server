set BASE_APP_DIR=%cd%
set TIME=%Date:~0,4%%Date:~5,2%%Date:~8,2% 
cd..
cd.. 
set mysqlpath=%cd%
ping /n 2 127.1>nul
cd..
cd %BASE_APP_DIR% 
zip -r -m  %mysqlpath%\xmlExportBackup%TIME% %mysqlpath%\xmlExport

:end
exit