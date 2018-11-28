#!/bin/bash

# $1: root name of xmi files
# $2: folder path

if [ ! "$1" = "" -a ! "$2" = "" ]; then

	echo "Looking for '$1' xmi files in folder '$2'"

	mkdir "Res-$1"

	models=$(find $2 -name "*$1*.xmi")

	for i in $models; do
		cp $i "Res-$1"
	done
	
else

	echo "Give the root name of xmi files to find and/or folder"

fi