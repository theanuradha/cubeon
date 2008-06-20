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
 * QueryEditor.java
 *
 * Created on Jun 19, 2008, 8:43:12 AM
 */
package org.netbeans.cubeon.local.query.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.cubeon.local.query.LocalQuery;
import org.netbeans.cubeon.local.repository.LocalTaskPriorityProvider;
import org.netbeans.cubeon.local.repository.LocalTaskRepository;
import org.netbeans.cubeon.local.repository.LocalTaskStatusProvider;
import org.netbeans.cubeon.local.repository.LocalTaskTypeProvider;
import org.netbeans.cubeon.tasks.spi.TaskStatus;
import org.netbeans.cubeon.tasks.spi.TaskType;
import org.netbeans.cubeon.tasks.spi.priority.TaskPriority;
import org.netbeans.cubeon.tasks.spi.query.TaskQuery;
import org.netbeans.cubeon.tasks.spi.query.TaskQuerySupportProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author Anuradha
 */
public class QueryEditor extends javax.swing.JPanel implements TaskQuerySupportProvider.ConfigurationHandler {

    private LocalTaskRepository repository;
    private LocalQuery localQuery;
    private final String TAG_ALL = "All";

    /** Creates new form QueryEditor */
    public QueryEditor(TaskQuery query, LocalTaskRepository repository) {
        this.localQuery = query.getLookup().lookup(LocalQuery.class);
        assert query != null;
        this.repository = repository;
        initComponents();
        loadAttributes(repository);
        loadTaskQuery(this.localQuery);

    }

    private void loadTaskQuery(LocalQuery query) {
        txtName.setText(query.getName());
        List<TaskPriority> priorities = query.getPriorities();
        for (TaskPriority tp : priorities) {
            lstPriority.setSelectedValue(tp, false);
        }
        if (priorities.size() == 0) {
            lstPriority.setSelectedValue(TAG_ALL, false);
        }

        List<TaskType> types = query.getTypes();
        for (TaskType type : types) {
            lstType.setSelectedValue(type, false);
        }
        if (types.size() == 0) {
            lstType.setSelectedValue(TAG_ALL, false);
        }
        List<TaskStatus> status = query.getStates();
        for (TaskStatus ts : status) {
            lstStatus.setSelectedValue(ts, false);
        }
        if (status.size() == 0) {
            lstStatus.setSelectedValue(TAG_ALL, false);
        }

        txtContains.setText(query.getContain());
        chkSummery.setSelected(query.isSummary());
        chkDescription.setSelected(query.isDescription());
    }

    private void loadAttributes(LocalTaskRepository repository) {
        DefaultListModel priorityModel = new DefaultListModel();
        priorityModel.addElement(TAG_ALL);
        LocalTaskPriorityProvider ltpp = repository.getLocalTaskPriorityProvider();
        for (TaskPriority priority : ltpp.getTaskPrioritys()) {
            priorityModel.addElement(priority);
        }
        lstPriority.setModel(priorityModel);

        DefaultListModel typeModel = new DefaultListModel();
        LocalTaskTypeProvider lttp = repository.getLocalTaskTypeProvider();
        typeModel.addElement(TAG_ALL);
        for (TaskType type : lttp.getTaskTypes()) {
            typeModel.addElement(type);
        }
        lstType.setModel(typeModel);


        DefaultListModel statusModel = new DefaultListModel();
        LocalTaskStatusProvider ltsp = repository.getLocalTaskStatusProvider();
        statusModel.addElement(TAG_ALL);
        for (TaskStatus status : ltsp.getStatusList()) {
            statusModel.addElement(status);
        }
        lstStatus.setModel(statusModel);




    }

    public TaskQuery getTaskQuery() {
        localQuery.setName(txtName.getText().trim());

        Object[] selectedValues = lstPriority.getSelectedValues();
        List<TaskPriority> prioritys = new ArrayList<TaskPriority>();
        for (Object object : selectedValues) {
            if (object.equals(TAG_ALL)) {
                prioritys.clear();
                break;
            }
            prioritys.add((TaskPriority) object);
        }
        localQuery.setPriorities(prioritys);

        List<TaskStatus> states = new ArrayList<TaskStatus>();
        selectedValues = lstStatus.getSelectedValues();
        for (Object object : selectedValues) {
            if (object.equals(TAG_ALL)) {
                states.clear();
                break;
            }
            states.add((TaskStatus) object);
        }
        localQuery.setStates(states);

        List<TaskType> types = new ArrayList<TaskType>();
        selectedValues = lstType.getSelectedValues();
        for (Object object : selectedValues) {
            if (object.equals(TAG_ALL)) {
                types.clear();
                break;
            }
            types.add((TaskType) object);
        }
        localQuery.setTypes(types);

        localQuery.setContain(txtContains.getText().trim());
        localQuery.setSummary(chkSummery.isSelected());
        localQuery.setDescription(chkDescription.isSelected());
        return localQuery;
    }

    public JComponent getComponent() {
        return this;
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

    public boolean isValidConfiguration() {

        //to-do
        return true;
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
        lblPriority = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstPriority = new javax.swing.JList();
        lblType = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstType = new javax.swing.JList();
        lblStatus = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstStatus = new javax.swing.JList();
        txtContains = new javax.swing.JTextField();
        chkSummery = new javax.swing.JCheckBox();
        chkDescription = new javax.swing.JCheckBox();

        lblName.setText(NbBundle.getMessage(QueryEditor.class, "QueryEditor.lblName.text","-")); // NOI18N

        lblPriority.setText(NbBundle.getMessage(QueryEditor.class, "QueryEditor.lblPriority.text","-")); // NOI18N

        lstPriority.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "All" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstPriority);

        lblType.setText(NbBundle.getMessage(QueryEditor.class, "QueryEditor.lblType.text","-")); // NOI18N

        lstType.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "All" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(lstType);

        lblStatus.setText(NbBundle.getMessage(QueryEditor.class, "QueryEditor.lblStatus.text","-")); // NOI18N

        lstStatus.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "All" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(lstStatus);

        chkSummery.setText(NbBundle.getMessage(QueryEditor.class, "QueryEditor.chkSummery.text","-")); // NOI18N

        chkDescription.setText(NbBundle.getMessage(QueryEditor.class, "QueryEditor.chkDescription.text","-")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkSummery)
                        .addGap(18, 18, 18)
                        .addComponent(chkDescription))
                    .addComponent(txtContains, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPriority, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                                .addGap(63, 63, 63))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblType, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                                .addGap(73, 73, 73))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                                .addGap(71, 71, 71))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPriority)
                    .addComponent(lblType)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane3, 0, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGap(18, 18, 18)
                .addComponent(txtContains, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSummery)
                    .addComponent(chkDescription))
                .addGap(69, 69, 69))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkDescription;
    private javax.swing.JCheckBox chkSummery;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private javax.swing.JList lstPriority;
    private javax.swing.JList lstStatus;
    private javax.swing.JList lstType;
    private javax.swing.JTextField txtContains;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
