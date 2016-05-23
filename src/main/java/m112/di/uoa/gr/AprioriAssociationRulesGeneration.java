package m112.di.uoa.gr;

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
    private int current_k;

    public AprioriAssociationRulesGeneration(List<AprioriCandidatesHashTree> kCandidateTrees) {

        trees = kCandidateTrees;
        current_k = 0;
    }

    @Override public boolean hasNext() {

        return current_k < trees.size();
    }

    @Override public AprioriAssociationRule next() {

        if(!hasNext()) throw new NoSuchElementException();

        // TODO gen new rule
        // for each tree
        // for each itemset
        // return new Rule
        AprioriAssociationRule rule = new AprioriAssociationRule();
        current_k++;

        return rule;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
