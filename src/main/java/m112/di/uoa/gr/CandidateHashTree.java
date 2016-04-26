package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author alexpap
 */
public class CandidateHashTree {

    private static final Logger log = Logger.getLogger(CandidateHashTree.class);

    private class Itemset{

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

        @Override public int hashCode() { return items[level]; }

        public int hashValue(){ return items[level] % items.length; }

        public void increaseLevel(){ ++level; }

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

        public Node(Itemset items){                 // create leaf node

            next = null;
            itemsets = new HashMap<Itemset, int[]>();
            itemsets.put(items, new int[]{1});
        }

        public Node(Itemset items, int[] support){  // create leaf node

            next = null;
            itemsets = new HashMap<Itemset, int[]>();
            itemsets.put(items, support);
        }

        public boolean isLeafNode(){ return itemsets != null; }
    }

    private Node root;
    private int offset, size;
    private double support_threshold;

    public CandidateHashTree(int k, double threshold){

        root = new Node(k);
        offset = k;
        support_threshold = threshold;
        size = 0;
    }

    public int size(){ return size; }

    public void frequencyIncrement(int[] items){

        int i;
        boolean added = false;
        Itemset newItemset = new Itemset(items, 0);
        Node current = root, node;
        while(newItemset.level < offset && !added){

            i = newItemset.hashValue();
            node = current.next[i];
            if(node == null){                           // empty leaf node

                current.next[i] = new Node(newItemset);
                ++size; added = true;
            } else if (node.isLeafNode()){              // leaf node

                int[] support = node.itemsets.get(newItemset);
                if(support != null){                        // already exists

                    ++support[0]; added = true;
                }else if(node.itemsets.size() < offset      // has space or
                    || (newItemset.level + 1) == offset){   // maximum level reached

                    node.itemsets.put(newItemset, new int[] {1});
                    ++size; added = true;
                } else {                                // create new index Node
                                                        // & rehash old itemsets
                    Node indexNode = new Node(offset);
                    Itemset oldItems;
                    current.next[i] = indexNode;    // unlink previous leaf
                    for (Map.Entry<Itemset, int[]> entry : node.itemsets.entrySet()) {

                        oldItems = entry.getKey();
                        support = entry.getValue();
                        oldItems.increaseLevel();   // increase level & rehash
                        if(indexNode.next[oldItems.hashValue()] == null){

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

        if(!added){
            throw new RuntimeException("Unable to insert new itemset");
        }
    }

    public void supportFiltering() {

        int minsup = (int) (support_threshold*size);
        ArrayDeque<Node> q = new ArrayDeque<Node>();
        q.add(root);
        Node node;
        while(!q.isEmpty()){

            node = q.removeFirst();
            if(node.isLeafNode()){

                Iterator<Map.Entry<Itemset, int[]>> it = node.itemsets.entrySet().iterator();
                while(it.hasNext()){

                    Map.Entry<Itemset, int[]> entry = it.next();
                    if(entry.getValue()[0] < minsup){
                        it.remove();
                        --size;
                    }
                }
            } else{

                for (int i =0; i < offset; i++){

                    if(node.next[i] != null){

                        q.add(node.next[i]);
                    }
                }
            }
        }

    }


    public CandidateHashTree aprioriGen() {
        return null;
    }

    public void supportCounting(int[] transaction) {
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
