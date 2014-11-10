package org.myorg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Partitioner;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/*
This is the second stage for the BigramCount app.
The mapper takes bigrams and maps each word to the bigram, by utlizing custom Writables.
A CompositeKey is used to perform a secondary sort and keep Bigrams by descending order of count.
*/
public class BigramStage2 {

		public static class CompositeKey implements WritableComparable<CompositeKey> {
	       private Text key;
	       private IntWritable count;

	       public CompositeKey()
	       {
	              setKey("");
	              setCount(0);
	       }
	      
	       public CompositeKey(String s, int count)
	       {
	       		setKey(s);
	       		setCount(count);
	       }
	       
	       public void write(DataOutput out) throws IOException {
	         key.write(out);
	         count.write(out);
	       }
	       
	       public void readFields(DataInput in) throws IOException {
	         key.readFields(in);
	         count.readFields(in); 
	       }

	       public String getKey() {
	       		return key.toString();
	       }

	       public IntWritable getCount() {
	       		return count;
	       }

	       public void setKey(String s) {
	       		this.key = new Text(s);
	       }


	       public void setCount(int count) {
	       		this.count = new IntWritable(count);
	       }

		   public int compare(CompositeKey k1, CompositeKey k2) {
				int compareValue = k1.getKey().compareTo(k2.getKey());
				if (compareValue == 0) {
					compareValue = -(k1.getCount().compareTo(k2.getCount()));
				}
				return compareValue;
		   }
	       
		   @Override
		   public int compareTo(CompositeKey o) {
				int compareValue = this.key.toString().compareTo(o.getKey());
				if (compareValue == 0) {
					compareValue = -(count.compareTo(o.getCount()));
				}
				return compareValue;
		   }

	       public int hashCode() {
	         return key.hashCode();
	       }
		}

		public static class BigramWritable implements Writable{
	      
	       private Text bigramText;
	       private IntWritable count;

	       public BigramWritable()
	       {
	              setBigramStr("");
	              setCount(0);
	       }
	      
	       public BigramWritable(String s, Integer count)
	       {
	       		setBigramStr(s);
	       		setCount(count);
	       }

	       public void readFields(DataInput in) throws IOException {
 				bigramText.readFields(in);
 				count.readFields(in);
	       }

	       public void write(DataOutput out) throws IOException {
				bigramText.write(out);
				count.write(out);
	       }

	       public String getBigramStr() {
	       		return bigramText.toString();
	       }

	       public int getCount() {
	       		return count.get();
	       }

	       public void setBigramStr(String s) {
	       		this.bigramText = new Text(s);
	       }


	       public void setCount(int count) {
	       		this.count = new IntWritable(count);
	       }

		}

		public static class CompositeKeyComparator extends WritableComparator {
			protected CompositeKeyComparator() {
				super(CompositeKey.class, true);
			}
			@Override
			public int compare(WritableComparable w1, WritableComparable w2) {
				CompositeKey ip1 = (CompositeKey) w1;
				CompositeKey ip2 = (CompositeKey) w2;
				return ip1.compareTo(ip2);
			}
		}


		public static class CompositeKeyPartitioner implements Partitioner<CompositeKey, BigramWritable> {
			public void configure(JobConf job) {
			}

		   	public int getPartition(BigramStage2.CompositeKey key, BigramStage2.BigramWritable value,
				int numPartitions) {
		        return key.getKey().hashCode() % numPartitions;
			}
		}

		public static class KeyGroupingComparator extends WritableComparator {
			protected KeyGroupingComparator() {
				super(CompositeKey.class, true);
			}
			@Override
			public int compare(WritableComparable w1, WritableComparable w2) {
				CompositeKey ip1 = (CompositeKey) w1;
				CompositeKey ip2 = (CompositeKey) w2;
				String s1 = ip1.getKey();
				String s2 = ip2.getKey();

				return s1.compareTo(s2);
			}
		}

        public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, CompositeKey, BigramWritable> {
            private final static IntWritable one = new IntWritable(1);
            private Text word = new Text();
            private BigramWritable bigram = new BigramWritable();
            private CompositeKey compositeKey = new CompositeKey();

            public void map(LongWritable key, Text value, OutputCollector<CompositeKey, BigramWritable> output, Reporter reporter) throws IOException {
    	        String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);

                while (tokenizer.hasMoreTokens()) {
                	String word1 = tokenizer.nextToken();
                	String word2 = tokenizer.nextToken();
                	String bgStr = word1 + " " + word2;
                	Integer count = Integer.parseInt(tokenizer.nextToken());
                	//Bigram bg = new Bigram(bgStr, count);

                	bigram.setBigramStr(bgStr);
                	bigram.setCount(count);       
					word.set(word1);

					compositeKey.setKey(word1);
					compositeKey.setCount(count);

                	output.collect(compositeKey, bigram);
				}
            }
        }

       public static class Reduce extends MapReduceBase implements Reducer<CompositeKey, BigramWritable, Text, Text> {
            private Text text = new Text();

            public void reduce(CompositeKey key, Iterator<BigramWritable> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
                StringBuilder sb = new StringBuilder();
            	int sum = 0;

            	// collect the top 5 bigrams as output 
            	// (they are already sorted by count at this point)
            	for(int i = 0; i < 5; i++) {
            		if(values.hasNext()) {
            			BigramWritable bg = values.next();
 		           		sb.append(bg.getBigramStr() + " (" + bg.getCount() + ") ");
            		}
            	}

            	String s = sb.toString();
            	text.set(s);

            	output.collect(new Text(key.getKey()), text);
            }
        }
	
	   public static void main(String[] args) throws Exception {
	     	JobConf conf = new JobConf(BigramStage2.class);
	   	  	conf.setJobName("bigramstage_2");


			// Reduce output
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(Text.class);

			// Map output
			conf.setMapOutputValueClass(BigramWritable.class);
			conf.setMapOutputKeyClass(CompositeKey.class);

  			conf.setPartitionerClass(CompositeKeyPartitioner.class);
  			conf.setOutputValueGroupingComparator(KeyGroupingComparator.class);
  			conf.setOutputKeyComparatorClass(CompositeKeyComparator.class);

		     conf.setMapperClass(Map.class);
		     conf.setReducerClass(Reduce.class);
		
		     conf.setInputFormat(TextInputFormat.class);
		     conf.setOutputFormat(TextOutputFormat.class);
		
		     FileInputFormat.setInputPaths(conf, new Path(args[0]));
		     FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		
		     JobClient.runJob(conf);
	   }
}
	
