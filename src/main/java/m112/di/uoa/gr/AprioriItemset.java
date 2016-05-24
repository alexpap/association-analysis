package m112.di.uoa.gr;

import java.util.Arrays;

/**
 * Provides Itemset abstraction capable of:
 *  + increase the level of the itemset ( used only internally for the CandidatesHashTree)
 *  + increase the support counter of the particular itemset
 *  + checks mergeable condition
 *  + merges two itemsets
 * @author alexpap
 */
public class AprioriItemset {

    private int[] items;
    private int   level, support;

    public AprioriItemset() {
        items = null;
        level = 0;
        support = 0;
    }

    public int[] getItems() {
        return items;
    }

    public void setItems(int[] items) {
        this.items = items;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int hashValue() {
        return items[level] % items.length;
    }

    public void increaseLevel() {
        level = (level == items.length - 1) ? items.length - 1 : level + 1;
    }

    public void increaseSupport(){ ++support;}

    /**
     * Let A = {a_1, a_2, ..., a_(l-1)} and B = {b_1, b_2, ..., b_(k-1)}
     * where A is this itemset, B is the given @itemset
     *
     * @param itemset
     * @return true if a_i == b_i (for i= 1,2, ..., k-2) and a_(k-1) != b_(k-1), else false
     */
    public boolean isMergeable(AprioriItemset itemset) {

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
     * Create the union of this itemset items and the given @itemset items
     * @param itemset
     * @return new items
     */
    public int[] merge(AprioriItemset itemset) {

        int[] newset = new int[items.length + 1];
        System.arraycopy(items, 0, newset, 0, items.length);
        newset[items.length] = itemset.items[items.length - 1];
        return newset;
    }

    @Override public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AprioriItemset itemset = (AprioriItemset) o;

        return Arrays.equals(items, itemset.items);

    }

    @Override public int hashCode() {
        return items[level];
    }

    @Override public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("{ items : [");
        if(items.length > 0) builder.append(items[0]);
        for (int i =1; i < items.length; i++) {

            builder.append(",");
            builder.append(items[i]);
        }

        builder.append("], support :");
        builder.append(support);
        builder.append(", level :");
        builder.append(level);
        builder.append(" }");
        return builder.toString();
    }
}
