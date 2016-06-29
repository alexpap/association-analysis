package m112.di.uoa.gr.core;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * A-priori, Support-base pruning
 *@author alexpap
 */
public class AprioriFrequentItemsetGeneration implements Iterator<AprioriCandidatesHashTree>{

    private static final Logger log = Logger.getLogger(AprioriFrequentItemsetGeneration.class);
    private int k, minsupp;
    private AprioriCandidatesHashTree currentFrequentItemsets;
    protected HashMap<Integer, String> items;
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
        items = new HashMap<Integer, String>();
        transactions = null;
        threshold = support_threshold;
    }

    private InputStream getInputFromZip(String zipFile, String fileName){
        try {
            ZipInputStream zipInputStream = new ZipInputStream(
                AprioriFrequentItemsetGeneration.class.getClassLoader()
                    .getResourceAsStream(zipFile));
            ZipEntry entry = null;
            while ((entry = zipInputStream.getNextEntry()) != null){
                if(fileName.equals(entry.getName())) break;
            }
            return zipInputStream;
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Pre-process anyone of the available movielens dataset.
     * @param datasetType
     */
    public void preprocess(MovieLensDatasetType datasetType) {

        log.debug("****************************************");
        log.debug("Input dataset " + datasetType.toString());
        String zipFileName, itemsFilename, moviesFilename, itemsep, moviessep;
        InputStream inputItems = null, inputMovies = null;

        if (MovieLensDatasetType.ml_100k.equals(datasetType)) {
            zipFileName = "ml-100k/";
            itemsFilename = "u.item";
            moviesFilename = "u.data";
            itemsep = "|";
            moviessep = "\t";
            inputItems = getInputFromZip("ml-100k.zip", zipFileName + itemsFilename);
            inputMovies = getInputFromZip("ml-100k.zip", zipFileName + moviesFilename);
        } else if (MovieLensDatasetType.ml_1m.equals(datasetType)){

            zipFileName = "ml-1m/";
            itemsFilename = "movies.dat";
            moviesFilename = "ratings.dat";
            itemsep = "::";
            moviessep = "::";
            inputItems = getInputFromZip("ml-1m.zip", zipFileName + itemsFilename);
            inputMovies = getInputFromZip("ml-1m.zip", zipFileName + moviesFilename);
        } else if(MovieLensDatasetType.ml_10m.equals(datasetType)){

            zipFileName = "ml-10M100K/";
            itemsFilename = "movies.dat";
            moviesFilename = "ratings.dat";
            itemsep = "::";
            moviessep = "::";
            inputItems = getInputFromZip("ml-10m.zip", zipFileName + itemsFilename);
            inputMovies = getInputFromZip("ml-10m.zip", zipFileName + moviesFilename);
        } else if(MovieLensDatasetType.ml_latest_small.equals(datasetType)) {

            zipFileName = "ml-latest-small/";
            itemsFilename = "movies.csv";
            moviesFilename = "ratings.csv";
            itemsep = ",";
            moviessep = ",";
            inputItems = getInputFromZip("ml-latest-small.zip", zipFileName + itemsFilename);
            inputMovies = getInputFromZip("ml-latest-small.zip", zipFileName + moviesFilename);
        } else throw new RuntimeException("Unable to locate " + datasetType.toString() + " dataset.");

        long tstart = System.currentTimeMillis();
        try {

            // loading items (movies.csv)
            // keep only movieId, title
            log.debug("Try to load items ...");
            InputStream inputStream = inputItems;
            if (inputStream == null) {

                throw new RuntimeException("Unable to locate movielens movies.");
            }

            try {

                String line;
                StringTokenizer tokenizer;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                if(MovieLensDatasetType.ml_latest_small.equals(datasetType))
                    reader.readLine(); // skip header

                while ((line = reader.readLine()) != null) {

                    tokenizer = new StringTokenizer(line, itemsep, false);
                    items.put(Integer.parseInt(tokenizer.nextToken()), tokenizer.nextToken());
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
            InputStream inputStream = inputMovies;
            if (inputStream == null) {

                throw new RuntimeException("Unable to locate movielens u.data.");
            }

            try {
                HashMap<Integer, TreeSet<Integer>> baskets = new HashMap<Integer, TreeSet<Integer>>();
                minsupp = Double.valueOf(threshold * items.size()).intValue();
                k = 1;
                currentFrequentItemsets = new AprioriCandidatesHashTree(1, minsupp);
                log.debug("Minimum support = " + minsupp + "(" + threshold *100 + "%)");
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
                    currentFrequentItemsets.frequencyIncrement(new int[] {ikey}, true, 1);
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
                long w, minw = Long.MAX_VALUE, maxw = 0, sumw = 0, ssumw = 0;
                int i = 0;
                Iterator<Integer> it;
                for (Map.Entry<Integer, TreeSet<Integer>> entry : baskets.entrySet()) {
                    TreeSet<Integer> basket = entry.getValue();
                    // stats
                    w = basket.size();
                    if(w < minw) minw = w;
                    if(w > maxw) maxw = w;
                    sumw += w; ssumw += w*w;
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
                int n = transactions.length;
                log.debug("Width Statistics : Max " + maxw
                        + ", Min " + minw + ", Avg " + (sumw/n)
                        + ", SD " + (Math.sqrt((ssumw/n)-Math.pow(sumw/n,2))));
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

    public HashMap<Integer, String> getItems() {
        return items;
    }

    @Override public boolean hasNext() {

        return (currentFrequentItemsets != null && currentFrequentItemsets.size() > 0);
    }

    @Override public AprioriCandidatesHashTree next() {

        if(!hasNext()) throw new NoSuchElementException();

        log.debug("Apriori next itemsets generation ...");
        if(k == 1) {

            log.debug("1-itemsets already exits");
            log.debug("Support filtering ...");
            currentFrequentItemsets.supportFiltering();
        } else {

            AprioriCandidatesHashTree currentItemsets = currentFrequentItemsets.aprioriGen();
            log.debug(k + "-itemsets generated size of " + currentItemsets.size());
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
        log.debug("****************************************");
        k++;
        return currentFrequentItemsets;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String[] args){

        AprioriFrequentItemsetGeneration frequentItemset =
            new AprioriFrequentItemsetGeneration(0.10);
        double min_cofidence = 0.50;
        frequentItemset.preprocess(MovieLensDatasetType.ml_100k);

        List<AprioriCandidatesHashTree> trees = new ArrayList<AprioriCandidatesHashTree>();
        AprioriItemset itemset;
        // iterate over trees
        while (frequentItemset.hasNext()) {

            AprioriCandidatesHashTree tree = frequentItemset.next();
            if (tree.size() > 0) trees.add(tree);

            //iterate over itemset
            while (tree.hasNext()) {
                itemset = tree.next();
                log.debug(itemset.toString());
            }
        }

        log.debug("\n");
        log.debug("Generating Apriori Association Rules...");

        List<AprioriRule> rules_temp;
        List<AprioriRule> rules_all=new ArrayList();
        if (trees.size()>=2) {
            AprioriAssociationRulesGeneration rules_gen = new AprioriAssociationRulesGeneration(trees, min_cofidence, rules_all);
            while (rules_gen.hasNext()) {
                rules_temp = rules_gen.next();
                for (int i = 0; i < rules_temp.size(); i++) {
                    log.debug(rules_temp.get(i).toString());
                }
            }
        }


        HashMap<Integer,String>  title=frequentItemset.items;
        String entitled_itemset;
        int ktree =0;
        while (ktree < trees.size()) {
            while (trees.get(ktree).hasNext()) {
                AprioriItemset current_itemset = trees.get(ktree).next();
                entitled_itemset="";
                for (int i=0; i<current_itemset.getItems().length; i++) {
                    int itemset_temp[] =current_itemset.getItems();
                    if (i==0)
                        entitled_itemset=entitled_itemset+title.get(itemset_temp[i]);
                    else
                        entitled_itemset=entitled_itemset+", "+title.get(itemset_temp[i]);
                }
                log.debug("Itemset="+entitled_itemset+", Support="+current_itemset.getSupport());
            }
            ktree++;
        }

        String entitled_itemset_body;
        if (rules_all!=null) {
            for (int i = 0; i < rules_all.size(); i++) {
                List<RuleElement> rule_temp = rules_all.get(i).rules;
                int counter = rules_all.get(i).getFinal_rules();
                for (int j = 0; j < counter; j++) {
                    //log.debug(rule_temp.get(j));
                    int head_temp[] = rule_temp.get(j).getHead();
                    int body_temp[] = rule_temp.get(j).getBody();

                    entitled_itemset = "";
                    for (int x = 0; x < head_temp.length; x++) {
                        if (x == 0)
                            entitled_itemset = entitled_itemset + title.get(head_temp[x]);
                        else
                            entitled_itemset = entitled_itemset + ", " + title.get(head_temp[x]);
                    }
                    entitled_itemset_body = "";
                    for (int x = 0; x < body_temp.length; x++) {
                        if (x == 0)
                            entitled_itemset_body = entitled_itemset_body + title.get(body_temp[x]);
                        else
                            entitled_itemset_body = entitled_itemset_body + ", " + title.get(body_temp[x]);
                    }
                    log.debug("Rule=" + entitled_itemset + " -> " + entitled_itemset_body + " Rule Confidence=" + rule_temp.get(j).getRule_confidence());
                }
            }
        }
    }
}
