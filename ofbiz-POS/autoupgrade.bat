set BASE_APP_DIR=%cd%
set OFBIZ_HOME=%cd%

if not exist "newversion.zip" goto end
  
:shutdownOfbiz
call "%OFBIZ_HOME%\ant.bat" stop
ping /n 20 127.1>nul
  
:unzip  
unzip -o "newversion.zip"   
REM #del "newversion.zip"  /q/f  
  



  
:delwork  
cd %OFBIZ_HOME%  
rd %OFBIZ_HOME%\runtime\catalina\work /s/q  
rd %OFBIZ_HOME%\runtime\tempfiles /s/q  

:startup  
cd %OFBIZ_HOME%  
call ant.bat start

  
:end  

exit