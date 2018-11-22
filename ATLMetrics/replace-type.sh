#!/bin/bash

# PARAMS
#  $1 ecore file to change

sed -re 's/#\/0\/String/ecore:EDataType http:\/\/www.eclipse.org\/emf\/2002\/Ecore#\/\/EString/g' "$1" > "$1-2"
sed -re 's/#\/0\/Integer/ecore:EDataType http:\/\/www.eclipse.org\/emf\/2002\/Ecore#\/\/EInt/g' "$1-2" > "$1-3"

#sed -re 's/\/1/#\/0/g' "$1-3" > "$1-4"