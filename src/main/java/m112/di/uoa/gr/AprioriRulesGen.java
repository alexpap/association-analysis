/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 *
 * @author ppetrou
 */
public class AprioriRulesGen {

    private static final Logger log = Logger.getLogger(AprioriRulesGen.class);
    
    private static int elements_all[];
    private static int update[];
    private static int combination_left[];
    
    public static int[] combination(int[] elements, int k, int n) {

        int combination_right[] = new int[k];
        int output_right[]=new int[k];
        int output_left[]=new int[elements_all.length-k];
        int counter_right=0;
        int r = 0, i;
        int index = 0;

        while (r >= 0) {

            if (index <= (n + (r - k))) {
                combination_right[r] = index;

                if (r == k - 1) {

                    for (i=0; i<combination_right.length; i++){
                        output_right[i]=elements[combination_right[i]];
                    }
                    
                    
                    Arrays.fill(combination_left, 0);
                    for (i=0; i<output_right.length; i++) {
                        int position = Arrays.binarySearch(elements_all, output_right[i]);
                        if (position>=0) {
                            combination_left[position]=1;
                        }
                    }                                    
                    
                    
                    int counter_left=0;
                    for (i=0; i<combination_left.length; i++) {
                        if (combination_left[i]!=1) {
                            output_left[counter_left]=elements_all[i];
                            counter_left++;
                        }
                    }
                    
                    log.debug(Arrays.toString(output_left)+" -> "+Arrays.toString(output_right));
                    
                    if (output_right.length==1 & output_right[0]==1) {
                        update[Arrays.binarySearch(elements_all, output_right[0])]=1;
                        counter_right++;
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
        
        int counter=0;
        int result[]=new int[n-counter_right];
        
        for (i = 0; i < update.length; i++) {
            if (update[i] != 1) {
                result[counter] = elements_all[i];
                counter++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] elements = new int[]{1, 2, 3};
        int k=1;
        elements_all=elements.clone();
        update=new int[elements_all.length];
        combination_left = new int [elements_all.length];
        
        while (k<=elements.length) {
            elements=combination(elements, k, elements.length);
            k++;
        }
    }
}
