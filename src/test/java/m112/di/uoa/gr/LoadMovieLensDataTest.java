package m112.di.uoa.gr;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.MapReduceDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing MapReduce phase using the ml-100k.
 * @author alexpap
 */
public class LoadMovieLensDataTest {

    MapReduceDriver<Object, Text, LongWritable, LongWritable, LongWritable, LongArrayWritable> mapReduceDriver;

    @Before public void setUp() throws Exception {
        mapReduceDriver =
            new MapReduceDriver<Object, Text, LongWritable, LongWritable, LongWritable, LongArrayWritable>();
        mapReduceDriver.setMapper(new LoadMovieLensData.Map());
        mapReduceDriver.setReducer(new LoadMovieLensData.Reduce());
    }

    @Test public void testMapReduce() throws Exception {

        String path =
            LoadMovieLensItemTest.class.getClassLoader().getResource("ml-100k.zip").getPath();
        List<String> items = MovieLensDataSet.getInstance().loadFile(path, "u.data");
        HashMap<Long, HashSet<Long>> baskets = new HashMap<Long, HashSet<Long>>();
        for (int i = 0; i < items.size(); i ++){
            String item = items.get(i);
            mapReduceDriver.withInput(NullWritable.get(), new Text(item));

            String[] split = item.split("\t");
            Long uid = Long.parseLong(split[0]);
            Long mid = Long.parseLong(split[1]);

            HashSet<Long> itemset = baskets.get(uid);
            if (itemset == null){
                itemset = new HashSet<Long>();
                baskets.put(uid, itemset);
            }
            itemset.add(mid);
        }
        for (Pair<LongWritable, LongArrayWritable> out : mapReduceDriver.run()) {
            assertTrue(baskets.containsKey(out.getFirst().get()));
            assertEquals(baskets.get(out.getFirst().get()).size(), out.getSecond().get().length);
        }
    }
}
