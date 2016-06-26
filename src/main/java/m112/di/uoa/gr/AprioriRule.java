package m112.di.uoa.gr;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ppetrou on 23/6/2016.
 */
public class AprioriRule {

    private int[] current_itemset;
    private int[] lhs;
    private int[] rhs;
    private double rule_confidence;

    public AprioriRule(int[] current_itemset, int[] lhs, double rule_confidence, int[] rhs) {
        this.current_itemset = current_itemset;
        this.lhs = lhs;
        this.rule_confidence = rule_confidence;
        this.rhs = rhs;
    }

    public int[] getCurrent_itemset() {
        return current_itemset;
    }

    public void setCurrent_itemset(int[] current_itemset) {
        this.current_itemset = current_itemset;
    }

    public int[] getLhs() {
        return lhs;
    }

    public void setLhs(int[] lhs) {
        this.lhs = lhs;
    }

    public int[] getRhs() {
        return rhs;
    }

    public void setRhs(int[] rhs) {
        this.rhs = rhs;
    }

    public double getRule_confidence() {
        return rule_confidence;
    }

    public void setRule_confidence(double rule_confidence) {
        this.rule_confidence = rule_confidence;
    }

    @Override
    public String toString() {
        return "AprioriRule{" +
                "current_itemset=" + Arrays.toString(current_itemset) +
                ", rule=" + Arrays.toString(lhs) +
                " -> " + Arrays.toString(rhs) +
                ", rule_confidence=" + rule_confidence +
                '}';
    }
}
