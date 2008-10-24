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
 * TracFilterQueryEditor.java
 *
 * Created on Oct 21, 2008, 10:43:44 PM
 */
package org.netbeans.cubeon.trac.query.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.netbeans.cubeon.trac.query.AbstractTracQuery;
import org.netbeans.cubeon.trac.query.TracFilterQuery;
import org.netbeans.cubeon.trac.query.TracQuerySupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class TracFilterQueryEditor extends javax.swing.JPanel implements TaskQuerySupportProvider.ConfigurationHandler {

    private static final long serialVersionUID = -4455036599583142301L;
    private final TracQuerySupport querySupport;
    private TracFilterQuery query;

    /** Creates new form TracFilterQueryEditor */
    public TracFilterQueryEditor(TracQuerySupport querySupport) {
        initComponents();
        this.querySupport = querySupport;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblName1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtQuery = new javax.swing.JEditorPane();
        lblHint = new javax.swing.JLabel();

        lblName.setLabelFor(txtName);
        lblName.setText(NbBundle.getMessage(TracFilterQueryEditor.class, "TracFilterQueryEditor.lblName.text")); // NOI18N

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        lblName1.setLabelFor(txtName);
        lblName1.setText(NbBundle.getMessage(TracFilterQueryEditor.class, "TracFilterQueryEditor.lblName1.text")); // NOI18N

        jScrollPane1.setViewportView(txtQuery);

        lblHint.setText(NbBundle.getMessage(TracFilterQueryEditor.class, "TracFilterQueryEditor.lblHint.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblHint, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addComponent(lblName, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addComponent(lblName1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblName1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private String normalize(String text) {
        return text.replaceAll("\\s*((&)|(=)|(~=)|(^=)|($=)|(!=)|(!~=)|(!^=)|(!$=)|(\\|))\\s*", "$1");
    }

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblName1;
    private javax.swing.JTextField txtName;
    private javax.swing.JEditorPane txtQuery;
    // End of variables declaration//GEN-END:variables

    public void setQuery(TracFilterQuery query) {
        this.query = query;
        txtQuery.setText(query.getQuery());
        txtName.setText(query.getName());
    }

    public TaskQuery getTaskQuery() {
        if (query == null) {
            query = (TracFilterQuery) querySupport.createTaskQuery(AbstractTracQuery.Type.FILTER);
        }
        query.setQuery(normalize(txtQuery.getText()));
        query.setName(txtName.getText());
        return query;
    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    public boolean isValidConfiguration() {
        return true;
    }

    public JComponent getComponent() {
        return this;
    }
}
