#!/bin/bash
source $HOME/.profile
hdfs namenode &  
hdfs datanode & 
yarn resourcemanager & 
yarn nodemanager & 
mapred historyserver & 
