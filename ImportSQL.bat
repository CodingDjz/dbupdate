echo on
call SetEnv.bat
rem 导入到MySQL5中
cd /d %NEW_MYSQL%\
mysql -u root -f --default-character-set=utf8 ltlndb<"%SQLFILE%"\ImportSQL.sql
