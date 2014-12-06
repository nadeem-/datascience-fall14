/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.umd.assignment;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.umd.assignment.spout.RandomSentenceSpout;
import org.umd.assignment.spout.TwitterSampleSpout;

import java.util.HashSet;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Integer;





/**
 * This topology demonstrates Storm's stream groupings and multilang capabilities.
 */
public class WordCountTopology {
  public static class SplitSentence extends ShellBolt implements IRichBolt {

    public SplitSentence() {
      super("python", "splitsentence.py");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
      return null;
    }
  }

	public static class Word implements Comparable<Word>{
		public String text;
		public int count;
		public Word(String text, int count) {
			this.text = text;
			this.count = count;
		}

		public int compareTo(Word other) {
			return - ((Integer)count).compareTo(other.count);
		}
	}

  public static class WordCount extends BaseBasicBolt {
    Map<String, Integer> counts = new HashMap<String, Integer>();
    HashSet<String> stopWords = new HashSet<String>();

    public void setStopWords(HashSet<String> stopWords) {
    	this.stopWords = stopWords;
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
      
		// ----------------- Task 2	---------------------------------
		//
		//
		//	Modify this code to exclude stop-words from counting.
		//  Stopword list is provided in the lab folder. 
		//
		//
		// ---------------------------------------------------------


		String word = tuple.getString(0);
		if(!stopWords.contains(word)) {
			Integer count = counts.get(word);
			if (count == null)
				count = 0;
			count++;
			counts.put(word, count);
			collector.emit(new Values(word, count));
		}
    }

	@Override
	public void cleanup()
	{
		// ------------------------  Task 3 ---------------------------------------
		//
		//
		//	This function gets called when the Stream processing finishes.
		//	MODIFY this function to print the most frequent words that co-occur 
		//	with Obama [The TwitterSimpleSpout already gives you Tweets that contain
		//  the word obama].
		//
		//	Since multiple threads will be doing the same cleanup operation, writing the
		//	output to a file might not work as desired. One way to do this would be
		//  print the output (using System.out.println) and do a grep/awk/sed on that.
		//  For a simple example see inside the runStorm.sh.
		//
		//--------------------------------------------------------------------------
		
		// top 10 words co-occuring with Obama
		PriorityQueue<Word> pq = new PriorityQueue<Word>(10);

		for(String key : counts.keySet()) {
			pq.add(new Word(key, counts.get(key)));
		}

		for(int i = 1; i <= 10; i++) {
			Word w = pq.poll();
			System.out.println(w.text + " " + w.count);
		}
	}

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word", "count"));
    }
  }

  public static void main(String[] args) throws Exception {

    TopologyBuilder builder = new TopologyBuilder();

	// ---------------------------- Task 1 -------------------------------------
	//
	//		You need to use TwitterSampleSpout() for the assignemt. But, it won't work
	//		unless you set up the access token correctly in the TwitterSampleSpout.java
	//
	//		RandomSentenceSpout() simply spits out a random sentence. 
	//
	//--------------------------------------------------------------------------

	// Setting up a spout
    //builder.setSpout("spout", new RandomSentenceSpout(), 3);
    builder.setSpout("spout", new TwitterSampleSpout(), 3);

	// Setting up bolts
    builder.setBolt("split", new SplitSentence(), 3).shuffleGrouping("spout");

    // read in the stop words file
    HashSet<String> stopWords = new HashSet<String>();

    BufferedReader br = new BufferedReader(
    	new FileReader("/Users/nadeem/Desktop/Classes/datascience-fall14/lab8/Stopwords.txt"));
    String line;

    while((line = br.readLine()) != null) {
    	String word = line.trim();
    	stopWords.add(word);
    }


    WordCount wc = new WordCount();
    wc.setStopWords(stopWords);

    builder.setBolt("count", wc, 3).fieldsGrouping("split", new Fields("word"));

    Config conf = new Config();
    conf.setDebug(true);


    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);

      StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
    }
    else {
      conf.setMaxTaskParallelism(3);

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("word-count", conf, builder.createTopology());

	  // --------------------------- Task 4 ---------------------------------
	  //
	  //	The sleep time simply indicates for how long you want to keep your
	  //	system up and running. 10000 (miliseconds) here means 10 seconds.
	  // 	
	  //
	  // ----------------------------------------------------------------------

      //Thread.sleep(10000);
      Thread.sleep(600000);

      cluster.shutdown(); // blot "cleanup" function is called when cluster is shutdown (only works in local mode)
    }
  }
}
