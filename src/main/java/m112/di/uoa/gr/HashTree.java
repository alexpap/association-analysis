package m112.di.uoa.gr;

import java.util.*;

/**
 * Created by alex on 23/04/16.
 */
public class HashTree {

    private abstract class Node {

        public abstract boolean isLeaf();
        public abstract int[] get(int[] key, int level);
    }

    private class IndexNode extends Node {

        Node[] next;

        public IndexNode(int k) {
            next = new Node[k];
        }

        @Override public boolean isLeaf() {
            return false;
        }

        @Override public int[] get(int[] key, int level) {

            Node node = next[key[level] % next.length];
            return null;
        }
    }

    private class LeafNode extends Node{

        HashMap<int[], int[]> data;

        public LeafNode(int[] key, int[] value) {
            data = new HashMap<int[], int[]>();
            data.put(key, value);
        }

        @Override public boolean isLeaf() {
            return true;
        }

        @Override public int[] get(int[] key, int level) {
            return data.get(key);
        }
    }

    private Node root;
    private int offset, size;

    public HashTree(int k){

        root = new IndexNode(k);
        offset = k;
        size = 0;
    }

    public void frequencyIncrement(int[] key, int[] value){

    }

}
