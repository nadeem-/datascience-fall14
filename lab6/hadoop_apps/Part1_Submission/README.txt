# In lab6 folder:

// To restart hadoop services, use the restart script
chmod 777 ./restart.sh
./restart.sh


# In hadoop_apps folder:

// To run the part1 (bigram) app 
sudo run_mr_bigram.sh

// To run only stage2: sudo run_mr_stage2.sh

// view output
hadoop fs -cat /bigramapp/output/part-00000


