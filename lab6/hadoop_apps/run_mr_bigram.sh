#!/bin/bash
source ~/.profile

# compile the bigram programs
javac -classpath ${HADOOP_HOME}/share/hadoop/common/hadoop-common-2.3.0-cdh5.1.2.jar:${HADOOP_HOME}/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.3.0-cdh5.1.2.jar -d bigramcount_classes BigramCount.java
javac -classpath ${HADOOP_HOME}/share/hadoop/common/hadoop-common-2.3.0-cdh5.1.2.jar:${HADOOP_HOME}/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.3.0-cdh5.1.2.jar -d bigramstage2_classes BigramStage2.java

# copy over the class files
cp ./BigramCount.java ./bigramcount_classes
cp ./BigramStage2.java ./bigramstage2_classes

# put it in a jar
jar cvf bigramcount.jar -C bigramcount_classes org
jar cvf bigramstage2.jar -C bigramstage2_classes org

hadoop fs -mkdir /bigramapp
hadoop fs -mkdir /bigramapp/input

hadoop fs -rm  -f /bigramapp/input/test.txt/
hadoop fs -put test.txt /bigramapp/input/ 

hadoop fs -rm -r -f /bigramapp/tmp
hadoop fs -rm -r -f /bigramapp/output

# run the bigram app
hadoop jar bigramcount.jar org.myorg.BigramCount /bigramapp/input /bigramapp/tmp
hadoop jar bigramstage2.jar org.myorg.BigramStage2 /bigramapp/tmp /bigramapp/output
