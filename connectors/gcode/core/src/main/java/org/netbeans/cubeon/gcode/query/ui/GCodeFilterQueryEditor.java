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
 * GCodeFilterQueryEditor.java
 *
 * Created on Oct 21, 2008, 10:43:44 PM
 */
package org.netbeans.cubeon.gcode.query.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.common.ui.TextValueCompleter;
import org.netbeans.cubeon.gcode.query.AbstractGCodeQuery;
import org.netbeans.cubeon.gcode.query.GCodeFilterQuery;
import org.netbeans.cubeon.gcode.query.GCodeQuerySupport;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class GCodeFilterQueryEditor extends javax.swing.JPanel implements TaskQuerySupportProvider.ConfigurationHandler {

    private static final long serialVersionUID = -4455036599583142301L;
    private final GCodeQuerySupport querySupport;
    private GCodeFilterQuery query;
    private List<String> queryOptions = Arrays.asList(
            "summary:", "description:", "comment:", "status:", "reporter:",
            "owner:", "cc:", "commentby:", "label:", "has:",
            "opened-before", "modified-before",
            "closed-before",
            "opened-after", "modified-after",
            "closed-after", "is:");

    /** Creates new form TracFilterQueryEditor */
    public GCodeFilterQueryEditor(final GCodeQuerySupport querySupport) {
        initComponents();
        this.querySupport = querySupport;
        TextValueCompleter.CallBackFilter callBackFilter = new TextValueCompleter.DefultCallBackFilter() {

            @Override
            public Collection<String> getFilterdCollection(String prifix, Collection<String> completions) {
                List<String> items = new ArrayList<String>();

                if (prifix.contains("label:")) {
                    List<String> labels = querySupport.getTaskRepository().getRepositoryAttributes().getLabels();
                    for (String label : labels) {
                        items.add("label:" + label);
                    }
                    return super.getFilterdCollection(prifix, items);
                }
                if (prifix.contains("status:")) {
                    List<String> statuses = querySupport.getTaskRepository().getRepositoryAttributes().getStatuses();
                    for (String status : statuses) {
                        items.add("status:" + status);
                    }
                    return super.getFilterdCollection(prifix, items);
                }
                if (prifix.contains("is:")) {
                    items.add("is:open");
                    items.add("is:starred");
                    return super.getFilterdCollection(prifix, items);
                }
                if (prifix.contains("has:")) {
                    items.addAll(Arrays.asList(
                            "has:summary", "has:description", "has:comment", "has:status", "has:reporter",
                            "has:owner", "has:cc", "has:commentby", "has:label", "has:attachment"));

                    return super.getFilterdCollection(prifix, items);
                }
                if (prifix.contains("opened-before")) {
                    items.add("opened-before:YYYY/MM/DD");
                    items.add("opened-before:today-N");
                    return super.getFilterdCollection(prifix, items);
                }
                if (prifix.contains("opened-after")) {
                    items.add("opened-after:YYYY/MM/DD");
                    items.add("opened-after:today-N");
                    return super.getFilterdCollection(prifix, items);
                }

                if (prifix.contains("closed-before")) {
                    items.add("closed-before:YYYY/MM/DD");
                    items.add("closed-before:today-N");
                    return super.getFilterdCollection(prifix, items);
                }
                if (prifix.contains("closed-after")) {
                    items.add("closed-after:YYYY/MM/DD");
                    items.add("closed-after:today-N");
                    return super.getFilterdCollection(prifix, items);
                }

                if (prifix.contains("modified-before")) {
                    items.add("modified-before:YYYY/MM/DD");
                    items.add("modified-before:today-N");
                    return super.getFilterdCollection(prifix, items);
                }
                if (prifix.contains("modified-after")) {
                    items.add("modified-after:YYYY/MM/DD");
                    items.add("modified-after:today-N");
                    return super.getFilterdCollection(prifix, items);
                }
                return super.getFilterdCollection(prifix, completions);
            }

            @Override
            public boolean needSeparators(String compelted) {
                return !queryOptions.contains(compelted);
            }
        };
        new TextValueCompleter(queryOptions, txtQuery, " ", callBackFilter);
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
        jScrollPane2 = new javax.swing.JScrollPane();
        lblHint = new javax.swing.JLabel();
        txtQuery = new javax.swing.JTextField();
        lblMaxResultCount = new javax.swing.JLabel();
        txtMaxResultCount = new javax.swing.JTextField();

        lblName.setLabelFor(txtName);
        lblName.setText(NbBundle.getMessage(GCodeFilterQueryEditor.class, "GCodeFilterQueryEditor.lblName.text")); // NOI18N

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        lblName1.setLabelFor(txtName);
        lblName1.setText(NbBundle.getMessage(GCodeFilterQueryEditor.class, "GCodeFilterQueryEditor.lblName1.text")); // NOI18N

        lblHint.setText(NbBundle.getMessage(GCodeFilterQueryEditor.class, "GCodeFilterQueryEditor.lblHint.text")); // NOI18N
        jScrollPane2.setViewportView(lblHint);

        lblMaxResultCount.setText(org.openide.util.NbBundle.getMessage(GCodeFilterQueryEditor.class, "GCodeFilterQueryEditor.lblMaxResultCount.text")); // NOI18N

        txtMaxResultCount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMaxResultCount.setText(org.openide.util.NbBundle.getMessage(GCodeFilterQueryEditor.class, "GCodeFilterQueryEditor.txtMaxResultCount.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addComponent(lblName1)
                    .addComponent(txtQuery, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMaxResultCount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtMaxResultCount, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblName1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtQuery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMaxResultCount)
                    .addComponent(txtMaxResultCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblMaxResultCount;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblName1;
    private javax.swing.JTextField txtMaxResultCount;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtQuery;
    // End of variables declaration//GEN-END:variables

    public void setQuery(GCodeFilterQuery query) {
        this.query = query;
        txtQuery.setText(query.getQuery());
        txtName.setText(query.getName());
        txtMaxResultCount.setText(""+query.getMaxResults());
    }

    public TaskQuery getTaskQuery() {
        if (query == null) {
            query = (GCodeFilterQuery) querySupport.createTaskQuery(AbstractGCodeQuery.Type.FILTER);
        }
        query.setQuery((txtQuery.getText()));
        query.setName(txtName.getText());
        try {
            query.setMaxResults(Integer.parseInt(txtMaxResultCount.getText().trim()));
        } catch (NumberFormatException numberFormatException) {
            //ignore
            query.setMaxResults(GCodeFilterQuery.MAX_RESULTS);
        }
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
