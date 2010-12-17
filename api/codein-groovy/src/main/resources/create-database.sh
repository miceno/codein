#!/bin/bash

#
# This scripts creates a user, and a database with permissions.
#

MYSQLBIN=/Applications/MAMP/Library/bin/mysql
PORT=8889
MY_USERNAME=root
MY_PASS=root

function execute_mysql(){
    cat - | $MYSQLBIN -P $PORT -u $MY_USERNAME --password=$MY_PASS
}

while getopts "u:p:" opt; do

  case $opt in
    u)
      echo "-u was triggered, Parameter: $OPTARG" 
      dbuser="$OPTARG"
      ;;
    p)
      echo "-p was triggered, Parameter: $OPTARG"
      dbpass="$OPTARG"
      ;;
    \?)
      echo "Invalid option: -$OPTARG"
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument."
      exit 1
      ;;
  esac

done

# Clear all options and reset the command line
shift $(( OPTIND -1 ))

# First parameter is the database name
if [ -z "$1" ]; then
    echo "usage: $0 [-u user] [-p password] database"
    exit
fi

# Default values for unset parameters
dbname=$1
[ -z "$dbuser" ] && dbuser="$dbname"
[ -z "$dbpass" ] && dbpass="$dbname"

CHECK_EXIST_DATABASE="SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '$dbname'"
CREATE_DATABASE="create database $dbname"
GRANT_PERMISSIONS="GRANT ALL PRIVILEGES ON \`$dbname\` . * TO '$dbuser'@'%'"
CREATE_USER="CREATE USER '$dbuser'@'%' IDENTIFIED BY '$dbpass'"

echo "CHECK_EXIST_DATABASE=$CHECK_EXIST_DATABASE"
echo "CREATE_DATABASE=$CREATE_DATABASE"
echo "GRANT_PERMISSIONS=$GRANT_PERMISSIONS"
echo "CREATE_USER=$CREATE_USER"

# Check if the database exists
echo "  checking if database exists: $dbname ..."
LINEAS=$( echo "$CHECK_EXIST_DATABASE" | execute_mysql  | wc -l )
if [ "$LINEAS" -gt 0 ]; then
   echo "$0: database $dbname already exists!"
   exit
fi

echo "  creating user: $dbuser ..."
echo "$CREATE_USER" | execute_mysql
echo "  creating database: $dbname ..."
echo "$CREATE_DATABASE" | execute_mysql
echo "$GRANT_PERMISSIONS" | execute_mysql



