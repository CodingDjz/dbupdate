echo on
call SetEnv.bat
rem ���뵽MySQL5��
cd /d %NEW_MYSQL%\
mysql -u root -f --default-character-set=utf8 ltlndb<"%SQLFILE%"\ImportSQL.sql
