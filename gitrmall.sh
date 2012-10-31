#!/bin/bash
for i in $(git status | grep deleted | sed "s/#.*deleted: //")
do

git rm  $i
#echo $i
done
