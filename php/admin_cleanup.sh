mysql -uroot -pcmsc495fall  -e "show databases" | grep -v Database | grep -v mysql | grep -v information_schema | grep -v performance_schema | gawk '{print "drop database " $1 ";select sleep(0.1);"}' | mysql -uroot -pcmsc495fall