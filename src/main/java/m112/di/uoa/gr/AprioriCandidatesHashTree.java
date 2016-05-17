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
public class AprioriCandidatesHashTree implements Iterator<AprioriItemset> {


    private class Node {

        Node[] next;
        HashMap<AprioriItemset, int[]> itemsets;

        public Node(int offset) {                   // create index node

            next = new Node[offset];
            itemsets = null;
        }

        public Node(AprioriItemset items) {                 // create leaf node

            next = null;
            itemsets = new HashMap<AprioriItemset, int[]>();
            itemsets.put(items, new int[] {1});
        }

        public Node(AprioriItemset items, int[] support) {  // create leaf node

            next = null;
            itemsets = new HashMap<AprioriItemset, int[]>();
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

    private Node root;
    private int offset, size;
    private double threshold;
    // support leafs iterator
    private ArrayDeque<Node> queue;
    private Iterator<Map.Entry<AprioriItemset, int[]>> leafIterator;
    private int counter;

    public AprioriCandidatesHashTree(int k, double minsupp) {

        root = new Node(k);
        offset = k;
        threshold = minsupp;
        size = 0;
        queue = null;
        leafIterator = null;
        counter = 0;
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

        int i, v;
        boolean added = false;
        AprioriItemset newItemset = new AprioriItemset();
        newItemset.setItems(items);
        newItemset.setLevel(0);

        Node current = root, node;
        while (newItemset.getLevel() < offset && !added) {

            i = newItemset.hashValue();
            node = current.next[i];
            if (node == null) {                                 // empty leaf node

                current.next[i] = new Node(newItemset);
                ++size;
                added = true;
            } else if (node.isLeafNode()) {                     // leaf node

                int[] support = node.itemsets.get(newItemset);
                if (support != null) {                          // already exists

                    ++support[0];
                    added = true;
                } else if (node.itemsets.size() < offset        // has space or
                    || (newItemset.getLevel() + 1) == offset) { // maximum level reached

                    node.itemsets.put(newItemset, new int[] {1});
                    ++size;
                    added = true;
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
        AprioriItemset newItemset = new AprioriItemset();
        newItemset.setItems(Arrays.copyOfRange(transaction, prefix, prefix + offset));
        newItemset.setLevel(0);

        Node current = root, node;
        while (newItemset.getLevel() < offset ) {

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
     * Extract frequent AprioriItemsets using support threshold.
     */
    public void supportFiltering() {

        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if (node.isLeafNode()) {

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

    /**
     * Candidate Generation using F_(k-1) X F_(k-1) Method
     * only for each leaf node.
     * @return F_(k) candidates hash tree
     */
    public AprioriCandidatesHashTree aprioriGen() {

        AprioriCandidatesHashTree candidates = new AprioriCandidatesHashTree(offset + 1, threshold);
        ArrayList<AprioriItemset> itemsets = getItemsets();
        for(int i = 0; i < itemsets.size() - offset; i ++) {

            for(int j = i + 1; j < itemsets.size(); j++){

                if(itemsets.get(i).isMergeable(itemsets.get(j))){                            // check condition

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


    public int getSupportByItemset(AprioriItemset itemset){


        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        int[] support = null;
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if (node.isLeafNode()) {

                if((support = node.itemsets.get(itemset)) != null){

                    return support[0];
                }
            } else {

                for (int i = 0; i < offset; i++) {

                    if (node.next[i] != null) {

                        q.add(node.next[i]);
                    }
                }
            }
        }
        return -1;
    }

    // iterator api
    @Override public boolean hasNext() {

        if(queue  == null && size > 0){             // 1st call

            queue = new ArrayDeque<Node>();
            queue.add(root);
            return true;
        }

        if(counter < size) {                        // n call

            return true;
        }
        queue = null;                                // re-init
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
