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

    public AprioriAssociationRulesGeneration(List<AprioriCandidatesHashTree> kCandidateTrees) {
        trees = kCandidateTrees;
    }

    @Override public boolean hasNext() {
        
    }

    @Override public AprioriAssociationRule next() {
        
    }

    @Override public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
