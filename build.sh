#!/bin/bash

echo "Creation of executable zip..."

runnable="runnable"

mkdir $runnable

cp "atlutf.jar" $runnable
cp -r "sandbox" $runnable
cp init-MT.sh $runnable

zip -rm "$runnable.zip" "$runnable" > /dev/null