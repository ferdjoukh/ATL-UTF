#!/bin/bash

# This script initializes the folder of a model transformation

# USAGE
# 	init-MT.sh folder

# PARAMS
#	$1 folder containing a model transformation

# RETURN
#	creates metamodels/ folder if not existing
#	creates models/ folder if not existing
#	creates a pre-filled folder.infos file

if [ ! "$1" = "" ]; then

	if [ -d "$1" ]; then
		echo "[OK] Model transformation is contained in folder [$1]"

		MT=$(echo ${1##*/})

		# Create metamodels folder
		if [ ! -d "$1/metamodels" ]; then
			mkdir "$1/metamodels"
			mkdir "$1/metamodels/input"
			mkdir "$1/metamodels/output"

			echo "[OK] metamodels folders created"
		fi

		#create models folders
		if [ ! -d "$1/models" ]; then
			mkdir "$1/models"
			mkdir "$1/models/input"
			mkdir "$1/models/output"
			mkdir "$1/models/trace"

			echo "[OK] models folders created"
		fi

		#create mt.infos file
		if [ ! -f "$1/$1.infos" ]; then
			:> "$1/$MT.infos"
			echo "Transformation name=MT" >> "$1/$MT.infos"
			echo "module=MOD" >> "$1/$MT.infos"
			echo "inMM=MM1" >> "$1/$MT.infos"
			echo "inMMRelativePath=MM1.ecore" >> "$1/$MT.infos"
			echo "rootClass=class" >> "$1/$MT.infos"			
			echo "outMM=MM2" >> "$1/$MT.infos"
			echo "outMMRelativePath=MM2.ecore" >> "$1/$MT.infos"

			echo "[OK] infos files [$MT.infos] created"
		fi


	else
		echo "[$1] folder does not exist !"
	fi
else
	echo "Please give a folder that contains the transformation"
fi
