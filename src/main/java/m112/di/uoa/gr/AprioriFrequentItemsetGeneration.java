package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * A-priori, Support-base pruning
 *@author alexpap
 */
public class AprioriFrequentItemsetGeneration implements Iterator<AprioriCandidatesHashTree>{

    private static final Logger log = Logger.getLogger(AprioriFrequentItemsetGeneration.class);
    private int k, minsupp;
    private AprioriCandidatesHashTree currentFrequentItemsets;
    private HashMap<int[], String> items;
    private int[][] transactions;
    private double threshold;

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
        k = 0;
        currentFrequentItemsets= null;
        items = new HashMap<int[], String>();
        transactions = null;
        threshold = support_threshold;
    }

    /**
     * Pre-process anyone of the available movielens dataset.
     * @param datasetType
     */
    public void preprocess(MovieLensDatasetType datasetType) {

        log.debug("****************************************");
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
            zipFileName = "ml-1m/";
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
        log.debug("****************************************");
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
                minsupp = Double.valueOf(threshold * items.size()).intValue();
                k = 1;
                currentFrequentItemsets = new AprioriCandidatesHashTree(1, minsupp);
                log.debug("Minimum support = " + minsupp);
                String line;
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
                    currentFrequentItemsets.frequencyIncrement(new int[] {ikey});
                }
                log.debug(baskets.size() + " #Baskets loaded (" + String
                    .valueOf(System.currentTimeMillis() - tstart) + "ms, " + getMemoryMBUsage()
                    + " MB)");
                log.debug(
                    currentFrequentItemsets.size() + " #1-itemset created ("
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
                log.debug("****************************************");
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

    @Override public boolean hasNext() {

        return (currentFrequentItemsets != null && currentFrequentItemsets.size() > 0);
    }

    @Override public AprioriCandidatesHashTree next() {

        if(!hasNext()) throw new NoSuchElementException();

        log.debug("Apriori next itemsets generation ...");
        if(k == 1) {
            log.debug("1-itemsets already exits");
            log.debug("Only Support filtering (k = 1) ...");
            currentFrequentItemsets.supportFiltering();
        } else {

            AprioriCandidatesHashTree currentItemsets = currentFrequentItemsets.aprioriGen();
            if (currentItemsets.size() > 0) {
                log.debug("Support counting ...");
                for (int i = 0; i < transactions.length; i++) {

                    currentItemsets.supportCounting(transactions[i]);
                }
                log.debug("Support filtering ...");
                currentItemsets.supportFiltering();
            }
            currentFrequentItemsets = currentItemsets;
        }
        log.debug(k + "-itemsets created size of " + currentFrequentItemsets.size());
        k++;
        return currentFrequentItemsets;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static TreeSet combination(TreeSet results, int k, int n, int current_support, double min_confidence) {

        Object [] elements = results.toArray();
    
        int combination[] = new int[k];

        int r = 0;
        int index = 0;

        Object[] output= new Object[k];
        Object[] right_part=elements.clone();
        int temp_support;
        double current_cofidence;
        while (r >= 0) {
            
            if (index <= (n + (r - k))) {
                combination[r] = index;

                if (r == k - 1) {
                    
                    for (int z = 0; z < combination.length; z++) {
                        output[z]=elements[combination[z]];
                    }
                    Arrays.asList(right_part).removeAll(Arrays.asList(output));
                  
                    temp_support=trees.get(right_part.length-1).getSupportByItemset(right_part);

                    current_cofidence = (double)(current_support/temp_support);
                    if (current_cofidence < min_confidence) {
                        results.removeAll(Arrays.asList(right_part));
                    } else {
                        log.debug("Confidence " + Arrays.toString(output) + " -> "
                                + Arrays.toString(right_part) +" = "+ current_cofidence);  
                    }
                    
                    index++;
                } else {

                    index = combination[r] + 1;
                    r++;
                }
            } else {
                r--;
                if (r > 0) {
                    index = combination[r] + 1;
                } else {
                    index = combination[0] + 1;
                }
            }
        }
        return results;
    }
    
    private static List<AprioriCandidatesHashTree> trees = new ArrayList<AprioriCandidatesHashTree>();
    
    public static void main(String[] args){

        AprioriFrequentItemsetGeneration frequentItemset = new AprioriFrequentItemsetGeneration(0.05);
        frequentItemset.preprocess(MovieLensDatasetType.ml_100k);

        //List<AprioriCandidatesHashTree> trees = new ArrayList<AprioriCandidatesHashTree>();
        List<AprioriItemset> itemsetToSearch = new ArrayList<AprioriItemset>();
        boolean flag;
        // iterate over trees
        while (frequentItemset.hasNext()){

            AprioriCandidatesHashTree tree = frequentItemset.next();
            if (tree.size()>0) {
                trees.add(tree);
            }
            flag = true;
            // iterate over itemset
            while(tree.hasNext()){

                AprioriItemset itemset = tree.next();
                //log.debug(Arrays.toString(itemset.getItems()));
                if(flag){
                    itemsetToSearch.add(itemset);
                    flag = false;
                }
            }
        }

        // search itemset on each tree
        /*
        for(int i =0; i < itemsetToSearch.size(); i++){
            log.debug("Itemset " + itemsetToSearch.get(i)
                + " found on " + i + " tree with support "
                + trees.get(i).getSupportByItemset(itemsetToSearch.get(i))
            );
        }*/
        double min_cofidence=0;
        int current_support;
        int k, n;
        TreeSet results = new TreeSet();
        
        for (int i=1; i<trees.size(); i++) {
            while (trees.get(i).hasNext()) {
                AprioriItemset current_itemset = trees.get(i).next();
                log.debug(Arrays.toString(current_itemset.getItems())+" "+current_itemset.getSupport());
                
                results.addAll(Arrays.asList(current_itemset.getItems()));
                
                current_support=current_itemset.getSupport();
                n=results.size();
                k=n-1;
                while (k>=1 & results.size()>k) {
                    results=combination(results, k, n, current_support, min_cofidence);
                    k--;
                }
            }
        }
    }
}
