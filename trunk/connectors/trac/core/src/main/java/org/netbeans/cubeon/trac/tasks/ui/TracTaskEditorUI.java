/*
 *  Copyright 2008 Anuradha.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

/*
 * TracTaskEditorUI.java
 *
 * Created on Sep 7, 2008, 8:58:15 PM
 */

package org.netbeans.cubeon.trac.tasks.ui;

import org.openide.util.NbBundle;



/**
 *
 * @author Anuradha
 */
public class TracTaskEditorUI extends javax.swing.JPanel {

    /** Creates new form TracTaskEditorUI */
    public TracTaskEditorUI() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtSummary = new javax.swing.JTextField();
        lblCreated = new javax.swing.JLabel();
        lblUpdated = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        lblCreated.setForeground(new java.awt.Color(102, 102, 102));
        lblCreated.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblCreated.text","-")); // NOI18N

        lblUpdated.setForeground(new java.awt.Color(102, 102, 102));
        lblUpdated.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblUpdated.text","-")); // NOI18N

        lblStatus.setForeground(new java.awt.Color(102, 102, 102));
        lblStatus.setText(NbBundle.getMessage(TracTaskEditorUI.class, "TracTaskEditorUI.lblStatus.text","-")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUpdated, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtSummary, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(txtSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUpdated)
                    .addComponent(lblCreated)
                    .addComponent(lblStatus))
                .addContainerGap(326, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblCreated;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblUpdated;
    private javax.swing.JTextField txtSummary;
    // End of variables declaration//GEN-END:variables

}
