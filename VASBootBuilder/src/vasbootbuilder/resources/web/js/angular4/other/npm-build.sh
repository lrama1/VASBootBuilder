#!/bin/bash
echo "hello, $USER. I wish to list some files of yours"
echo "listing files in the current directory, $PWD"

cd src/ui
echo "Executing npm in $PWD"
npm install
npm run build
cd dist
echo "Currently in $PWD"
cp * ../../main/resources/public
