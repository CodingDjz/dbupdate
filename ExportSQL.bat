echo off
call SetEnv.bat
rem ����MySQL4����
cd /d %OLD_MYSQL%
mysqldump -u root -c --skip-lock-table --default-character-set=latin1 --set-charset=utf8 --skip-opt ltlndb>"%SQLFILE%"\ExportSQL.sql
cd /d %BASE_HOME%