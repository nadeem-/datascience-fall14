#!/bin/bash
source $HOME/.profile

sudo chown -R nadeem:everyone /Users/nadeem/cloudera/ops/dn
sudo chown -R nadeem:everyone /Users/nadeem/cloudera/ops/nn
sudo chown -R nadeem:everyone /Users/nadeem/cloudera/ops/logs/yarn

/Users/nadeem/cloudera/cdh5.1/hadoop-2.3.0-cdh5.1.2/sbin/stop-dfs.sh
/Users/nadeem/cloudera/cdh5.1/hadoop-2.3.0-cdh5.1.2/sbin/stop-yarn.sh
rm -r /Users/nadeem/cloudera/ops/dn

hdfs datanode & 
hdfs namenode -format

#chown -R nadeem:everyone /Users/nadeem/cloudera/ops/dn
#chown -R nadeem:everyone /Users/nadeem/cloudera/ops/nn

sudo chown -R nadeem:everyone /Users/nadeem/cloudera/ops/dn
sudo chown -R nadeem:everyone /Users/nadeem/cloudera/ops/nn
sudo chown -R nadeem:everyone /Users/nadeem/cloudera/ops/logs/yarn

hdfs namenode &  
yarn resourcemanager & 
yarn nodemanager & 
