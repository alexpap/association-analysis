package m112.di.uoa.gr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author alexpap
 */
public class InitAprioriGen {

    public static class Map extends MapReduceBase
        implements Mapper<LongWritable, LongArrayWritable, LongWritable, LongWritable>{

        @Override public void map(
            LongWritable key, LongArrayWritable value,
            OutputCollector<LongWritable, LongWritable> output,
            Reporter reporter) throws IOException {

            for (Writable writable : value.get()) {
                output.collect(((LongWritable)writable), new LongWritable(1));
            }
        }
    }

    public static class Reduce extends MapReduceBase
        implements Reducer<LongWritable, LongWritable,LongWritable, LongWritable>{

        private long supportCount = 0;

        @Override public void configure(JobConf job) {
            super.configure(job);
            supportCount = Long.parseLong(job.get("support.count"));
        }

        @Override public void reduce(
            LongWritable key, Iterator<LongWritable> values,
            OutputCollector<LongWritable, LongWritable> output,
            Reporter reporter) throws IOException {

            long sum = 0;
            while (values.hasNext()){
                sum += values.next().get();
            }
            if (sum >= supportCount) {
                output.collect(key, new LongWritable(sum));
            }
        }
    }

}
