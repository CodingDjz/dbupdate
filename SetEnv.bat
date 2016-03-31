@echo off

call SetPath.bat

set BASE_HOME=%~dp0
rem set OLD_NMS=D:\XView\XView 1.6.2.1211
rem set NEW_NMS=D:\XView\XView 16.0318

rem java环境
set JAVA_HOME=%BASE_HOME%\jre
set PATH=%BASE_HOME%\lib;%PATH%
set CLASSPATH=%BASE_HOME%\DBupdate.jar;%BASE_HOME%\lib;;%CLASSPATH%

rem 其他环境
set OLD_MYSQL=%OLD_NMS%\MYSQL\bin
set NEW_MYSQL=%NEW_NMS%\MYSQL\bin
set SQLFILE=%BASE_HOME%\SQLFile
