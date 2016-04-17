package m112.di.uoa.gr;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author alexpap
 *
 */
public class WordCountApp {

    public static class Map extends MapReduceBase
        implements Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        public void map(
            LongWritable key, Text value,
            OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException {

            /**
             * In this class we ignore the key. The key value here is a byte offset
             * into the input file. We don't need that information so we don't need
             * to touch it.
             */

            // Read in one full line of text from the value
            String s = value.toString();

            // Split the string on non-word characters (ie. spaces, commas, etc)
            for (String word : s.split("\\W+")) {
                // Is this word longer than zero characters?
                if (word.length() > 0) {
                    /**
                     * Yes, output the Text key (the word) and the number 1 since
                     * this is a single instance of it
                     */
                    output.collect(new Text(word), new IntWritable(1));
                }
            }
        }
    }


    public static class Reduce extends MapReduceBase
        implements Reducer<Text, IntWritable, Text, IntWritable> {

        @Override public void reduce(
            Text key, Iterator<IntWritable> values,
            OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {

            // Initialize our word count to zero
            int wordCount = 0;

            // Do we have any more IntWritable values for this Text key?
            while (values.hasNext()) {
                // Get the IntWritable value
                IntWritable value = values.next();

                /**
                 * NOTE: You cannot loop through the keys and just add one and
                 * ignore the IntWritable value. This is because a combiner could
                 * run before this reducer or this class might be used in a
                 * different job that doesn't behave as our WordMapper class does.
                 * From the WordMapper class we expect that our values are all going
                 * to be the number "1" but making this assumption is bad practice
                 * and will break in more advanced scenarios or when your code gets
                 * reused. Never depend on that kind of behavior. Always use the
                 * values you are given.
                 */

                // Add it to the word count
                wordCount += value.get();
            }

            // Output the Text key (the word) with the calculated word count
            output.collect(key, new IntWritable(wordCount));
        }
    }

}
