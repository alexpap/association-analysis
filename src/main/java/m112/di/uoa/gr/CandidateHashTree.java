package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author alexpap
 */
public class CandidateHashTree {

    private static final Logger log = Logger.getLogger(CandidateHashTree.class);


    private class Itemset {

        int[] items;
        int level;

        public Itemset() {
        }

        public Itemset(int[] items, int level) {
            this.items = items;
            this.level = level;
        }

        @Override public boolean equals(Object o) {

            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Itemset itemset = (Itemset) o;

            return Arrays.equals(items, itemset.items);

        }

        @Override public int hashCode() {
            return items[level];
        }

        public int hashValue() {
            return items[level] % items.length;
        }

        public void increaseLevel() {
            ++level;
        }

        /**
         * Let A = {a_1, a_2, ..., a_(l-1)} and B = {b_1, b_2, ..., b_(k-1)}
         * where A is this itemset, B is the given @itemset
         *
         * @param itemset
         * @return true if a_i == b_i (for i= 1,2, ..., k-2) and a_(k-1) != b_(k-1), else false
         */
        public boolean isMergable(Itemset itemset) {

            if (itemset.items.length != items.length) return false;
            int i = 0, n = items.length - 1;
            for (i = 0; i < n; i++) {

                if (items[i] != itemset.items[i])
                    break;
            }
            if ((i == n) && items[n] != itemset.items[n])
                return true;
            return false;
        }

        /**
         * Create the union of this itemset and the given @itemset
         *
         * @param itemset
         * @return the union
         */
        public int[] merge(Itemset itemset) {

            int[] newset = new int[items.length + 1];
            System.arraycopy(items, 0, newset, 0, items.length);
            newset[items.length] = itemset.items[items.length - 1];
            return newset;
        }

        @Override public String toString() {

            StringBuilder builder = new StringBuilder();
            builder.append("<");
            for (int item : items) {

                builder.append(item);
                builder.append(",");
            }
            builder.append("(");
            builder.append(level);
            builder.append(")>");
            return builder.toString();
        }
    }


    private class Node {

        Node[] next;
        HashMap<Itemset, int[]> itemsets;

        public Node(int offset) {                   // create index node

            next = new Node[offset];
            itemsets = null;
        }

        public Node(Itemset items) {                 // create leaf node

            next = null;
            itemsets = new HashMap<Itemset, int[]>();
            itemsets.put(items, new int[] {1});
        }

        public Node(Itemset items, int[] support) {  // create leaf node

            next = null;
            itemsets = new HashMap<Itemset, int[]>();
            itemsets.put(items, support);
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


    // root node of the HashTree
    private Node root;
    private int offset, size;
    private double support_threshold;

    public CandidateHashTree(int k, double threshold) {

        root = new Node(k);
        offset = k;
        support_threshold = threshold;
        size = 0;
    }

    public int size() {
        return size;
    }

    /**
     * Increase the counter for the given itemset @items.
     * If the given itemset does not exist,
     * then new will created with counter value 1.
     *
     * @param items
     */
    public void frequencyIncrement(int[] items) {

        int i;
        boolean added = false;
        Itemset newItemset = new Itemset(items, 0);
        Node current = root, node;
        while (newItemset.level < offset && !added) {

            i = newItemset.hashValue();
            node = current.next[i];
            if (node == null) {                           // empty leaf node

                current.next[i] = new Node(newItemset);
                ++size;
                added = true;
            } else if (node.isLeafNode()) {              // leaf node

                int[] support = node.itemsets.get(newItemset);
                if (support != null) {                        // already exists

                    ++support[0];
                    added = true;
                } else if (node.itemsets.size() < offset      // has space or
                    || (newItemset.level + 1) == offset) {   // maximum level reached

                    node.itemsets.put(newItemset, new int[] {1});
                    ++size;
                    added = true;
                } else {                                // create new index Node
                    // & rehash old itemsets
                    Node indexNode = new Node(offset);
                    Itemset oldItems;
                    current.next[i] = indexNode;    // unlink previous leaf
                    for (Map.Entry<Itemset, int[]> entry : node.itemsets.entrySet()) {

                        oldItems = entry.getKey();
                        support = entry.getValue();
                        oldItems.increaseLevel();   // increase level & rehash
                        if (indexNode.next[oldItems.hashValue()] == null) {

                            indexNode.next[oldItems.hashValue()] = new Node(oldItems, support);
                        } else {

                            indexNode.next[oldItems.hashValue()].itemsets.put(oldItems, support);
                        }
                    }
                    current = current.next[i];          // continue on the new index node
                    newItemset.increaseLevel();
                }
            } else {                                // index node

                newItemset.increaseLevel();
                current = node;
            }
        }

        if (!added) {
            throw new RuntimeException("Unable to insert new itemset");
        }
    }

    /**
     * Extract frequent itemsets using support threshold.
     */
    public void supportFiltering() {

        int minsup = (int) (support_threshold * size);
        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if (node.isLeafNode()) {

                Iterator<Map.Entry<Itemset, int[]>> it = node.itemsets.entrySet().iterator();
                while (it.hasNext()) {

                    Map.Entry<Itemset, int[]> entry = it.next();
                    if (entry.getValue()[0] < minsup) {
                        it.remove();
                        --size;
                    }
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

    private ArrayList<Itemset> getItemsets(){
        ArrayList<Itemset> itemsets = new ArrayList<Itemset>();
        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if (node.isLeafNode()) {

                Iterator<Map.Entry<Itemset, int[]>> it = node.itemsets.entrySet().iterator();
                while (it.hasNext()) {

                    Map.Entry<Itemset, int[]> entry = it.next();
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

    /**
     * Candidate Generation using F_(k-1) X F_(k-1) Method
     * only for each leaf node.
     * @return F_(k) candidates hash tree
     */
    public CandidateHashTree aprioriGen() {

        CandidateHashTree candidates = new CandidateHashTree(offset + 1, support_threshold);
        ArrayList<Itemset> itemsets = getItemsets();
        for(int i = 0; i < itemsets.size(); i ++) {

            for(int j = i; j < itemsets.size(); j++){

                if(itemsets.get(i).isMergable(itemsets.get(j))){                            // check condition

                    candidates.frequencyIncrement(itemsets.get(i).merge(itemsets.get(j)));
                }
            }
        }
        // @TODO ensure that k-2 subsets are frequent
        return candidates;
    }


    private static void subset(
        int[] transaction,
        BitSet used,
        int k,
        int start,
        int remain,
        ArrayList<int[]> subsets){

        if(remain == 0){

            int[] set = new int[k];
            int j = 0, i = used.nextSetBit(0);
            while(i > 0 ){

                set[j] = transaction[i];
                j++;
                i = used.nextSetBit(i + 1);
            }
            subsets.add(set);
        } else if( start + remain <= transaction.length){

            for(int i =start; i < transaction.length; i++){

                if(used.nextSetBit(i) == -1){
                    used.set(i, true);
                    CandidateHashTree.subset(transaction, used, k, i + 1, remain - 1, subsets);
                    used.set(i, false);
                }
            }
        }

    }

    /**
     * Candidate Pruning
     * @param transaction
     */
    public void supportCounting(int[] transaction) {

        CandidateHashTree prefixTree = new CandidateHashTree(offset, support_threshold);
        ArrayList<int[]> subsets = new ArrayList<int[]>();
        BitSet used = new BitSet(transaction.length);
        used.clear();
        CandidateHashTree.subset(transaction, used, offset, 0, offset, subsets);
//        for (int[] subset : subsets) {
//            for (int i : subset) {
//                log.info(i);
//            }
//            log.info("----");
//        }

    }

    @Override public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("\nCandidate Hash Tree\n");
        builder.append("++++++++++++++++++++\n");
        builder.append("<<itemset>, level, support>\n");
        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while(!q.isEmpty()){

            node = q.removeFirst();
            if( node.next == null) {    // leaf
                builder.append("<");
                for (Map.Entry<Itemset, int[]> entry : node.itemsets.entrySet()) {
                    builder.append("<<");
                    for (int item : entry.getKey().items) {
                        builder.append(item);
                        builder.append(", ");
                    }
                    builder.append(">, ");
                    builder.append(entry.getKey().level);
                    builder.append(", ");
                    builder.append(entry.getValue()[0]);
                    builder.append(">\n");
                }
                builder.append(">\n");
            } else if (node.itemsets == null){
                builder.append("--\n");
                for(int i = 0; i < offset; i++){
                    if(node.next[i] != null) q.addLast(node.next[i]);
                }
            } else throw new RuntimeException("Something goes really bad!");
        }
        builder.append("<<itemset>, level, support>\n");
        return builder.toString();
    }
}
