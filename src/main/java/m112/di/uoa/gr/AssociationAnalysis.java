/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m112.di.uoa.gr;

import javax.swing.text.AbstractDocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ppetrou
 */
public class AssociationAnalysis extends javax.swing.JFrame {
    private String dataset;
    private AprioriFrequentItemsetGeneration frequentItemset;
    private List<AprioriRule> rules_all;
    private List<AprioriCandidatesHashTree> trees;
    /**
     * Creates new form AssociationAnalysis
     */
    public AssociationAnalysis() {
        initComponents();
        ((AbstractDocument) jTextField5.getDocument()).setDocumentFilter(
                new MyDocumentFilter());
        ((AbstractDocument) jTextField4.getDocument()).setDocumentFilter(
                new MyDocumentFilter());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Association Analysis");

        jLabel1.setText("File:");

        jButton2.setText("Run Apriori Algorithm");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel8.setText("                                ");

        jLabel9.setText("                                     ");

        jLabel10.setText("Status: ");

        jLabel11.setText("Cofidence: ");

        jTextField4.setText("0.50");

        jTextField5.setText("0.10");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MovieLens 100K Dataset", "MovieLens 1M Dataset", "MovieLens 10M Dataset", "MovieLens Latest Small" }));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        jLabel12.setText(" Apriori Frequent Itemsets");

        jLabel13.setText(" Apriori Association Rules");

        jButton1.setText("Transform into Titles");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel14.setText("Support: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(72, 72, 72)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(24, 24, 24)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(210, 210, 210)
                                .addComponent(jLabel8))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15)
                                .addGap(78, 78, 78)
                                .addComponent(jLabel9)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jLabel14)
                    .addContainerGap(562, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addComponent(jButton2)
                .addGap(9, 9, 9)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(65, 65, 65)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(465, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
//        String dataset=(String) jComboBox2.getSelectedItem();
        //JTextAreaWriter myJTextAreaWriter1 = new JTextAreaWriter();
        //JTextAreaWriter myJTextAreaWriter2 = new JTextAreaWriter();
        
        jTextArea1.setText("");
        jTextArea2.setText("");
        new Thread(){
            @Override public void run() {
                jLabel15.setText("Executing...");
                dataset=(String) jComboBox2.getSelectedItem();
                frequentItemset = new AprioriFrequentItemsetGeneration(Double.parseDouble(jTextField5.getText()));

                double min_cofidence = Double.parseDouble(jTextField4.getText());

                if (dataset.equals("MovieLens 100K Dataset")) {
                    frequentItemset.preprocess(MovieLensDatasetType.ml_100k);
                } else if (dataset.equals("MovieLens 1M Dataset")) {
                    frequentItemset.preprocess(MovieLensDatasetType.ml_1m);
                } else if (dataset.equals("MovieLens 10M Dataset")) {
                    frequentItemset.preprocess(MovieLensDatasetType.ml_10m);
                } else if (dataset.equals("MovieLens Latest Small")) {
                    frequentItemset.preprocess(MovieLensDatasetType.ml_latest_small);
                }

                trees = new ArrayList<AprioriCandidatesHashTree>();
                List<AprioriItemset> itemsetToSearch = new ArrayList<AprioriItemset>();
                boolean flag;
                // iterate over trees
                while (frequentItemset.hasNext()) {

                    AprioriCandidatesHashTree tree = frequentItemset.next();
                    trees.add(tree);
                    // iterate over itemset
                    while (tree.hasNext()) {
                        final AprioriItemset itemset = tree.next();
                        jTextArea2.append(itemset.toString()+"\n");
                    }

                    List<AprioriRule> rules_temp;
                    rules_all=new ArrayList();
                    AprioriAssociationRulesGeneration rules_gen = new AprioriAssociationRulesGeneration(trees, min_cofidence, rules_all);
                    while (rules_gen.hasNext()) {
                        rules_temp=rules_gen.next();
                        for (int i=0; i<rules_temp.size(); i++) {
                            jTextArea1.append(rules_temp.get(i).toString()+"\n");
                        }
                    }
                }
                jLabel15.setText("Done!!!");
            }
        }.start();
        
//        AprioriFrequentItemsetGeneration frequentItemset = new AprioriFrequentItemsetGeneration(Double.parseDouble(jTextField5.getText()));
//        double min_cofidence = Double.parseDouble(jTextField4.getText());
//
//        if (dataset.equals("MovieLens 100K Dataset")) {
//            frequentItemset.preprocess(MovieLensDatasetType.ml_100k);
//        } else if (dataset.equals("MovieLens 1M Dataset")) {
//            frequentItemset.preprocess(MovieLensDatasetType.ml_1m);
//        } else if (dataset.equals("MovieLens 10M Dataset")) {
//            frequentItemset.preprocess(MovieLensDatasetType.ml_10m);
//        } else if (dataset.equals("MovieLens Latest Small")) {
//            frequentItemset.preprocess(MovieLensDatasetType.ml_latest_small);
//        }
//
//        List<AprioriCandidatesHashTree> trees = new ArrayList<AprioriCandidatesHashTree>();
//        List<AprioriItemset> itemsetToSearch = new ArrayList<AprioriItemset>();
//        boolean flag;
//        // iterate over trees
//        while (frequentItemset.hasNext()) {
//
//            AprioriCandidatesHashTree tree = frequentItemset.next();
//            trees.add(tree);
//            // iterate over itemset
//            while (tree.hasNext()) {
//                final AprioriItemset itemset = tree.next();
//                jTextArea2.append(itemset.toString()+"\n");
//            }
//
//            List<AprioriRule> rules_temp;
//            List<AprioriRule> rules_all=new ArrayList();
//            AprioriAssociationRulesGeneration rules_gen = new AprioriAssociationRulesGeneration(trees, min_cofidence, rules_all);
//            while (rules_gen.hasNext()) {
//                rules_temp=rules_gen.next();
//                for (int i=0; i<rules_temp.size(); i++) {
//                    jTextArea1.append(rules_temp.get(i).toString()+"\n");
//                }
//            }
//        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextArea1.setText("");
        jTextArea2.setText("");
        new Thread() {
            @Override
            public void run() {
                jLabel15.setText("Transforming...");
                HashMap<Integer, String> title = frequentItemset.items;
                String entitled_itemset;
                int ktree = 0;
                while (ktree < trees.size()) {
                    while (trees.get(ktree).hasNext()) {
                        AprioriItemset current_itemset = trees.get(ktree).next();
                        entitled_itemset = "";
                        for (int i = 0; i < current_itemset.getItems().length; i++) {
                            int itemset_temp[] = current_itemset.getItems();
                            if (i == 0) {
                                entitled_itemset = entitled_itemset + title.get(itemset_temp[i]);
                            } else {
                                entitled_itemset = entitled_itemset + ", " + title.get(itemset_temp[i]);
                            }
                        }
                        jTextArea2.append("Itemset=" + entitled_itemset + ", Support=" + current_itemset.getSupport() + "\n");
                    }
                    ktree++;
                }

                String entitled_itemset_body;
                for (int i = 0; i < rules_all.size(); i++) {
                    List<RuleElement> rule_temp = rules_all.get(i).rules;
                    for (int j = 0; j < rule_temp.size(); j++) {
                        //log.debug(rule_temp.get(j));
                        int head_temp[] = rule_temp.get(j).getHead();
                        int body_temp[] = rule_temp.get(j).getBody();

                        entitled_itemset = "";
                        for (int x = 0; x < head_temp.length; x++) {
                            if (x == 0) {
                                entitled_itemset = entitled_itemset + title.get(head_temp[x]);
                            } else {
                                entitled_itemset = entitled_itemset + ", " + title.get(head_temp[x]);
                            }
                        }
                        entitled_itemset_body = "";
                        for (int x = 0; x < body_temp.length; x++) {
                            if (x == 0) {
                                entitled_itemset_body = entitled_itemset_body + title.get(body_temp[x]);
                            } else {
                                entitled_itemset_body = entitled_itemset_body + ", " + title.get(body_temp[x]);
                            }
                        }
                        jTextArea1.append("Rule=" + entitled_itemset + " -> " + entitled_itemset_body + " Rule Confidence=" + rule_temp.get(j).getRule_confidence() + "\n");
                    }
                }
                jLabel15.setText("Done!!!");
            }
        }.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AssociationAnalysis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AssociationAnalysis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AssociationAnalysis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AssociationAnalysis.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AssociationAnalysis().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

}
