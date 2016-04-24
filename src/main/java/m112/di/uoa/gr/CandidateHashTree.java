package m112.di.uoa.gr;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author alexpap
 */
public class CandidateHashTree {

    private TreeMap<int[], int[]> itemsets;
    private int minsup;

    private class CandidateComparator implements Comparator<int[]> {

        @Override public int compare(int[] s1, int[] s2) {
            return s1[0] < s1[0] ?  -1 : s1[0] == s1[0] ? 0 : 1;
        }
    }

    public CandidateHashTree(int support_threshold) {
        itemsets = new TreeMap<int[], int[]>(new CandidateComparator());
        minsup = support_threshold;
    }

    public int size(){
        return itemsets.size();
    }

    /**
     * Iterates over the itemsets and removes the itemsets
     * that have counter value less than the threshold.
     */
    public void supportFiltering(){

        Iterator<Map.Entry<int[], int[]>> it = itemsets.entrySet().iterator();
        while (it.hasNext()){

            Map.Entry<int[], int[]> entry = it.next();
            if (entry.getValue()[0] < minsup){

                it.remove();
            }
        }
    }

    /**
     * Increases the counter fo the corresponding @itemset.
     * If itemset does not exists, adds @itemset with counter value = 1.
     * @param itemset
     */
    public void frequencyIncrement(int[] itemset){

        int[] counter = itemsets.get(itemset);
        if (counter == null){

            itemsets.put(itemset, new int[] {1});
        } else {

            ++counter[0];
        }
    }

    /**
     * Generates k candidate itemsets
     * using F_(k-1)xF_(k-1) method without the (k-2)-itemsets frequency filtering.
     */
    public CandidateHashTree aprioriGen(){

        CandidateHashTree newItemsets = new CandidateHashTree(minsup);
        if (itemsets.size() < 1) return newItemsets;

        int[] a,b;
        int i = 0, n = itemsets.firstEntry().getKey().length;
        Iterator<Map.Entry<int[], int[]>> it1 = itemsets.entrySet().iterator(), it2;
        while (it1.hasNext()) {

            a = it1.next().getKey();
            it2 = itemsets.entrySet().iterator();
            while(it2.hasNext()){

                b = it2.next().getKey();
                for( i = 0; i < n - 1; i++){

                    if ( (i == n-1) && (a[i] != a[i])) {

                        break;
                    }
                }
                if (a[n-1] != b[n-1]){  // satisfy merge condition
                    // TODO check k-2 itemsets frequency < minup
                    int[] newItemset = new int[n + 1];
                    System.arraycopy(a, 0, newItemset, 0, n);
                    System.arraycopy(b, n-1, newItemset, n, 1);
                    newItemsets.frequencyIncrement(newItemset);
                }
            }
        }
        return newItemsets;
    }

    /**
     * Identify all candidates that belong to @transaction
     * and increase their counter value
     * @param transaction
     */
    public void supportCounting(int[] transaction){

        if (itemsets.size() < 1) return;
        int k = itemsets.firstEntry().getKey().length, n = transaction.length;
        int c = Long.valueOf(CombinatoricsUtils.binomialCoefficient(n, k)).intValue();

        CandidateHashTree candidates = new CandidateHashTree(minsup);
        for(int i = 0; i < k; i++){         //  level

        }
    }

}
