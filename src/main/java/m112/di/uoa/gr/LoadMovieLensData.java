package m112.di.uoa.gr;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Pre-processing step in order to tranform movielens data.
 * e.g.
 *  225	237	5	879539643
 *  299	229	3	878192429
 *  225	480	5	879540748
 *  ...
 *  -->
 *  <225, [237, 480]>
 *  <299, [480]>
 *  ...
 * @author alexpap
 */
public class LoadMovieLensData {

    public static class Map extends MapReduceBase
        implements Mapper<Object, Text, LongWritable, LongWritable> {

        @Override
        public void map(
            Object key, Text value,
            OutputCollector<LongWritable, LongWritable> output,
            Reporter reporter) throws IOException {

            StringTokenizer tokenizer = new StringTokenizer(value.toString(), "\t");
            if (!tokenizer.hasMoreTokens()){
                throw new IOException("Wrong items format.");
            }
            long uid = Long.parseLong(tokenizer.nextToken());

            if (!tokenizer.hasMoreTokens()){
                throw new IOException("Wrong items format.");
            }
            long mid = Long.parseLong(tokenizer.nextToken());

            output.collect(new LongWritable(uid), new LongWritable(mid));
        }
    }

    public static class Reduce extends MapReduceBase
        implements Reducer<LongWritable, LongWritable, LongWritable, LongArrayWritable> {

        @Override public void reduce(
            LongWritable key, Iterator<LongWritable> values,
            OutputCollector<LongWritable, LongArrayWritable> output,
            Reporter reporter) throws IOException {

            HashSet<LongWritable> itemset = new HashSet<LongWritable>();
            while (values.hasNext()){
                itemset.add(values.next());
            }
            output.collect(key, new LongArrayWritable(itemset.toArray(new Writable[itemset.size()])));
        }

    }
}
