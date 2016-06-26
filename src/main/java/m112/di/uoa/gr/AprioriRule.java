package m112.di.uoa.gr;

import com.sun.deploy.security.ruleset.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ppetrou on 23/6/2016.
 */
public class AprioriRule {

    private int[] current_itemset;
    protected List<RuleElement> rules;
    private int possible_combinations;

    public AprioriRule(int[] current_itemset, int possible_combinations) {
        this.current_itemset = current_itemset;
        rules=new ArrayList();
        this.possible_combinations=possible_combinations;
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
        return possible_combinations;
    }

    public void setPossible_combinations(int possible_combinations) {
        this.possible_combinations = possible_combinations;
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
