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
    private double min_cofidence = 0.50;
    private int current_support;
    private int k, loops;
    private int elements[];

    public AprioriAssociationRulesGeneration(List<AprioriCandidatesHashTree> kCandidateTrees, double min_confidence) {
        trees = kCandidateTrees;
        ktree=0;
        apriori_rules = new AprioriAssociationRule(trees);
        min_cofidence=min_confidence;
    }

    @Override public boolean hasNext() {
        ktree++;
        return ktree < trees.size();
    }

    @Override public List<AprioriRule> next() {

        List<AprioriRule> rules_result=new ArrayList<AprioriRule>();

        while (trees.get(ktree).hasNext()) {
            AprioriItemset current_itemset = trees.get(ktree).next();
            elements = current_itemset.getItems().clone();
            apriori_rules.elements_all = current_itemset.getItems().clone();
            apriori_rules.update = new int[apriori_rules.elements_all.length];
            apriori_rules.combination_left = new int[apriori_rules.elements_all.length];
            current_support = current_itemset.getSupport();
            apriori_rules.rule_elements=new ArrayList();

            k = 1;
            loops = elements.length - 1;
            while (k <= elements.length & loops >= 1) {
                elements = apriori_rules.combination(elements, k, elements.length, current_support, min_cofidence);
                k++;
                loops--;
            }

        }
        return rules_result;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

