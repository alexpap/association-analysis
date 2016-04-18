package m112.di.uoa.gr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Pre-processing step in order to tranform movielens item data.
 * Notice that requires IdentityReducer.
 * e.g.
 *  1|Toy Story (1995)|01-Jan-1995|...|
 *  2|GoldenEye (1995)|01-Jan-1995|...|
 *  ...
 *  -->
 *  <1, Toy Story (1995)>
 *  <2, GoldenEye (1995)>
 *  ...
 * @author alexpap
 */
public class LoadMovieLensItem {

    public static class Map extends MapReduceBase
        implements Mapper<Object, Text, LongWritable, Text> {

        @Override
        public void map(
            Object key, Text value,
            OutputCollector<LongWritable, Text> output,
            Reporter reporter) throws IOException {

            StringTokenizer tokenizer = new StringTokenizer(value.toString(), "|");
            if (!tokenizer.hasMoreTokens()){
                throw new IOException("Wrong items format.");
            }
            long id = Long.parseLong(tokenizer.nextToken());

            if (!tokenizer.hasMoreTokens()){
                throw new IOException("Wrong items format.");
            }
            String title = tokenizer.nextToken();

            output.collect(new LongWritable(id), new Text(title));
            
        }
    }
}
