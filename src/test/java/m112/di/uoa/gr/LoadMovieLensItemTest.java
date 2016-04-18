package m112.di.uoa.gr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mrunit.MapDriver;
import org.apache.hadoop.mrunit.MapReduceDriver;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Tesing the Map phase using ml-100k.
 * @author alexpap
 */
public class LoadMovieLensItemTest {

    MapDriver<Object, Text, LongWritable, Text> mapDriver;

    @Before public void setUp() throws Exception {

        mapDriver = new MapDriver<Object, Text, LongWritable, Text>();
        mapDriver.setMapper(new LoadMovieLensItem.Map());
    }

    @Test public void testMapper() throws Exception {
        String path =
            LoadMovieLensItemTest.class.getClassLoader().getResource("ml-100k.zip").getPath();
        List<String> items = MovieLensDataSet.getInstance().loadFile(path, "u.item");
        for (int i = 0; i < items.size(); i ++){
            String item = items.get(i);
            mapDriver.withInput(NullWritable.get(), new Text(item));
            int s = item.indexOf("|"), e = item.indexOf("|", s +1);
            mapDriver.withOutput(new LongWritable(Long.parseLong(item.substring(0,s))), new Text(item.substring(s+1,e)));
        }
        mapDriver.runTest();
    }
}
