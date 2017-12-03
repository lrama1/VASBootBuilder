
echo "hello,  %USERNAME%. I wish to list some files of yours"
echo "listing files in the current directory, %CD%"

cd src\ui
echo "Executing npm in %CD%"
call npm install
call npm run build
cd dist
echo "Currently in %CD%"
copy index.html ..\..\main\resources\public
xcopy static ..\..\main\resources\public