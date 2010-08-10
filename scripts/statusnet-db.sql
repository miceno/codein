# Create Database user name
create user $USERNAME;

# Database name
create database $DATABASE;

# Create permission and assign password
grant all on $DATABASE.* to '$USERNAME'@'localhost' identified by 'social';

