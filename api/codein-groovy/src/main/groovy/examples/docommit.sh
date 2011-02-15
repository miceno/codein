
DATE=$(date "+%F-%T")
touch prueba.$DATE

ls prueba.$DATE
echo git add prueba.$DATE
echo git commit -m "[TEST]: Test run at $DATE #codein"
