/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m112.di.uoa.gr;

import java.io.*;
import java.util.*;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 *
 * @author ppetrou
 */
public class AprioriMainClass {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        long testing = System.currentTimeMillis();

        Scanner scanner = new Scanner(new File("ratings-medium.dat"));
        HashMap<Integer, TreeSet> baskets = new HashMap<Integer, TreeSet>();
        int number_baskets = 0, threshold;
        double minsup = 0.20;
        ArrayList<HashMap> itemsets = new ArrayList<HashMap>();
        //BufferedReader scanner = new BufferedReader(new FileReader("ratings.csv"));
        TreeSet<Integer> basket_temp = new TreeSet<Integer>();
        ArrayList key = new ArrayList<Integer>();

        HashMap<ArrayList, MutableInt> CandiMap = new HashMap<ArrayList, MutableInt>();

        int user;
        int item_temp;
        while (scanner.hasNext()) {
            //scanner.readLine();
            //String line = scanner.readLine();
            //while (line != null) {
            String[] parts = scanner.next().split("::");
            //String[] parts = line.split("\t");
            //String[] parts = line.split(",");
            user = Integer.valueOf(parts[0]);
            item_temp = Integer.valueOf(parts[1]);
            key = new ArrayList();
            key.add(item_temp);
            if (CandiMap.containsKey(key)) {
                CandiMap.get(key).increment();
            } else {
                CandiMap.put(key, new MutableInt(1));
            }

            if (baskets.containsKey(user)) {
                basket_temp = baskets.get(user);
                basket_temp.add(item_temp);
                baskets.put(user, basket_temp);

            } else {
                basket_temp = new TreeSet<Integer>();
                basket_temp.add(item_temp);
                baskets.put(user, basket_temp);

                number_baskets++;
            }
            //line = scanner.readLine();
        }

        threshold = (int) Math.round(minsup * number_baskets);
        //System.out.println(threshold);

        Iterator<Map.Entry<ArrayList, MutableInt>> iter = CandiMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<ArrayList, MutableInt> entry = iter.next();
            if (entry.getValue().intValue() < threshold) {
                iter.remove();
            }
        }

        itemsets.add(CandiMap);
        key = new ArrayList(CandiMap.keySet());
        System.out.println(CandiMap);

        long testing2 = System.currentTimeMillis();
        System.out.println(((testing2 - testing) / 1000) % 60);
    }
}
