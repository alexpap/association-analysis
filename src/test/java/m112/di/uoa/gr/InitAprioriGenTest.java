package m112.di.uoa.gr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mrunit.MapReduceDriver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author alexpap
 */
public class InitAprioriGenTest {

    MapReduceDriver<LongWritable, LongArrayWritable, LongWritable, LongWritable,LongWritable, LongArrayWritable> mapReduceDriver;

    @Before public void setUp() throws Exception {

        mapReduceDriver = new MapReduceDriver<LongWritable, LongArrayWritable, LongWritable, LongWritable, LongWritable, LongArrayWritable>();
        mapReduceDriver.setMapper(new InitAprioriGen.Map());
        mapReduceDriver.setCombiner(new InitAprioriGen.Reduce());
        Configuration configuration = mapReduceDriver.getConfiguration();
        configuration.set("supportCount", "1");
    }

    @Test public void testMapReduce() throws Exception {
        //TODO prepare baskets and results for validation.
    }
}
