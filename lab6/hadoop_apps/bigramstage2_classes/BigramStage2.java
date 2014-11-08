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
	import org.apache.hadoop.mapred.FileInputFormat;
	import org.apache.hadoop.mapred.FileOutputFormat;
	import org.apache.hadoop.mapred.JobClient;
	import org.apache.hadoop.mapred.JobConf;
	import org.apache.hadoop.mapred.MapReduceBase;
	import org.apache.hadoop.mapred.Mapper;
	import org.apache.hadoop.mapred.OutputCollector;
	import org.apache.hadoop.mapred.Reducer;
	import org.apache.hadoop.mapred.Reporter;
	import org.apache.hadoop.mapred.TextInputFormat;
	import org.apache.hadoop.mapred.TextOutputFormat;

	public class BigramStage2 {

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


	       	/*@Override
    		public int hashCode() {
        	// This is used by HashPartitioner, so implement it as per need
        	// this one shall hash based on request id
       		 return bigramText.hashCode();
    		}*/
		}

        public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, BigramWritable> {
            private final static IntWritable one = new IntWritable(1);
            private Text word = new Text();
            private BigramWritable bigram = new BigramWritable();

            public void map(LongWritable key, Text value, OutputCollector<Text, BigramWritable> output, Reporter reporter) throws IOException {
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

                	output.collect(word, bigram);
				}
            }
        }
	
        public static class Reduce extends MapReduceBase implements Reducer<Text, BigramWritable, Text, IntWritable> {
            public void reduce(Text key, Iterator<BigramWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
                // right now this should just print
                // the number of bigrams assoc with each word
                // (good test)

                output.collect(key, new IntWritable(0));
            }
        }
	
	   public static void main(String[] args) throws Exception {
	     JobConf conf = new JobConf(BigramStage2.class);
	     conf.setJobName("bigramstage_2");
	
	     //conf.setOutputKeyClass(Text.class);
	     //conf.setOutputValueClass(BigramWritable.class);
		 // map output types

		 
		 // HAVE TO CHANGE THESE DEPENDING ON REDUCE OUTPUT
		 conf.setOutputKeyClass(Text.class);
		 conf.setOutputValueClass(IntWritable.class);

		 conf.setMapOutputKeyClass(Text.class);
		 conf.setMapOutputValueClass(BigramWritable.class);

	     conf.setMapperClass(Map.class);
	     //conf.setCombinerClass(Reduce.class);
	     conf.setReducerClass(Reduce.class);
	
	     conf.setInputFormat(TextInputFormat.class);
	     conf.setOutputFormat(TextOutputFormat.class);
	
	     FileInputFormat.setInputPaths(conf, new Path(args[0]));
	     FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	
	     JobClient.runJob(conf);
	   }
	}
	
