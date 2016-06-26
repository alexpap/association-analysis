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
    protected int possible_combinations;
    protected AprioriRule aprioriRule;

    public AprioriAssociationRule(List<AprioriCandidatesHashTree> kCandidateTrees) {
        this.trees=kCandidateTrees;
    }

    public int[] combination(int[] elements, int k, int n, int current_support, double min_confidence) {

        int combination_right[] = new int[k];
        int output_right[] = new int[k];
        int output_left[] = new int[elements_all.length - k];
        int counter_right = 0;

        double current_confidence;
        
        int r = 0, i;
        int index = 0;
        aprioriRule = new AprioriRule(elements_all, possible_combinations);
        while (r >= 0) {

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

                    current_confidence = (double) current_support / trees.get(output_left.length - 1).getSupportByItems(output_left);
                    
                    if (current_confidence < min_confidence) {
                        
                        for (i=0; i<output_right.length; i++) {
                            update[Arrays.binarySearch(elements_all, output_right[i])] = 1;
                            counter_right++;
                        }
                        
                    } else {
                        RuleElement rule = new RuleElement(output_left.clone(), output_right.clone(), current_confidence);
                        aprioriRule.add(rule);
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