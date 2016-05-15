package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * Apriori k-itemset candidates  Hash Tree capable of :
 *  - itemset frequency increment
 *  - support factor itemset filtering
 *  - new k+1-itemset generation (apriori-gen) using F_(k-1)xF_(k-1) method
 *  - transaction support counting
 * @author alexpap
 */
public class AprioriCandidatesHashTree {

    private class Itemset {

        int[] items;
        int   level;

        public Itemset() {
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
            int i, n = items.length - 1;
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
    private double threshold;

    public AprioriCandidatesHashTree(int k, double minsupp) {

        root = new Node(k);
        offset = k;
        threshold = minsupp;
        size = 0;
    }

    public int size() {
        return size;
    }

    /**
     * Increase the counter for the given itemset @items.
     * If the given itemset does not exist,
     * then new set will created with counter value 1.
     * @param items
     */
    public void frequencyIncrement(int[] items) {

        int i;
        boolean added = false;
        Itemset newItemset = new Itemset();
        newItemset.items = items;
        newItemset.level = 0;

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
     * Increase the counter for the given transaction
     * itemset sizeof k starting from prefix only if exist.
     * @param transaction
     * @param prefix
     */
    private void frequencyIncrementWithoutAddition(int[] transaction, int prefix) {

        int i;
        boolean added = false;
        Itemset newItemset = new Itemset();
        newItemset.items = Arrays.copyOfRange(transaction, prefix, prefix + offset);
        newItemset.level = 0;

        Node current = root, node;
        while (newItemset.level < offset ) {

            i = newItemset.hashValue();
            node = current.next[i];
            if (node == null) {                           // empty leaf node

                return;
            } else if (node.isLeafNode()) {              // leaf node

                int[] support = node.itemsets.get(newItemset);
                if (support != null) {                        // already exists

                    ++support[0];
                }
                return;
            } else {                                // index node

                newItemset.increaseLevel();
                current = node;
            }
        }
    }


    /**
     * Extract frequent itemsets using support threshold.
     */
    public void supportFiltering() {

        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if (node.isLeafNode()) {

                Iterator<Map.Entry<Itemset, int[]>> it = node.itemsets.entrySet().iterator();
                while (it.hasNext()) {

                    Map.Entry<Itemset, int[]> entry = it.next();
                    if (entry.getValue()[0] < threshold) {
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
    public AprioriCandidatesHashTree aprioriGen() {

        AprioriCandidatesHashTree candidates = new AprioriCandidatesHashTree(offset + 1, threshold);
        ArrayList<Itemset> itemsets = getItemsets();
        for(int i = 0; i < itemsets.size() - offset; i ++) {

            for(int j = i + 1; j < itemsets.size(); j++){

                if(itemsets.get(i).isMergable(itemsets.get(j))){                            // check condition

                    candidates.frequencyIncrement(itemsets.get(i).merge(itemsets.get(j)));
                }
            }
        }
        return candidates;
    }

    /**
     * Candidate Pruning
     * @param transaction
     */
    public void supportCounting(int[] transaction) {

        for(int i = 0; i < transaction.length - offset; i++){

            frequencyIncrementWithoutAddition(transaction, i);
        }
    }
}
