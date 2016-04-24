package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * A-priori, Support-base pruning
 * TODO checkout TreeHash to avoid iterating over all the k-itemsets each time
 *
 *@author alexpap
 */
public class AprioriFrequentItemsetGeneration {

    private static final Logger log = Logger.getLogger(AprioriFrequentItemsetGeneration.class);
    private ArrayList<CandidateHashTree> frequentItemsets;
    private HashMap<String, String> items;
    private int[][] transactions;
    private int threshold;

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
    public AprioriFrequentItemsetGeneration(int support_threshold) {

        frequentItemsets = new ArrayList<CandidateHashTree>();
        frequentItemsets.add(null);  // skip k = 0
        items = new HashMap<String, String>();
        transactions = null;
        threshold = support_threshold;
    }

    /**
     * Pre-process the latest movieLens dataset ginen as @zipPath
     * @param zipPath
     */
    public void preprocess(String zipPath){

        long tstart = System.currentTimeMillis();
        try{

            // loading items (movies.csv)
            // keep only movieId, title
            log.debug("Try to load items ...");
            ZipFile zipFile = new ZipFile(zipPath);
            String parent = zipPath.substring(zipPath.lastIndexOf("/") + 1).replace(".zip","/");
            InputStream inputStream =
                zipFile.getInputStream(zipFile.getEntry(parent.concat("movies.csv")));
            if (inputStream == null){

                throw new RuntimeException("Unable to locate movielens movies.");
            }

            try {

                String line = "";
                StringTokenizer tokenizer;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine(); // skip header

                while ((line = reader.readLine()) != null) {

                    tokenizer = new StringTokenizer(line,",", false);
                    items.put(tokenizer.nextToken(), tokenizer.nextToken());
                }
                log.debug(
                    items.size() + " #Items loaded ("
                        + String.valueOf(System.currentTimeMillis() - tstart)+ " ms, "
                        + getMemoryMBUsage()+" MB)"
                );
            }finally{

                try { if (inputStream != null) {inputStream.close();}
                } catch (Throwable ignore){}
            }
        }catch (Exception ex){

            throw new RuntimeException("Internal error occurred", ex);
        }

        tstart = System.currentTimeMillis();
        try{

            // loading ratings.csv in order to create movie baskets
            // and the first itemset by skipping ratings.
            log.debug("Try to load baskets ...");
            ZipFile zipFile = new ZipFile(zipPath);
            String parent = zipPath.substring(zipPath.lastIndexOf("/") + 1).replace(".zip","/");
            InputStream inputStream = zipFile.getInputStream(zipFile.getEntry( parent.concat("ratings.csv")));
            if (inputStream == null){

                throw new RuntimeException("Unable to locate movielens u.data.");
            }

            try {
                HashMap<Integer,TreeSet<Integer>> baskets = new HashMap<Integer,TreeSet<Integer>>();
                CandidateHashTree firstItemsets = new CandidateHashTree(threshold);
                int[] tmpKey = new int[1];
                String line = "";
                StringTokenizer tokenizer;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine(); // skip header

                while ((line = reader.readLine()) != null) {

                    tokenizer = new StringTokenizer(line, ",", false);
                    Integer ukey = Integer.valueOf(tokenizer.nextToken());
                    Integer ikey = Integer.valueOf(tokenizer.nextToken());

                    // add new item into the user basket
                    TreeSet<Integer> ubasket = baskets.get(ukey);
                    if (ubasket == null){

                        baskets.put(ukey, (ubasket = new TreeSet<Integer>()));
                    }
                    ubasket.add(ikey);

                    // update firstItemsets
                    firstItemsets.frequencyIncrement(tmpKey);
                }
                frequentItemsets.add(firstItemsets);
                log.debug(
                    baskets.size() + " #Baskets loaded ("
                        + String.valueOf(System.currentTimeMillis() - tstart)+ "ms, "
                        + getMemoryMBUsage() +" MB)"
                );
                log.debug(
                    firstItemsets.size() + " #1-itemset without support counting created ("
                        + String.valueOf(System.currentTimeMillis() - tstart)+ "ms, "
                        + getMemoryMBUsage() +" MB)"
                );
                tstart = System.currentTimeMillis();
                log.debug("Try to format baskets ...");
                transactions = new int[baskets.size()][];
                int i =0;
                Iterator<Integer> it;
                for (Map.Entry<Integer, TreeSet<Integer>> entry : baskets.entrySet()) {
                    TreeSet<Integer> basket = entry.getValue();
                    it = basket.iterator();
                    transactions[i] = new int[basket.size()];

                    int j = 0;
                    while (it.hasNext()){
                        transactions[i][j] = it.next();
                        ++j;
                    }
                    i++;
                }
                log.debug("Baskets formatted ("
                    + String.valueOf(System.currentTimeMillis()-tstart) + " ms, "
                    + getMemoryMBUsage() +" MB)"
                );
            }finally{

                try { if (inputStream != null) {inputStream.close();}
                } catch (Throwable ignore){}
            }
        }catch (Exception ex){

            throw new RuntimeException("Internal error occurred", ex);
        }
    }


    /**
     * Generates k frequent itemsets
     * @return list of frequent itemsets
     */
    public ArrayList<CandidateHashTree> generateItemsets(){

        int k = 1;
        int minsup = (threshold*frequentItemsets.get(k).size())/100;

        // {Find all frequent 1-itemsets}
        // note that 1-itemsets was generated at the pre-processing phase.
        log.debug("1-itemsets support filtering (" + minsup + ") ...");
        frequentItemsets.get(k).supportFiltering();

        //
        CandidateHashTree currentItemsets;
        do {
            ++k;
            log.debug("Try to generate 2-itemsets ...");
            currentItemsets = frequentItemsets.get(k-1).aprioriGen();
            for(int i = 0; i < transactions.length; i++){

                currentItemsets.supportCounting(transactions[i]);
            }
            currentItemsets.supportFiltering();
            frequentItemsets.add(currentItemsets);
            log.debug("2-itemsets created ");
        }while(currentItemsets.size() > 0);
        return frequentItemsets;
    }

    public static void main(String[] args){
        String path =
//            AprioriFrequentItemsetGeneration.class.getClassLoader().getResource("ml-latest-small.zip").getPath();
              AprioriFrequentItemsetGeneration.class.getClassLoader().getResource("ml-latest.zip").getPath();
        AprioriFrequentItemsetGeneration frequentItemset = new AprioriFrequentItemsetGeneration(10);
        frequentItemset.preprocess(path);
        for (CandidateHashTree candidateHashTree : frequentItemset.generateItemsets()) {
            if (candidateHashTree != null)
            log.debug(candidateHashTree.size());
        }
    }
}
