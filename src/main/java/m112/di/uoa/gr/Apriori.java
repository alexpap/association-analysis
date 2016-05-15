/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m112.di.uoa.gr;

/**
 *
 * @author ppetrou
 */
import java.util.*;
import org.apache.commons.lang3.mutable.MutableInt;

public class Apriori {
    
    public Apriori () {
        
    }

    public void frequent_itemsets(ArrayList key, ArrayList itemsets, HashMap<Integer, TreeSet> baskets, int threshold) {

        ArrayList temp;
        ArrayList temp2 = new ArrayList<Integer>();
        ArrayList temp3;

        do {
            LinkedHashMap<ArrayList, MutableInt> CandiMap = new LinkedHashMap<ArrayList, MutableInt>();
            temp3 = new ArrayList<ArrayList>();
            for (int i = 0; i <= key.size() - 1; i++) {

                for (int j = i + 1; j < key.size(); j++) {
                    temp = new ArrayList((ArrayList) key.get(i));
                    temp2 = (ArrayList) key.get(j);

                    if (temp.subList(0, temp.size() - 1).containsAll(temp2.subList(0, temp2.size() - 1))) {
                        temp.add(temp2.get(temp2.size() - 1));
                    } else {
                        break;
                    }
                    temp3.add(temp);
                    /*
                    counter = 0;
                    for (Map.Entry<Integer, TreeSet> entry : baskets.entrySet()) {
                        if (entry.getValue().containsAll(temp)) {
                            counter++;
                        }
                    }

                    if (counter >= threshold) {
                        CandiMap.put(temp, new MutableInt(counter));
                    }*/
                }
            }

            for (Map.Entry<Integer, TreeSet> entry : baskets.entrySet()) {
                for (int i=0; i<temp3.size(); i++) {
                    temp=(ArrayList)temp3.get(i);
                    
                    if (entry.getValue().containsAll(temp)) {
                        if (CandiMap.containsKey(temp)) {
                            CandiMap.get(temp).increment();
                        } else {
                            CandiMap.put(temp, new MutableInt(1));
                        }
                    }
                }
            }
            
            Iterator<Map.Entry<ArrayList, MutableInt>> iter = CandiMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<ArrayList, MutableInt> entry = iter.next();
                if (entry.getValue().intValue() < threshold) {
                    iter.remove();
                }
            }
            
            if (CandiMap.size() > 0) {
                itemsets.add(CandiMap);
            }
            System.out.println(CandiMap);
            key = new ArrayList(CandiMap.keySet());

        } while (key.size() > 1);

        /*
        for (int i=itemsets.size()-1; i>=0; i--) {
            System.out.println(itemsets.get(i));
        //otan ftiaxnw 4->1 oti tessaria exw sto parakatw epipedo ta diagrafw!!!!
        //gia na min eksetazw idies periptwseis!!!!
        }*/
    }
}
