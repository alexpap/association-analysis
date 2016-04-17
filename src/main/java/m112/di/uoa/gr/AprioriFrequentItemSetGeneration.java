package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * Apriori algorithm implement as described in figure 6.1 at
 * http://www-users.cs.umn.edu/~kumar/dmbook/ch6.pdf
 * Notice that steps 1,2 are moved into the loading prepossessing phase
 * @author alexpap
 */
public class AprioriFrequentItemSetGeneration {

    private static final Logger log = Logger.getLogger(AprioriFrequentItemSetGeneration.class);
    private Map<Integer, String> items = null;
    private int[] firstItemSet = null;
    private AprioriFrequentItemSetGeneration(){}
    private static final AprioriFrequentItemSetGeneration instance =
        new AprioriFrequentItemSetGeneration();

    public static AprioriFrequentItemSetGeneration getInstance(){
        return instance;
    }

    /**
     *
     * @param fileName
     * @param support_count
     */
    public void loadMovieLens(String fileName, double support_count){

        try {

            items = new HashMap<Integer, String>();
            Map<Integer, Set<Integer>> baskets = new HashMap<Integer, Set<Integer>>();
            ZipFile zipFile = new ZipFile(fileName);
            String parent =
                fileName.substring(fileName.lastIndexOf("/") + 1).replace(".zip","/");
            InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(parent + "u.item"));

            try{

                String line;
                String[] splits;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while( (line=reader.readLine()) != null){

                     splits = line.trim().split("|");
                     items.put(Integer.parseInt(splits[1]), splits[2]);
                }
            } finally {

                try {inputStream.close();}catch (Throwable ignore){}
            }

            firstItemSet = new int[items.size()];
            int nbaskets = 0;
            inputStream = zipFile.getInputStream(zipFile.getEntry(parent + "u.data"));
            try{

                String line = "";
                String[] splits;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while( (line=reader.readLine()) != null){

                    splits = line.trim().split("\t");
                    firstItemSet[Integer.parseInt(splits[2])]++;
                }
            } finally {

                try {inputStream.close();} catch (Throwable ignore){}
            }

            Arrays.sort(firstItemSet);
            log.info("Support count " + support_count);
            log.info("N " + firstItemSet.length);
            log.info("Percent " + (int) (support_count*firstItemSet.length));
            int thresholdIndex = Arrays.binarySearch(firstItemSet, (int)(support_count*firstItemSet.length));
            firstItemSet = Arrays.copyOfRange(firstItemSet, thresholdIndex, firstItemSet.length);

        } catch (IOException e) {

            log.error(e);
        }

    }

    /**
     * Returns the 1-itemset joined with the item title.
     * @return
     */
    public Map<String, Integer> getFirstItemSet(){

        Map<String, Integer> firstResult = new HashMap<String, Integer>();
        for (int i = 0; i < firstItemSet.length; i++){

            firstResult.put(items.get(i), firstItemSet[i]);
        }
        return firstResult;
    }

}
