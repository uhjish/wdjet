#!/bin/sh

PROJECT_PATH=`pwd`

rmic -d src/ -classpath lib/colt.jar:lib/concurrent.jar:lib/jung-1.7.6.jar:lib/commons-collections-3.2.jar:lib/commons-math-1.2.jar:lib/mysql-connector-java-3.1.13-bin.jar:lib/prefuse.jar:src/ org.omelogic.hocuslocus.wdjet.server.WdjetServiceServer

pwd

java -Xms64m -Xmx2048m -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy -Djava.rmi.server.codebase=file://$PROJECT_PATH/src/ -cp src/:lib/colt.jar:lib/concurrent.jar:lib/jung-1.7.6.jar:lib/commons-collections-3.2.jar:lib/commons-math-1.2.jar:lib/mysql-connector-java-3.1.13-bin.jar:lib/prefuse.jar:src/:./ org.omelogic.hocuslocus.wdjet.server.WdjetServiceHost

