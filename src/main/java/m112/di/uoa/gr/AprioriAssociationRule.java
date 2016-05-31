package m112.di.uoa.gr;

/**
 *
 */

import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

public class AprioriAssociationRule {
    
    private static final Logger log = Logger.getLogger(AprioriAssociationRule.class);
    private List<AprioriCandidatesHashTree> trees;
    protected int[] elements_all;
    protected int update[];
    protected int combination_left[];
    
    public AprioriAssociationRule(List<AprioriCandidatesHashTree> kCandidateTrees) {
        this.trees=kCandidateTrees;
    }
    
    public int[] combination(int[] elements, int k, int n, int current_support, double min_confidence) {

        int combination_right[] = new int[k];
        int output_right[] = new int[k];
        int output_left[] = new int[elements_all.length - k];
        int counter_right = 0;
        
        int temp_support;
        double current_confidence;
        
        int r = 0, i;
        int index = 0;

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
                    
                    temp_support = trees.get(output_left.length - 1).getSupportByItems(output_left);
                    current_confidence = (double) current_support / temp_support;
                    
                    if (current_confidence < min_confidence) {
                        //log.debug("Delete Confidence " + Arrays.toString(output_left) + " -> "
                                //+ Arrays.toString(output_right) + " = " + current_confidence
                                //+ " X " + Arrays.toString(output_left) + " support " + temp_support);
                        
                        for (i=0; i<output_right.length; i++) {
                            update[Arrays.binarySearch(elements_all, output_right[i])] = 1;
                            counter_right++;
                        }
                        
                    } else {
                        log.debug("Confidence " + Arrays.toString(output_left) + " -> "
                                + Arrays.toString(output_right) + " = " + current_confidence
                                + " X " + Arrays.toString(output_left) + " support " + temp_support);
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
    /*
    public void combination(int[] elements, int k, int n, int current_support, double min_confidence) {

        int combination_left[] = new int[k];
        int output_left[] = new int[k];
        
        int output_right[] = new int[n - k];
        int combination_right[] = new int[n-k];
        
        int counter_right, i;
        //int updater_left[] = new int[elements_all.length];

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
                            combination_right[counter_right]=j;
                            counter_right++;
                        }
                    }
                    
                    temp_support = trees.get(output_left.length - 1).getSupportByItems(output_left);
                    
                    log.debug("X "+ Arrays.toString(output_left)+" support "+temp_support);
                    
                    current_confidence = (double) current_support / temp_support;
                    
                    
                    
                    if (current_confidence < min_confidence) {
                        log.debug("Delete Confidence " + Arrays.toString(output_left) + " -> "
                                + Arrays.toString(output_right) + " = " + current_confidence+ 
                                " X "+ Arrays.toString(output_left)+" support "+temp_support);
                    } else {
                        log.debug("Confidence " + Arrays.toString(output_left) + " -> "
                                + Arrays.toString(output_right) + " = " + current_confidence+ 
                                " X "+ Arrays.toString(output_left)+" support "+temp_support);
                        /*
                        for (int z = 0; z < combination_right.length; z++) {
                           if (updater[combination_right[z]] != 1) {
                               updater[combination_right[z]] = 1;
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
        /*
        log.debug(Arrays.toString(updater));
        
        int result[]=new int[updater_count];
        r=0;
        for (int i=0; i<updater.length; i++) {
            if (updater[i]==1) {
                result[r]=elements[i];
                r++;
            }
        }
        //log.debug(Arrays.toString(result));
        //return result;
    }*/
}