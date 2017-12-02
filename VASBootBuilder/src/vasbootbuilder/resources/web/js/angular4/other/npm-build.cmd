echo "hello, %USERNAME%. I wish to list some files of yours"
echo "listing files in the current directory, %CD%"

cd src\ui
echo "Executing npm in %CD%"
echo "**************************RUNNING INSTALL*************************"
call npm install
echo "**************************RUNNING BUILD*************************"
call npm run build
echo "**************************DONE RUNNING BUILD*************************"
cd dist
echo "Currently in %CD%"
copy * ..\..\main\resources\public


