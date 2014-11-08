package org.myorg;

	import java.io.IOException;
	import java.util.*;
	
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.conf.*;
	import org.apache.hadoop.io.*;
	import org.apache.hadoop.mapred.*;
	import org.apache.hadoop.util.*;
	
	public class TopBigrams {
		class Bigram {
			String bigram;
			Integer count;
			Bigram(String bigram, Integer count) {
				this.bigram  = bigram;
				this.count = count;
			}
		}

		class BigramComparator<Bigram> {
			int compare(Bigram b1, Bigram b2) {
				return Integer.compare(b1.count, b2.count);
			}
		}


        HashMap<String, PriorityQueue<Bigram>> wordToBigramList = new HashMap<String, PriorityQueue<Bigram>>();

        public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
            private final static IntWritable one = new IntWritable(1);
            private Text word = new Text();

            public void map(LongWritable key, Text value, OutputCollector output, Reporter reporter) throws IOException {
                String line = value.toString();
                StringTokenizer tokenizer = new StringTokenizer(line);
                if (tokenizer.hasMoreTokens()) {
                	String word1 = tokenizer.nextToken();
                	String word2 = tokenizer.nextToken();
                	String bgStr = word1 + " " + word2;
                	Integer count = Integer.parseInt(tokenizer.nextToken());
                	Bigram bg = new Bigram(bgStr, count);

                	PriorityQueue<Bigram> bigramList1 = wordToBigramList.get(word1);
                	if(bigramList1 == null) {
                		PriorityQueue<Bigram> pq = new PriorityQueue<Bigram>(2, BigramComparator);
                		pq.add(bg);
                		wordToBigram.put(word1, pq);
                	}else {
                		bigramList1.add(bg);
                		wordToBigram.put(word1, bigramList1);
                	}

                	PriorityQueue<Bigram>  bigramList2 = wordToBigramList.get(word2);
                	if(bigramList2 == null) {
                		PriorityQueue<Bigram> pq = new PriorityQueue<Bigram>(2, BigramComparator);
                		pq.add(bg);
                		wordToBigram.put(word2, pq);
                	}else {
                		bigramList2.add(bg);
                		wordToBigram.put(word2, bigramList2);
                	}
                }
            }
        }
	
        public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
            public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
                int sum = 0;
                while (values.hasNext()) {
                    sum += values.next().get();
                }
                output.collect(key, new IntWritable(sum));
            }
        }
	
	   public static void main(String[] args) throws Exception {
	     JobConf conf = new JobConf(TopBigrams.class);
	     conf.setJobName("topbigrams");
	
	     conf.setOutputKeyClass(Text.class);
	     conf.setOutputValueClass(IntWritable.class);
	
	     conf.setMapperClass(Map.class);
	     conf.setCombinerClass(Reduce.class);
	     conf.setReducerClass(Reduce.class);
	
	     conf.setInputFormat(TextInputFormat.class);
	     conf.setOutputFormat(TextOutputFormat.class);
	
	     FileInputFormat.setInputPaths(conf, new Path(args[0]));
	     FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	
	     JobClient.runJob(conf);
	   }
	}
	
