package m112.di.uoa.gr;

/**
 *
 */

import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

public class AprioriAssociationRule {
    
    private static final Logger log = Logger.getLogger(AprioriFrequentItemsetGeneration.class);
    private List<AprioriCandidatesHashTree> trees;
    private int current_k;
    
    public AprioriAssociationRule(List<AprioriCandidatesHashTree> kCandidateTrees) {
        this.trees=kCandidateTrees;
    }
    
    public int [] combination(int[] elements, int k, int n, int current_support, double min_confidence) {

        int combination_left[] = new int[k];
        int output_right[] = new int [n-k];
        int output_left[]=new int[k];
        int counter_right;
        
        int updater[]=new int[n];
        int updater_count=0;
        int temp_support;
        double current_confidence;
        
        int r = 0;
        int index = 0;

        while (r >= 0) {

            if (index <= (n + (r - k))) {
                combination_left[r] = index;

                if (r == k - 1) {

                    for (int z = 0; z < combination_left.length; z++) {
                        output_left[z] = elements[combination_left[z]];
                    }
                    
                    counter_right=0;
                    for (int j=0; j<elements.length; j++) {
                        if (Arrays.binarySearch(combination_left, j)<0) {
                            output_right[counter_right]=elements[j];
                            counter_right++;
                        }
                    }
                    
                    temp_support = trees.get(output_left.length - 1).getSupportByItems(output_left);
                    
                    log.debug("X "+ Arrays.toString(output_left)+" support "+temp_support);
                    
                    current_confidence = (double) current_support / temp_support;
                    
                    
                    
                    if (current_confidence < min_confidence) {
                        log.debug("Delete Confidence " + Arrays.toString(output_left) + " -> "
                                + Arrays.toString(output_right) + " = " + current_confidence);
                    } else {
                        log.debug("Confidence " + Arrays.toString(output_left) + " -> "
                                + Arrays.toString(output_right));
                        
                        for (int z = 0; z < combination_left.length; z++) {
                           if (updater[combination_left[z]] != 1) {
                               updater[combination_left[z]] = 1;
                               updater_count++;
                           }
                        }
                    }
                    index++;
                    
                } else {
                    index = combination_left[r] + 1;
                    r++;
                }
            } else {
                r--;
                if (r > 0) {
                    index = combination_left[r] + 1;
                } else {
                    index = combination_left[0] + 1;
                }
            }
        }
        
        log.debug(Arrays.toString(updater));
        
        int result[]=new int[updater_count];
        r=0;
        for (int i=0; i<updater.length; i++) {
            if (updater[i]==1) {
                result[r]=elements[i];
                r++;
            }
        }       
        log.debug(Arrays.toString(result));
        return result;
    }
}