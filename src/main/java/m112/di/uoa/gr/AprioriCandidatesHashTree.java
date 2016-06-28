package m112.di.uoa.gr;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Apriori k-itemset candidates  Hash Tree capable of :
 *  - itemset frequency increment
 *  - support factor itemset filtering
 *  - k-itemset generation (apriori-gen) using F_(k-1)xF_(k-1) method
 *  - transaction support counting
 *  - iterator api over the leafs itemsets
 * @author alexpap
 */
public class AprioriCandidatesHashTree implements Iterator<AprioriItemset> {

    private static final Logger log = Logger.getLogger(AprioriCandidatesHashTree.class);

    private static class Node {

        Node[] next;
        HashMap<AprioriItemset, int[]> itemsets;
        boolean visited;

        public Node(int offset) {                            // create index node

            next = new Node[offset];
            itemsets = null;
            visited = false;
        }

        public Node(AprioriItemset items) {                 // create leaf node

            next = null;
            itemsets = new HashMap<AprioriItemset, int[]>();
            itemsets.put(items, new int[] {1});
            visited = false;
        }

        public Node(AprioriItemset items, int[] support) {  // create leaf node

            next = null;
            itemsets = new HashMap<AprioriItemset, int[]>();
            itemsets.put(items, support);
            visited = false;
        }

        public boolean isLeafNode() {
            return itemsets != null;
        }

        @Override public String toString() {
            return "Node{" +
                "next=" + Arrays.toString(next) +
                ", itemsets=" + itemsets +
                '}';
        }
    }

    private Node root;
    private int offset, size;
    private double threshold;
    // support leafs iterator and search
    private ArrayDeque<Node> queue;
    private Iterator<Map.Entry<AprioriItemset, int[]>> leafIterator;
    private int counter;
    private AprioriItemset itemset;

    public AprioriCandidatesHashTree(int k, double minsupp) {

        root = new Node(k);
        offset = k;
        threshold = minsupp;
        size = 0;
        queue = null;
        leafIterator = null;
        counter = 0;
        itemset = new AprioriItemset();
    }

    public int size() {
        return size;
    }

    /**
     * Increase the counter for the given itemset.
     * If the given itemset does not exist,
     * then new set will created.
     * if gen is true and the given itemset does not exist
     * then the newly created set has counter value equals to initValue,
     * otherwise no action is taken.
     * @param items
     */
    public void frequencyIncrement(int[] items, boolean gen, int initValue) {

        int i, v;
        AprioriItemset newItemset = new AprioriItemset();
        newItemset.setItems(items);
        newItemset.setLevel(0);

        Node current = root, node;
        while (newItemset.getLevel() < offset) {

            i = newItemset.hashValue();
            node = current.next[i];
            if (node == null) {                                 // empty leaf node

                if (gen) {
                    current.next[i] = new Node(newItemset, new int[] {initValue});
                    ++size;
                }
                return;
            } else if (node.isLeafNode()) {                     // leaf node

                int[] support = node.itemsets.get(newItemset);
                if (support != null) {                          // already exists

                    ++support[0];
                    return;
                } else if (node.itemsets.size() < offset        // has space or
                    || (newItemset.getLevel() + 1) == offset) { // maximum level reached

                    if(gen){
                        node.itemsets.put(newItemset, new int[] {initValue});
                        ++size;
                    }
                    return;
                } else {                                        // create new index Node
                                                                // & rehash old itemsets
                    Node indexNode = new Node(offset);
                    AprioriItemset oldItems;
                    current.next[i] = indexNode;                // unlink previous leaf
                    for (Map.Entry<AprioriItemset, int[]> entry : node.itemsets.entrySet()) {

                        oldItems = entry.getKey();
                        support = entry.getValue();
                        oldItems.increaseLevel();               // increase level & rehash
                        v = oldItems.hashValue();
                        if (indexNode.next[v] == null) {

                            indexNode.next[v] = new Node(oldItems, support);
                        } else {

                            indexNode.next[v].itemsets.put(oldItems, support);
                        }
                    }
                    current = current.next[i];                  // continue on the new index node
                    newItemset.increaseLevel();
                }
            } else {                                            // index node

                newItemset.increaseLevel();
                current = node;
            }
        }
    }

    /**
     * Extract frequent AprioriItemsets using support threshold.
     */
    public void supportFiltering() {

        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if(node == null) continue;
            else if (node.isLeafNode()) {

                Iterator<Map.Entry<AprioriItemset, int[]>> it = node.itemsets.entrySet().iterator();
                while (it.hasNext()) {

                    Map.Entry<AprioriItemset, int[]> entry = it.next();
                    if (entry.getValue()[0] < threshold) {
                        it.remove();
                        --size;
                    } else entry.getKey().setSupport(entry.getValue()[0]);
                }
            } else {

                for (int i = 0; i < offset; i++) {

                    if (node.next[i] != null) {

                        q.add(node.next[i]);
                    }
                }
            }
        }
    }

    private ArrayList<AprioriItemset> getItemsets(){

        ArrayList<AprioriItemset> itemsets = new ArrayList<AprioriItemset>();
        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if (node.isLeafNode()) {

                Iterator<Map.Entry<AprioriItemset, int[]>> it = node.itemsets.entrySet().iterator();
                while (it.hasNext()) {

                    Map.Entry<AprioriItemset, int[]> entry = it.next();
                    itemsets.add(entry.getKey());
                }
            } else {

                for (int i = 0; i < offset; i++) {

                    if (node.next[i] != null) {

                        q.add(node.next[i]);
                    }
                }
            }
        }
        return itemsets;
    }

    private boolean checkFrequency(int[] items){

        int supp = getSupportByItems(items);
        return supp >= threshold;
    }

    private static int[] isMergeable(AprioriCandidatesHashTree tree, int[] is1, int[] is2){

        if (is1 == null || is2 == null || is1.length != is2.length) return null;
        int i, n = is1.length - 1;
        for (i = 0; i < n; i++) {

            if (is1[i] != is2[i])
                break;
        }
        if ((i == n) && is1[n] != is2[n]){

            if(is1[n] > is2[n]) {
                if( n > 0 ) {
                    int[] tmp = new int[n + 1];
                    for (int j = 1; j < tmp.length; j++) {
                        tmp[j - 1] = is2[j];
                    }
                    tmp[n] = is1[n];
                    if (!tree.checkFrequency(tmp))
                        return null;
                }
                int[] newarr = new int[is1.length + 1];
                System.arraycopy(is2, 0, newarr, 0, n+1);
                newarr[n+1] = is1[n];
                return newarr;
            } else{
                if(n > 0) {
                    int[] tmp = new int[n + 1];
                    for (int j = 1; j < tmp.length; j++) {
                        tmp[j - 1] = is1[j];
                    }
                    tmp[n] = is2[n];
                    if (!tree.checkFrequency(tmp))
                        return null;
                }
                int[] newarr = new int[is1.length + 1];
                System.arraycopy(is1, 0, newarr, 0, n+1);
                newarr[n+1] = is2[n];
                return newarr;
            }
        }
        return null;
    }
    /**
     * Candidate Generation using F_(k-1) X F_(k-1) Method
     * only for each leaf node.
     * @return F_(k) candidates hash tree
     */
    public AprioriCandidatesHashTree aprioriGen() {

        AprioriCandidatesHashTree candidates = new AprioriCandidatesHashTree(offset + 1, threshold);
        ArrayList<AprioriItemset> itemsets = getItemsets();
        int[] items;
        for(int i = 0; i < itemsets.size() - offset; i ++) {

            if(itemsets.get(i).getItems() == null) continue;

            for(int j = i + 1; j < itemsets.size(); j++){

                if(itemsets.get(j).getItems() == null) continue;

                items = isMergeable(this, itemsets.get(i).getItems(),itemsets.get(j).getItems());
                if(items != null){

                    candidates.frequencyIncrement(items, true, 0);
                }
            }
        }
        return candidates;
    }

    /**
     *
     * @param transaction
     */
    public void supportCounting(int[] transaction){

        int[] items = new int[offset];


        long comb1 = CombinatoricsUtils.binomialCoefficient(transaction.length, offset) * (long)Math.log(size());
        long comb2 = size()*offset*(long)Math.log(transaction.length);
        if(comb1 < comb2) { // search transaction combinations @ candidates
            Iterator<int[]> iterator =
                CombinatoricsUtils.combinationsIterator(transaction.length, offset);
            while (iterator.hasNext()){
                int[] next = iterator.next();
                for(int i = 0; i < offset; i++){
                    items[i] = transaction[next[i]];
                }
                frequencyIncrement(items, false, 0);
            }
        }
        else { // search candidates @ transaction

            while (hasNext()){
                AprioriItemset itemset = next();
                boolean flag = true;
                for (int item : itemset.getItems()) {
                    if(Arrays.binarySearch(transaction, item) < 0){
                        flag = false;
                    }
                }
                if(flag)itemset.increaseSupport();
            }
        }

    }

    public int getSupportByItems(int[] items){

        itemset.setItems(items);
        int supportByItemset = getSupportByItemset(itemset);
        itemset.setItems(null);
        return supportByItemset;
    }

    public int getSupportByItemset(AprioriItemset itemset){

        itemset.setLevel(0);
        int i;
        Node current = root, node;
        while (itemset.getLevel() < offset ) {

            i = itemset.hashValue();
            node = current.next[i];
            if (node == null) {                           // empty leaf node

                return -1;
            } else if (node.isLeafNode()) {               // leaf node

                int[] support = node.itemsets.get(itemset);
                return support == null ? -1 : support[0];
            } else {                                      // index node

                itemset.increaseLevel();
                current = node;
            }
        }
        return -1;
    }

    @Override public boolean hasNext() {

        if(queue  == null && size > 0){             // 1st call

            queue = new ArrayDeque<Node>();
            queue.add(root);
            return true;
        }

        if(counter < size) {                        // n call

            return true;
        }
        queue = null;                               // re-init
        leafIterator = null;
        counter = 0;
        return false;
    }

    @Override public AprioriItemset next() {

        if(!hasNext()) throw new NoSuchElementException();

        if(leafIterator != null && leafIterator.hasNext()){
            counter++;
            return leafIterator.next().getKey();
        }

        Node node;
        while(queue.size() > 0) {

            node = queue.removeFirst();
            if (node.isLeafNode()) {

                leafIterator = node.itemsets.entrySet().iterator();
                if (leafIterator.hasNext()) {

                    counter++;
                    return leafIterator.next().getKey();
                }
            } else {

                for (int i = 0; i < offset; i++) {

                    if (node.next[i] != null) {

                        queue.add(node.next[i]);
                    }
                }
            }
        }
        throw new NoSuchElementException();
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
