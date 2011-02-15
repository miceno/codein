
DATE=$(date "+%F-%T")
touch prueba.$DATE

ls prueba.$DATE
git add prueba.$DATE
git commit -m "[TEST]: Test run at $DATE #codein"
