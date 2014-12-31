package edu.gatech.cse6242;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
/*
int i1 = 0;
long number = 0;
StringTokenizer st = new StringTokenizer("51    11789    12");
    while (st.hasMoreTokens()) {
        number = Long.valueOf(st.nextToken()).longValue();
        System.out.println(number + 2);
        i1 = 1;
    }

*/



public class Task1 
{

    public static class Task1Mapper extends MapReduceBase
           implements Mapper<LongWritable, Text, LongWritable, LongWritable> 
    { 
    private final static LongWritable src      = new LongWritable(-1);
    private final static LongWritable dst      = new LongWritable(-1);
    private final static LongWritable weight = new LongWritable(-1);
          public void map(LongWritable key, Text val, OutputCollector<LongWritable,
 LongWritable> output, Reporter  reporter)     throws IOException 
           {
        String line = val.toString();
                        StringTokenizer itr = new StringTokenizer(line);
        long number  = 0;
        if (itr.hasMoreTokens()) 
{
                    number = Long.valueOf(itr.nextToken()).longValue();
            src.set (number);
}

if (itr.hasMoreTokens()) 
{
                    number = Long.valueOf(itr.nextToken()).longValue();
            dst.set (number);
}


if (itr.hasMoreTokens()) 
{
                    number = Long.valueOf(itr.nextToken()).longValue();
            weight.set (number);
}

output.collect(dst, weight);
}
}


public static class Task1Reducer extends MapReduceBase implements Reducer<LongWritable, LongWritable, LongWritable, LongWritable> {
    
public void reduce(LongWritable key, Iterator<LongWritable> values, OutputCollector<LongWritable, LongWritable> output, Reporter reporter) throws IOException {
      long sum = 0;
      while (values.hasNext()) {
        sum += values.next().get();
      }
      if (sum != 0)
      {
          output.collect(key, new LongWritable(sum));
      }
    }
  }


  public static void main(String[] args) throws Exception {
    JobConf conf = new JobConf(Task1.class);
    conf.setJobName("Task1");

    // To Do : confirm if tab separator for key value 
conf.setOutputKeyClass(LongWritable.class);
        conf.setOutputValueClass(LongWritable.class);

conf.setMapperClass(Task1Mapper.class);
conf.setReducerClass(Task1Reducer.class);

conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

    FileInputFormat.setInputPaths(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[1]));

    JobClient.runJob(conf);
  }
}


