package m112.di.uoa.gr;

import org.apache.commons.lang3.SystemUtils;
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

    private static final Logger log = Logger.getLogger(AprioriCandidatesHashTree.class);

    private class Node {

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
     * Increase the counter for the given itemset @items.
     * If the given itemset does not exist,
     * then new set will created with counter value 1.
     * @param items
     */
    public void frequencyIncrement(int[] items, boolean gen) {

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

                if (gen) current.next[i] = new Node(newItemset, new int[] {0});
                else current.next[i] = new Node(newItemset);
                ++size;
                added = true;
            } else if (node.isLeafNode()) {                     // leaf node

                int[] support = node.itemsets.get(newItemset);
                if (support != null) {                          // already exists

                    ++support[0];
                    added = true;
                } else if (node.itemsets.size() < offset        // has space or
                    || (newItemset.getLevel() + 1) == offset) { // maximum level reached

                    if(gen) node.itemsets.put(newItemset, new int[] {0});
                    else node.itemsets.put(newItemset, new int[] {1});
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
    private boolean checkFrequency(int[] items){

        int supp = getSupportByItems(items);
        return supp >= threshold;
    }

    private static int[] isMergeable(AprioriCandidatesHashTree tree, int[] is1, int[] is2){

        if (is1.length != is2.length) return null;
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

            for(int j = i + 1; j < itemsets.size(); j++){

                items = isMergeable(this, itemsets.get(i).getItems(),itemsets.get(j).getItems());
                if(items != null){

                    candidates.frequencyIncrement(items, true);
                }
            }
        }
        return candidates;
    }

    private void clearVisited(){

        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while (!q.isEmpty()) {

            node = q.removeFirst();
            if (node.isLeafNode()) {

               node.visited = false;
            } else {

                for (int i = 0; i < offset; i++) {

                    if (node.next[i] != null) {

                        q.add(node.next[i]);
                    }
                }
            }
        }
    }

    private static void frequencyIncrement(int[] transaction, int k, Node node, int index){

        if(node == null) return;

        if(node.isLeafNode()){

            if(!node.visited) {

                int[] items;
                int i, search;
                for (Map.Entry<AprioriItemset, int[]> entry : node.itemsets.entrySet()) {

                    items = entry.getKey().getItems();
                    for (i = 0; i < k; i++) {

                        search = Arrays.binarySearch(transaction, 0, transaction.length, items[i]);
                        if (search < 0) {

                            break;
                        }
                    }

                    if (i == k) {

                        ++entry.getValue()[0];
                    }
                }
                node.visited = true;
            }
        } else {

            for (int i = index; i < transaction.length - k; i++) {

                AprioriCandidatesHashTree
                    .frequencyIncrement(transaction, k, node.next[transaction[i] % k] , i + 1);
            }
        }
    }

    /**
     *
     * @param transaction
     */
    public void supportCounting(int[] transaction){

        AprioriCandidatesHashTree.frequencyIncrement(transaction, offset, root, 0);
        clearVisited();
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
