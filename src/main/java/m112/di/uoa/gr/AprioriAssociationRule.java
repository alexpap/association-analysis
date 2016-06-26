package m112.di.uoa.gr;

/**
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class AprioriAssociationRule {
    
    private static final Logger log = Logger.getLogger(AprioriAssociationRule.class);
    private List<AprioriCandidatesHashTree> trees;
    protected int[] elements_all;
    protected int update[];
    protected int combination_left[];
    protected ArrayList<AprioriRule> rule_elements;

    public AprioriAssociationRule(List<AprioriCandidatesHashTree> kCandidateTrees) {
        this.trees=kCandidateTrees;
    }
    
    public int[] combination(int[] elements, int k, int n, int current_support, double min_confidence) {

        int combination_right[] = new int[k];
        int output_right[] = new int[k];
        int output_left[] = new int[elements_all.length - k];
        int counter_right = 0;

        //rule_confidence = new ArrayList();
        //int temp_support;
        double current_confidence;
        
        int r = 0, i;
        int index = 0;

        while (r >= 0) {
            //AprioriRule rule= new AprioriRule();
            if (index <= (n + (r - k))) {
                combination_right[r] = index;

                if (r == k - 1) {

                    for (i = 0; i < combination_right.length; i++) {
                        output_right[i] = elements[combination_right[i]];
                    }

                    Arrays.fill(combination_left, 0);
                    for (i = 0; i < output_right.length; i++) {
                        int position = Arrays.binarySearch(elements_all, output_right[i]);
                        if (position >= 0) {
                            combination_left[position] = 1;
                        }
                    }

                    int counter_left = 0;
                    for (i = 0; i < combination_left.length; i++) {
                        if (combination_left[i] != 1) {
                            output_left[counter_left] = elements_all[i];
                            counter_left++;
                        }
                    }

                    //temp_support = ;
                    current_confidence = (double) current_support / trees.get(output_left.length - 1).getSupportByItems(output_left);
                    
                    if (current_confidence < min_confidence) {
                        
                        for (i=0; i<output_right.length; i++) {
                            update[Arrays.binarySearch(elements_all, output_right[i])] = 1;
                            counter_right++;
                        }
                        
                    } else {
                        //TODO
                        rule_elements.add(new AprioriRule(elements_all, output_left, current_confidence, output_right));
                        /*
                        rules_result.add("Current itemset " + Arrays.toString(elements_all) + " itemset's support " + current_support
                                +" Confidence " + Arrays.toString(output_left) + " -> "
                                + Arrays.toString(output_right) + " = " + current_confidence
                                + " X " + Arrays.toString(output_left) + " support " + temp_support);
                                */
                    }

                    index++;
                } else {
                    index = combination_right[r] + 1;
                    r++;
                }
            } else {
                r--;
                if (r > 0) {
                    index = combination_right[r] + 1;
                } else {
                    index = combination_right[0] + 1;
                }
            }
        }
        //TODO
        for (i=0; i<rule_elements.size(); i++) {
            log.debug(rule_elements.get(i).toString());
        }

        if (n > counter_right) {
            int result[] = new int[n - counter_right];
            int counter = 0;
            for (i = 0; i < update.length; i++) {
                if (update[i] != 1) {
                    result[counter] = elements_all[i];
                    counter++;
                }
            }
            return result;
        } else {
            int result[]=new int[]{-1};
            return result;
        }
    }
}