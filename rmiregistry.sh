#!/bin/sh
CUR_PATH=`pwd`
CLASS_PATH=$CUR_PATH/src/
rmiregistry -J-classpath -J$CLASS_PATH 
