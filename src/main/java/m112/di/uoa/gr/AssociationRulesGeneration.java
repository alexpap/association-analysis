package m112.di.uoa.gr;

import java.util.*;

/**
 *
 * @author ppetrou
 */

public class AssociationRulesGeneration {

    public class Node {
        private TreeSet<Integer> list_left;
        private TreeSet<Integer> list_right;
    }

    private int last, last2;
    private TreeSet<Integer> itemset;
    private Node node;
    
    public void apriori_gen(List<Node> CandidateRules, int k) {

        if (k > 1 & CandidateRules.size()>=2) {

            node = new Node();
            ArrayList<Node> new_CandidateRules = new ArrayList<Node>();

            for (int i = 0; i <= CandidateRules.size() - k; i++) {

                for (int j = i + 1; j < CandidateRules.size(); j++) {
                    last = CandidateRules.get(j).list_right.last();
                    last2 = CandidateRules.get(i).list_right.last();

                    node.list_left = new TreeSet<Integer>(CandidateRules.get(i).list_left);
                    node.list_right = new TreeSet<Integer>(CandidateRules.get(i).list_right);

                    if (CandidateRules.get(i).list_right.headSet(last2).containsAll(CandidateRules.get(j).list_right.headSet(last))) {
                        node.list_left.addAll(CandidateRules.get(j).list_left);
                        node.list_right.add(last);
                    } else {
                        node = new Node();
                        break;
                    }

                    node.list_left.removeAll(node.list_right);

                    new_CandidateRules.add(node);

                    node = new Node();
                }
            }
            /*
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println(k);
            for (int i = 0; i < new_CandidateRules.size(); i++) {
                System.out.println(new_CandidateRules.get(i).list_left + " -> " + new_CandidateRules.get(i).list_right);
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            */
            k--;

            CandidateRules.clear();
            apriori_gen(new_CandidateRules, k);
        }
    }

    public AssociationRulesGeneration(TreeSet itemset) {
        this.itemset=itemset;
        initialize();
    }

    public void initialize() {

        List<Node> CandidateRules = new ArrayList<Node>();
        int k = itemset.size();

        Iterator<Integer> iterator = itemset.iterator();
        int i_temp;

        while (iterator.hasNext()) {
            node = new Node();
            node.list_left=new TreeSet<Integer>(itemset);
            i_temp = iterator.next();
            node.list_left.remove(i_temp);
            
            node.list_right=new TreeSet<Integer>();
            node.list_right.add(i_temp);

            CandidateRules.add(node);           
        }
        /*
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(k);
        for (int i = 0; i < CandidateRules.size(); i++) {
            System.out.println(CandidateRules.get(i).list_left + " -> " + CandidateRules.get(i).list_right);
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        */
        k--;

        apriori_gen(CandidateRules, k);
    }
}
