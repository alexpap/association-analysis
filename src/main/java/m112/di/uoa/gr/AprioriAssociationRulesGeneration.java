package m112.di.uoa.gr;

import java.util.Arrays;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 */

public class AprioriAssociationRulesGeneration implements Iterator<AprioriAssociationRule> {

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

    @Override public AprioriAssociationRule next() {
        
        while (trees.get(ktree).hasNext()) {
            AprioriItemset current_itemset = trees.get(ktree).next();
            System.out.println("\n");
            log.debug("Current itemset " + Arrays.toString(current_itemset.getItems()) + " itemset's support " + current_itemset.getSupport());
            elements = current_itemset.getItems().clone();
            apriori_rules.elements_all = current_itemset.getItems().clone();
            apriori_rules.update = new int[apriori_rules.elements_all.length];
            apriori_rules.combination_left = new int[apriori_rules.elements_all.length];
            current_support = current_itemset.getSupport();

            k = 1;
            loops = elements.length - 1;
            while (k <= elements.length & loops >= 1) {
                elements = apriori_rules.combination(elements, k, elements.length, current_support, min_cofidence);
                k++;
                loops--;
            }
        }
        return 10;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

