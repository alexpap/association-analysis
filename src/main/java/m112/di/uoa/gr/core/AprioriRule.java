package m112.di.uoa.gr.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ppetrou on 23/6/2016.
 */
public class AprioriRule {

    private int[] current_itemset;
    protected List<RuleElement> rules;
    private int candidate_rules;
    private int final_rules;

    public AprioriRule(int[] current_itemset, int possible_combinations) {
        this.current_itemset = current_itemset;
        rules=new ArrayList();
        this.candidate_rules=possible_combinations;
    }

    public int[] getCurrent_itemset() {
        return current_itemset;
    }

    public void setCurrent_itemset(int[] current_itemset) {
        this.current_itemset = current_itemset;
    }

    public boolean add (RuleElement rl) {
        return rules.add(rl);
    }

    public int getPossible_combinations() {
        return candidate_rules;
    }

    public void setPossible_combinations(int candidate_rules) {
        this.candidate_rules = candidate_rules;
    }

    public int getFinal_rules() {
        return final_rules;
    }

    public void setFinal_rules(int final_rules) {
        this.final_rules = final_rules;
    }

    public List<RuleElement> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        String result= "AprioriRule {" +
                "current_itemset=" + Arrays.toString(current_itemset)+"\n";

        for (int i=0; i<rules.size(); i++) {
            result=result+"rule="+
                    Arrays.toString(rules.get(i).getHead())+" -> "+
                    Arrays.toString(rules.get(i).getBody())+
                    ", rule_confidence="+rules.get(i).getRule_confidence()+"\n";
        }

        return result+"}";
    }
}
