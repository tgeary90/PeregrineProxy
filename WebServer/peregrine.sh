#!/bin/bash

function find_dev()
{
    path=`find /home/$USER -name 'target' -type d|grep WebServer`
    cls_path=$path
    parent=$path
}

parent='.'
cls_path='.'
if [ ! -z $1 ]
then
    if [ $1 == "dev" ]
    then
        find_dev
    else
        parent="$1"
        cls_path="$1"
    fi
fi
exec -a peregrine java -cp $cls_path -Xmx128m -jar ${parent}/Peregrine-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
