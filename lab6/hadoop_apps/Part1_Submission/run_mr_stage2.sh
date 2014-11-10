#!/bin/bash
source ~/.profile

javac -classpath ${HADOOP_HOME}/share/hadoop/common/hadoop-common-2.3.0-cdh5.1.2.jar:${HADOOP_HOME}/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.3.0-cdh5.1.2.jar -d bigramstage2_classes BigramStage2.java

cp ./BigramStage2.java ./bigramstage2_classes

jar cvf bigramstage2.jar -C bigramstage2_classes org

hadoop fs -rm -r -f /bigramapp/output

hadoop jar bigramstage2.jar org.myorg.BigramStage2 /bigramapp/tmp /bigramapp/output
