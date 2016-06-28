package m112.di.uoa.gr;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 */

public class AprioriAssociationRulesGeneration implements Iterator<List<AprioriRule>> {

    private static final Logger log = Logger.getLogger(AprioriAssociationRulesGeneration.class);
    private List<AprioriCandidatesHashTree> trees;
    private int ktree;
    private AprioriAssociationRule apriori_rules;
    private double min_cofidence;
    private int current_support;
    private List<AprioriRule> rules_all;
    private int rule_counter;
    private int temp;

    public AprioriAssociationRulesGeneration(List<AprioriCandidatesHashTree> kCandidateTrees, double min_confidence, List<AprioriRule> rules_all) {
        trees = kCandidateTrees;
        ktree=0;
        apriori_rules = new AprioriAssociationRule(trees);
        min_cofidence=min_confidence;
        this.rules_all=rules_all;
    }

    @Override public boolean hasNext() {
        ktree++;
        return ktree < trees.size();
    }

    @Override public List<AprioriRule> next() {

        //List<AprioriRule> rules_result=new ArrayList<AprioriRule>();
        temp=0;
        rule_counter=0;
        while (trees.get(ktree).hasNext()) {
            AprioriItemset current_itemset = trees.get(ktree).next();
            int elements[] = current_itemset.getItems().clone();
            apriori_rules.elements_all = current_itemset.getItems().clone();
            apriori_rules.update = new int[apriori_rules.elements_all.length];
            apriori_rules.combination_left = new int[apriori_rules.elements_all.length];
            current_support = current_itemset.getSupport();

            int counter=0;
            for (int i=1; i<elements.length; i++) {
                counter=counter+possible_combinations(elements.length, i);
            }
            apriori_rules.possible_combinations=counter;

            int k = 1;
            int loops = elements.length - 1;
            while (k <= elements.length & loops >= 1) {
                elements = apriori_rules.combination(elements, k, elements.length, current_support, min_cofidence);
                k++;
                loops--;
            }
            if (!apriori_rules.aprioriRule.rules.isEmpty()) {
                rules_all.add(apriori_rules.aprioriRule);
                temp++;
            }
            apriori_rules.aprioriRule.setFinal_rules(apriori_rules.aprioriRule.rules.size());
        }
        return rules_all.subList(rule_counter, temp);
    }

    public int possible_combinations(int n, int r){
        int nf=fact(n);
        int rf=fact(r);
        int nrf=fact(n-r);
        int npr=nf/nrf;
        int ncr=npr/rf;

        return ncr;
    }

    public int fact(int n)
    {
        if(n == 0)
            return 1;
        else
            return n * fact(n-1);
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

