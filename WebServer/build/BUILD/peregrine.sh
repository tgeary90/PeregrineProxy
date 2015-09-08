#!/bin/bash

if [ -z $1 ]
then
    parent='.'
else
    parent=~/.m2/repository/tom/msc/PeregrineServer/1.0-SNAPSHOT/
fi

if [ -z $2 ]
then
    cls_path=".:$parent"
else
    cls_path=".:~/.m2/repository/tom/msc/peregrine/CacheBypassPlugins/1.0-SNAPSHOT/CacheBypassPlugins-1.0-SNAPSHOT.jar"
fi
exec -a peregrine java -cp $cls_path -Xmx128m -jar ${parent}/PeregrineServer-1.0-SNAPSHOT-jar-with-dependencies.jar 
