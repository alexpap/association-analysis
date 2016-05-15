package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * A-priori, Support-base pruning
 * TODO checkout TreeHash to avoid iterating over all the k-itemsets each time
 * TODO checkout tree shrink while deleting
 *@author alexpap
 */
public class AprioriFrequentItemsetGeneration {

    private static final Logger log = Logger.getLogger(AprioriFrequentItemsetGeneration.class);
    private ArrayList<CandidatesHashTree> frequentItemsets;
    private HashMap<int[], String> items;
    private int[][] transactions;
    private double threshold, minsupp;

    /**
     * Calculate the current memory usage in MBs
     * @return  memory usage
     */
    private long getMemoryMBUsage(){
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory())/(1024*1024);
    }

    /**
     * Initialize internal structures and @support_threshold
     * @param support_threshold
     */
    public AprioriFrequentItemsetGeneration(double support_threshold) {

        frequentItemsets = new ArrayList<CandidatesHashTree>();
        frequentItemsets.add(null);  // skip k = 0
        items = new HashMap<int[], String>();
        transactions = null;
        threshold = support_threshold;
    }

    /**
     * Pre-process anyone of the available movielens dataset.
     * @param datasetType
     */
    public void preprocess(MovieLensDatasetType datasetType) {

        log.debug("Input dataset " + datasetType.toString());
        String zipPath = null, zipFileName, itemsFilename, moviesFilename, itemsep, moviessep;

        if (MovieLensDatasetType.ml_100k.equals(datasetType)) {
            zipPath =
                AprioriFrequentItemsetGeneration.class.getClassLoader().getResource("ml-100k.zip").getPath();
            zipFileName = "ml-100k/";
            itemsFilename = "u.item";
            moviesFilename = "u.data";
            itemsep = "|";
            moviessep = "\t";

        } else if (MovieLensDatasetType.ml_1m.equals(datasetType)){
            zipPath =
                AprioriFrequentItemsetGeneration.class.getClassLoader().getResource("ml-1m.zip").getPath();
            zipFileName = "ml-1M/";
            itemsFilename = "movies.dat";
            moviesFilename = "ratings.dat";
            itemsep = "::";
            moviessep = "::";
        } else if(MovieLensDatasetType.ml_10m.equals(datasetType)){
            zipPath = AprioriFrequentItemsetGeneration.class.getClassLoader().getResource("ml-10m.zip").getPath();
            zipFileName = "ml-10M100K/";
            itemsFilename = "movies.dat";
            moviesFilename = "ratings.dat";
            itemsep = "::";
            moviessep = "::";
        } else if(MovieLensDatasetType.ml_latest_small.equals(datasetType)) {
            zipPath =
                AprioriFrequentItemsetGeneration.class.getClassLoader().getResource("ml-latest-small.zip").getPath();
            zipFileName = "ml-latest-small/";
            itemsFilename = "movies.csv";
            moviesFilename = "ratings.csv";
            itemsep = ",";
            moviessep = ",";
        } else throw new RuntimeException("Unable to locate " + datasetType.toString() + " dataset.");

        long tstart = System.currentTimeMillis();
        try {

            // loading items (movies.csv)
            // keep only movieId, title
            log.debug("Try to load items ...");
            ZipFile zipFile = new ZipFile(zipPath);
            String parent = zipPath.substring(zipPath.lastIndexOf("/") + 1).replace(".zip", "/");
            InputStream inputStream =
                zipFile.getInputStream(zipFile.getEntry(zipFileName.concat(itemsFilename)));
            if (inputStream == null) {

                throw new RuntimeException("Unable to locate movielens movies.");
            }

            try {

                String line = "";
                StringTokenizer tokenizer;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine(); // skip header

                while ((line = reader.readLine()) != null) {

                    tokenizer = new StringTokenizer(line, itemsep, false);
                    items.put(new int[] {Integer.parseInt(tokenizer.nextToken())}, tokenizer.nextToken());
                }
                log.debug(items.size() + " #Items loaded (" + String
                    .valueOf(System.currentTimeMillis() - tstart) + " ms, " + getMemoryMBUsage()
                    + " MB)");
            } finally {

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Throwable ignore) {
                }
            }
        } catch (Exception ex) {

            throw new RuntimeException("Internal error occurred", ex);
        }

        tstart = System.currentTimeMillis();
        try {

            // loading ratings.csv in order to create movie baskets
            // and the first itemset by skipping ratings.
            log.debug("Try to load baskets ...");
            ZipFile zipFile = new ZipFile(zipPath);
            String parent = zipPath.substring(zipPath.lastIndexOf("/") + 1).replace(".zip", "/");
            InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(zipFileName.concat(moviesFilename)));
            if (inputStream == null) {

                throw new RuntimeException("Unable to locate movielens u.data.");
            }

            try {
                HashMap<Integer, TreeSet<Integer>> baskets = new HashMap<Integer, TreeSet<Integer>>();
                minsupp = threshold * items.size();
                CandidatesHashTree firstItemsets = new CandidatesHashTree(1, minsupp);
                String line = "";
                StringTokenizer tokenizer;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine(); // skip header

                while ((line = reader.readLine()) != null) {

                    tokenizer = new StringTokenizer(line, moviessep, false);
                    Integer ukey = Integer.valueOf(tokenizer.nextToken());
                    Integer ikey = Integer.valueOf(tokenizer.nextToken());

                    // add new item into the user basket
                    TreeSet<Integer> ubasket = baskets.get(ukey);
                    if (ubasket == null) {

                        baskets.put(ukey, (ubasket = new TreeSet<Integer>()));
                    }
                    ubasket.add(ikey);

                    // update firstItemsets
                    firstItemsets.frequencyIncrement(new int[] {ikey});
                }
                frequentItemsets.add(firstItemsets);
                log.debug(baskets.size() + " #Baskets loaded (" + String
                    .valueOf(System.currentTimeMillis() - tstart) + "ms, " + getMemoryMBUsage()
                    + " MB)");
                log.debug(
                    firstItemsets.size() + " #1-itemset without support counting created ("
                        + String.valueOf(System.currentTimeMillis() - tstart) + "ms, "
                        + getMemoryMBUsage() + " MB)");
                tstart = System.currentTimeMillis();
                log.debug("Try to format baskets ...");
                transactions = new int[baskets.size()][];
                int i = 0;
                Iterator<Integer> it;
                for (Map.Entry<Integer, TreeSet<Integer>> entry : baskets.entrySet()) {
                    TreeSet<Integer> basket = entry.getValue();
                    it = basket.iterator();
                    transactions[i] = new int[basket.size()];

                    int j = 0;
                    while (it.hasNext()) {
                        transactions[i][j] = it.next();
                        ++j;
                    }
                    i++;
                }
                log.debug(
                    "Baskets formatted (" + String.valueOf(System.currentTimeMillis() - tstart)
                        + " ms, " + getMemoryMBUsage() + " MB)");
            } finally {

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Throwable ignore) {
                }
            }
        } catch (Exception ex) {

            throw new RuntimeException("Internal error occurred", ex);
        }

    }

    /**
     * Generates k frequent itemsets
     * @return list of frequent itemsets
     */
    public ArrayList<CandidatesHashTree> generateItemsets(){

        int k = 1;
        // {Find all frequent 1-itemsets}
        // note that 1-itemsets was generated at the pre-processing phase.
        log.debug("1-itemsets created sizeof " + frequentItemsets.get(k).size());
        log.debug("Support filtering ...\"");
        frequentItemsets.get(k).supportFiltering();
        log.debug("1-itemsets created sizeof " + frequentItemsets.get(k).size());

        CandidatesHashTree currentItemsets;
        do {
            ++k;
            log.debug("Try to generate " + k + "-itemsets ...");
            currentItemsets = frequentItemsets.get(k-1).aprioriGen();
            if( currentItemsets.size() > 0) {
                log.debug("Support counting ...");
                for (int i = 0; i < transactions.length; i++) {

                    currentItemsets.supportCounting(transactions[i]);
                }
                log.debug("Support filtering ...");
                currentItemsets.supportFiltering();
                frequentItemsets.add(currentItemsets);
                log.debug(k + "-itemsets created size of " + currentItemsets.size());
            }
        }while(currentItemsets.size() > 0);
        return frequentItemsets;
    }

    public static void main(String[] args){

        AprioriFrequentItemsetGeneration frequentItemset = new AprioriFrequentItemsetGeneration(0.003);
        frequentItemset.preprocess(MovieLensDatasetType.ml_latest_small);
        ArrayList<CandidatesHashTree> itemsets = frequentItemset.generateItemsets();

    }
}
