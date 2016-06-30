package m112.di.uoa.gr.expeval;

import m112.di.uoa.gr.core.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alexpap
 */
public class AprioriExpEval {
    private static final Logger log = Logger.getLogger(AprioriExpEval.class);

    public static void main(String[] args) {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        MovieLensDatasetType[] inputTypes =
            new MovieLensDatasetType[] {MovieLensDatasetType.ml_100k,
                MovieLensDatasetType.ml_1m, MovieLensDatasetType.ml_10m};
        double[] minSuppValues = new double[] {0.5, 0.4, 0.3, 0.2, 0.1};

        for (MovieLensDatasetType inputType : inputTypes) {

            log.info("** INPUT TYPE " + inputType.toString());
            for (double minSuppValue : minSuppValues) {

                log.info("** MIN SUPP " + minSuppValue);
                AprioriFrequentItemsetGeneration frequentItemsetGeneration =
                    new AprioriFrequentItemsetGeneration(minSuppValue);
                List<AprioriCandidatesHashTree> trees = new ArrayList<>();

                frequentItemsetGeneration.preprocess(inputType);
                while(frequentItemsetGeneration.hasNext()){
                    AprioriCandidatesHashTree candidatesHashTree = frequentItemsetGeneration.next();
                    if(candidatesHashTree.size() > 0) trees.add(candidatesHashTree);
                }
            }

        }

    }

}
